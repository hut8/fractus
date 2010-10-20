package us.fract.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;


import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

import us.fract.connection.FractusConnector;
import us.fract.main.Fractus;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;

public class NetListener
implements Runnable {
        private ServerSocket serverSocket;
	private static Logger log;
	private boolean useUpnp;
	private Integer outsidePort;
	private Integer insidePort;
	private String insideIP;
	private String outsideIP;
	private InternetGatewayDevice ign;
	private boolean mapped = false;
        private EncryptionManager encryptionManager;
        private PacketHandler packetHandler;

	
	public ArrayList<Location> getListeningLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		if (insideIP != null && insidePort != null) {
			locations.add(new Location(insideIP, insidePort));
		}
		if (useUpnp) {
			locations.add(new Location(outsideIP, outsidePort));
		}
		return locations;
	}

	public NetListener(EncryptionManager encryptionManager, PacketHandler packetHandler) {
		super();
                this.encryptionManager = encryptionManager;
                this.packetHandler = packetHandler;
		log = Logger.getLogger(this.getClass().getName());
	}

	public Integer getListenPort() {
		if (serverSocket == null || !serverSocket.isBound()) {
			return null;
		}
		return serverSocket.getLocalPort();
	}

	private int rn(Random g) {
		return g.nextInt(64511)+1024;
	}

	private boolean newSocket(int port) {
		try {
			serverSocket = new ServerSocket(port);
			insidePort = port;
			insideIP = getLocalIP();

			//server attempts to connect here, if it fails continue to upnp

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return false;
	}

	public boolean useUpnp(String localHostIP, int port) throws IOException, UPNPResponseException  {
		Logger.getAnonymousLogger().log(Level.INFO,"NetListener: attempting UPnP. finding gateways.");
		InternetGatewayDevice[] devices = InternetGatewayDevice.getDevices( 2500 );
		if (devices == null) {
			log.info("No UPNP device responded before the timeout.");
			return false;
		}
		Logger.getAnonymousLogger().log(Level.INFO,"NetListener: found " + devices.length + " gateway(s)");
		Random g = new Random();
		int[] upnpport = {80, 8080, rn(g), rn(g), rn(g), rn(g)
				, rn(g), rn(g), rn(g), rn(g), rn(g), rn(g)
				, rn(g), rn(g), rn(g)};
		if ( devices != null ) {
			for ( int i = 0; i < devices.length; i++ ) {
				Logger.getAnonymousLogger().log(Level.INFO, "NetListener: trying to map ports from gateway device " + devices[i].getIGDRootDevice().getModelName() );
				for(int j = 0; j < upnpport.length; j++) {
					Logger.getAnonymousLogger().log(Level.INFO,"NetListener: trying to map port " + upnpport[j] + " from device" + devices[i].getIGDRootDevice().getModelName());
					mapped  = devices[i].addPortMapping( "Fractus", 
							null, port, upnpport[j],
							localHostIP, 0, "TCP" );
//					boolean mappedudp = devices[i].addPortMapping( "Fractus listener", 
//							null, port, upnpport[j],
//							localHostIP, 0, "UDP" );
					if ( mapped ) {//&& mappedudp ) {
						ign = devices[i];
						outsideIP = devices[i].getExternalIPAddress();
						outsidePort = upnpport[j];
						Logger.getAnonymousLogger().log(Level.INFO, "NetListener: external port "+outsideIP+":"+upnpport[j]+" mapped to " + localHostIP + ":" + port);
						return true;
					} else {
						Logger.getAnonymousLogger().log(Level.INFO, "NetListener: external port "+upnpport[j]+" failed to map to " + localHostIP + ":" + port);
					}
				}

			}
		}
		return false;
	}

	public void closeUpnp() {
		boolean unmapped = false;
		try {
			if(mapped)
				unmapped = ign.deletePortMapping( null, outsidePort, "TCP" );
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.INFO,"NetListener: IO Exception while attempting to close UPnP");
			e.printStackTrace();
		} catch (UPNPResponseException e) {
			Logger.getAnonymousLogger().log(Level.INFO,"NetListener: UPNPResponseException while trying to close UPnP");
			e.printStackTrace();
		}
		if ( unmapped && mapped) {
			Logger.getAnonymousLogger().log(Level.INFO, "Port unmapped" );
		}
	}


	private boolean isPrivateAddress(String addr) {
		return (addr.startsWith("10.")      ||
				addr.startsWith("172.") ||
				addr.startsWith("192."));
	}
	

	public boolean establishIncomingRoute() {
		/* we will try to bind to 80, then 8080, then ten random ports */
		Random g = new Random();
		int[] port = {80, 8080, rn(g), rn(g), rn(g), rn(g)
				, rn(g), rn(g), rn(g), rn(g), rn(g), rn(g)
				, rn(g), rn(g), rn(g)};
		
		/* try to bind to these ports in order */
		boolean canBindToPort = false;
		for(int i = 0; i < port.length; i++){
			if(newSocket(port[i])) {
				Logger.getAnonymousLogger().log(Level.INFO,"NetListener: bound to " + port[i]);
				canBindToPort = true;
				break;
			}
		}
		if (!canBindToPort) {
			Logger.getAnonymousLogger().log(Level.INFO,"NetListener: could not bind to any port.");
			/* no point in continuing or trying UPnP */
			return false;
		}
		
		/* we have bound to some port. */
		
		/* TODO: determine if private address */
		Logger.getAnonymousLogger().log(Level.INFO,"NetListener: checking local address");
		String localAddress = null;
		serverSocket.getInetAddress().getHostAddress();
		try {
			localAddress = getLocalIP();
		} catch (SocketException e) {
			Logger.getAnonymousLogger().log(Level.INFO,"NetListener: got socket exception while determining local address");
			e.printStackTrace();
			return false;
		}
		Logger.getAnonymousLogger().log(Level.INFO,"NetListener: local address is " + localAddress);
		if (isPrivateAddress(localAddress)) {
			Logger.getAnonymousLogger().log(Level.INFO,"NetListener: that address is in a private network.  should attempt UPnP...");
			try {
				if(useUpnp(localAddress,serverSocket.getLocalPort())) {
					useUpnp = true;
					Logger.getAnonymousLogger().log(Level.INFO,"upnp success");
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UPNPResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/* see if it can be reached from server. */
//		Logger.getAnonymousLogger().log(Level.INFO,"NetListener: seeing if server can connect to us");
		
		return true;
		
	}
	
	public String getLocalIP() throws SocketException {
		Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
		while (ifaces != null && ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
			while (iaddresses.hasMoreElements()) {
				InetAddress iaddress = iaddresses.nextElement();
				if (java.net.Inet4Address.class.isInstance(iaddress)) {
					if ((!iaddress.isLoopbackAddress()) && (!iaddress.isLinkLocalAddress())) {
						return iaddress.getHostAddress();
					}
				}
			}
		}
		return "127.0.0.1";
	}
	
	public void run() {
		while(true){
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				Logger.getAnonymousLogger().log(Level.INFO,"After accept");
				FractusConnector newconnection =
                                        new FractusConnector(clientSocket,
						encryptionManager);
				new Thread(newconnection).start();
			} catch (IOException e) {
				Logger.getAnonymousLogger().log(Level.INFO,"Client AcceptConnection failed");
			}
		}
	}

	public String getUpnpLoc()  {
		if(useUpnp)
			return outsideIP;
		else
			return insideIP;
	}

	public String getUpnpPort() {
		if(useUpnp)
			return Integer.toString(outsidePort);
		else
			return Integer.toString(insidePort);
	}
	
	public String getIP() {
		return insideIP;
	}
	
	public int getPort() {
		return insidePort;
	}

}
