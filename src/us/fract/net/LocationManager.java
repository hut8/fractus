package us.fract.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import us.fract.connection.ClientConnector;
import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;

public class LocationManager {
	private EncryptionManager encryptionManager;
        private PacketHandler packetHandler;
        private PublicKeyDirectory publicKeyDirectory;
        final private HashSet<ClientConnector> connections;
	private Logger log;

	public LocationManager(EncryptionManager encryptionManager,
                PacketHandler packetHandler,
                PublicKeyDirectory publicKeyDirectory) {
            this.encryptionManager = encryptionManager;
            this.packetHandler = packetHandler;
            this.publicKeyDirectory = publicKeyDirectory;
            log = Logger.getLogger(this.getClass().getName());
		connections = new HashSet<ClientConnector>();
	}
	
	public ClientConnector[] getConnections() {
		ClientConnector[] conArr = new ClientConnector[0];
		connections.toArray(conArr);
		return conArr;
	}

	public void removeConnection(ClientConnector c) {
		synchronized(connections) {
			Logger.getAnonymousLogger().log(Level.INFO,"connection " + c.toString() + " removed");
			connections.remove(c);
		}
	}

	public synchronized void addConnection(ClientConnector connection) {
		if (connections.contains(connection)) {
			return;
		}
		Logger.getAnonymousLogger().log(Level.INFO,"connection added at " + connection.toString());
		connections.add(connection);
	}

	public synchronized void addConnection(String address, int port) {
		ClientConnector connection = null;
		try {
			connection = new ClientConnector(address, port,
                                encryptionManager,
				packetHandler, publicKeyDirectory);
			Logger.getAnonymousLogger().log(Level.INFO,"location manager: addConnection: connection added at " + connection.toString());
			connection.start();
			connections.add(connection);
		} catch(Exception e) {
			log.warning("location invalid: "+address+":"+port);
		}

	}

	public void sendPacket(FractusPacket m) {
		Logger.getAnonymousLogger().log(Level.INFO,"LocationManager: sending XML document...");
		Iterator<ClientConnector> cItr = connections.iterator();
		while (cItr.hasNext()) {
			ClientConnector c = cItr.next();
			Logger.getAnonymousLogger().log(Level.INFO,"LocationManager: Now sending XML document to " + c);
			try {
				c.sendMessage(m);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public boolean isReachable() {
		/* rule: need either origin or two proxies */
		int proxiesReachable = 0;
		Iterator<ClientConnector> cItr = connections.iterator();
		while(cItr.hasNext()) {
			ClientConnector c = cItr.next();
			return c.isAlive();
		}
		return proxiesReachable >= 2;
	}
}
