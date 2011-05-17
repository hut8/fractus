package fractus.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.NoSuchPaddingException;
import org.apache.log4j.Logger;

import fractus.crypto.PublicKeyDirectory;



/**
 * Tracks individual FractusConnections to a remote node
 * @author bowenl2
 */

public class LocationManager {
	final private Set<FractusConnector> connections;
	private static Logger log;
	static {
		log = Logger.getLogger(LocationManager.class.getName());
	}

	public LocationManager() {
		this.connections = new HashSet<FractusConnector>();
	}


	public void removeConnection(FractusConnector c) {
		synchronized(connections) {
			log.info("Connection " + c.toString() + " removed");
			connections.remove(c);
		}
	}

	public synchronized void addConnection(String address, int port) {

	}

	public void sendPacket(FractusPacket m) {
	}


	public boolean isReachable() {
		//		/* rule: need either origin or two proxies */
		//		int proxiesReachable = 0;
		//		Iterator<FractusConnector> cItr = connections.iterator();
		//		while(cItr.hasNext()) {
		//			FractusConnector c = cItr.next();
		//                        // TODO: fix isAlive!!!
		//			return true; //c.isAlive();
		//		}
		//		return proxiesReachable >= 2;
		return false;
	}
}
