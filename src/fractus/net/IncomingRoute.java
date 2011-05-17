package fractus.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import org.apache.log4j.Logger;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

public class IncomingRoute {
	private final static Logger log;
	static {
		log = Logger.getLogger(IncomingRoute.class);
	}
	
	private ServerSocket serverSocket;
    private boolean useUpnp;
    private Integer outsidePort;
    private Integer insidePort;
    private String insideIP;
    private String outsideIP;
    private InternetGatewayDevice ign;
    private boolean mapped = false;
	
	public IncomingRoute() {
		
	}
	
    public void establishIncomingRoute()
    throws IOException {
        // Try to bind to 80, then 8080, then ten random ports
        Random g = new Random();
        int[] port = { 80, 8080, rn(g), rn(g), rn(g), rn(g), rn(g), rn(g),
        		rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g) };
        boolean canBindToPort = false;
        for (int i = 0; i < port.length; i++) {
            if (newSocket(port[i])) {
                log.info("Bound to " + port[i]);
                canBindToPort = true;
                break;
            }
        }
        if (!canBindToPort) {
            log.warn("Could not bind to any port.");
            throw new IOException("Cannot bind to any port.  Proxy support necessary.");
        }

        log.debug("Determining interface addresses");
        String localAddress = null;
        serverSocket.getInetAddress().getHostAddress();
        try {
            localAddress = getLocalIP();
        } catch (SocketException e) {
            log.warn("NetListener: got socket exception while determining local address", e);
            return;
        }
        log.debug("Local address is: " + localAddress);
        if (isPrivateAddress(localAddress)) {
            log.info("Detected address resides on private network.  Will attempt UPnP");
            log.info("Trying to forward to " + localAddress + ":" + serverSocket.getLocalPort());
            try {
                if (useUpnp(localAddress, serverSocket.getLocalPort())) {
                    useUpnp = true;
                    log.info("UPnP success");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UPNPResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    

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

    public String getUpnpLoc() {
        if (useUpnp) {
            return outsideIP;
        } else {
            return insideIP;
        }
    }

    public String getUpnpPort() {
        if (useUpnp) {
            return Integer.toString(outsidePort);
        } else {
            return Integer.toString(insidePort);
        }
    }

    public String getIP() {
        return insideIP;
    }

    public int getPort() {
        return insidePort;
    }
    
    public void closeUpnp() {
    	if (!mapped) {
    		return;
    	}
        boolean unmapped = false;
        try {
                unmapped = ign.deletePortMapping(null, outsidePort, "TCP");
        } catch (IOException e) {
            log.debug("IO Exception while attempting to close UPnP");
            e.printStackTrace();
        } catch (UPNPResponseException e) {
            log.debug("UPNPResponseException while trying to close UPnP");
            e.printStackTrace();
        }
        if (unmapped && mapped) {
            log.debug("Port unmapped");
        }
    }

    public boolean useUpnp(String localHostIP, int port) throws IOException, UPNPResponseException {
        log.debug("Attempting UPnP. Finding gateways.");
        InternetGatewayDevice[] devices = InternetGatewayDevice.getDevices(2500);
        if (devices == null) {
            log.info("No UPNP device responded before the timeout.");
            return false;
        }
        log.debug("Found " + devices.length + " gateway(s)");
        Random g = new Random();
        int[] upnpport = {80, 8080, rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g), rn(g)};
        if (devices != null) {
            for (int i = 0; i < devices.length; i++) {
                log.debug("Trying to map ports from gateway device " + devices[i].getIGDRootDevice().getModelName());
                for (int j = 0; j < upnpport.length; j++) {
                    log.debug("Trying to map port " + upnpport[j] + " from device" + devices[i].getIGDRootDevice().getModelName());
                    mapped = devices[i].addPortMapping("Fractus",
                            null, port, upnpport[j],
                            localHostIP, 0, "TCP");
//					boolean mappedudp = devices[i].addPortMapping( "Fractus listener", 
//							null, port, upnpport[j],
//							localHostIP, 0, "UDP" );
                    if (mapped) {//&& mappedudp ) {
                        ign = devices[i];
                        outsideIP = devices[i].getExternalIPAddress();
                        outsidePort = upnpport[j];
                        log.debug("External port " + outsideIP + ":" + upnpport[j] + " mapped to " + localHostIP + ":" + port);
                        return true;
                    } else {
                        log.debug("External port " + upnpport[j] + " failed to map to " + localHostIP + ":" + port);
                    }
                }

            }
        }
        return false;
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
    
    private boolean newSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
            insidePort = port;
            insideIP = getLocalIP();
            return true;
        } catch (IOException e) {
        	log.debug("Unable to bind to " + port, e);
        }
        return false;
    }
	
	private boolean isPrivateAddress(String addr) {
        return (addr.startsWith("10.")
                || addr.startsWith("172.")
                || addr.startsWith("192."));
    }
	
    private int rn(Random g) {
        return g.nextInt(64511) + 1024;
    }
}
