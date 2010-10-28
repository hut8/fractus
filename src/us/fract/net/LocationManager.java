package us.fract.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.NoSuchPaddingException;
import org.apache.log4j.Logger;



/**
 * Tracks individual FractusConnections to a remote node
 * @author bowenl2
 */

public class LocationManager {
	private EncryptionManager encryptionManager;
        private PacketHandler packetHandler;
        private PublicKeyDirectory publicKeyDirectory;
        final private HashSet<FractusConnector> connections;
	private static Logger log;
        static {
            log = Logger.getLogger(LocationManager.class.getName());
        }


	public LocationManager(EncryptionManager encryptionManager,
                PacketHandler packetHandler,
                PublicKeyDirectory publicKeyDirectory) {
            this.encryptionManager = encryptionManager;
            this.packetHandler = packetHandler;
            this.publicKeyDirectory = publicKeyDirectory;
            this.connections = new HashSet<FractusConnector>();
	}
	
	public FractusConnector[] getConnections() {
		FractusConnector[] conArr = new FractusConnector[0];
		connections.toArray(conArr);
		return conArr;
	}

	public void removeConnection(FractusConnector c) {
		synchronized(connections) {
			log.info("Connection " + c.toString() + " removed");
			connections.remove(c);
		}
	}

	public synchronized void addConnection(FractusConnector connection) {
		if (connections.contains(connection)) {
			return;
		}
		log.info("Connection added at " + connection.toString());
		connections.add(connection);
	}

	public synchronized void addConnection(String address, int port) {
            

            //		FractusConnector connection = null;
//		try {
//			connection = new FractusConnector(address, port,
//                                encryptionManager, publicKeyDirectory);
//			Logger.getAnonymousLogger().log(Level.INFO,"location manager: addConnection: connection added at " + connection.toString());
//			connections.add(connection);
//		} catch(Exception e) {
//			log.warning("location invalid: "+address+":"+port);
//		}
	}

	public void sendPacket(FractusPacket m) {
		
		Iterator<FractusConnector> cItr = connections.iterator();
		while (cItr.hasNext()) {
			FractusConnector c = cItr.next();
		}
	}


	public boolean isReachable() {
		/* rule: need either origin or two proxies */
		int proxiesReachable = 0;
		Iterator<FractusConnector> cItr = connections.iterator();
		while(cItr.hasNext()) {
			FractusConnector c = cItr.next();
                        // TODO: fix isAlive!!!
			return true; //c.isAlive();
		}
		return proxiesReachable >= 2;
	}
}
