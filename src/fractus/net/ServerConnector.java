package fractus.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.xml.parsers.ParserConfigurationException;

import fractus.delegate.Delegate;
import fractus.delegate.DelegateMethod;
import fractus.delegate.EventData;
import fractus.main.*;

import org.apache.log4j.*;

/**
 * Provides methods to interact with server functionality
 * Uses FractusConnector to connect.
 * @author 14581
 *
 */
public class ServerConnector
implements Runnable {
	private FractusConnector connector;
	private Thread connectorThread;
	private InetSocketAddress address;
	private String hostname;
	private Integer port;

	private static Logger log;
	static {
		log = Logger.getLogger(ServerConnector.class.getName());
	}

	public ServerConnector(String hostname, Integer port) {
		this.hostname = hostname;
		this.port = port;
	}

	// Key management
	public void registerKey(String keyEncoding, byte[] encodedKey) {

	}
	public void unregisterKey(String keyEncoding, byte[] encodedKey) {

	}
	public String identifyKey(byte[] encodedKey) {
		ProtocolBuffer.IdentifyKeyReq request = ProtocolBuffer.IdentifyKeyReq.newBuilder().build();
		return null;
	}

	// Contact management
	public void getContactData() {

	}
	public void addContact() {

	}
	public void removeContact() {

	}
	public void getNonreciprocalContacts() {

	}

	// Location management
	public void registerLocation(List<Inet4Address> locations) {
		log.debug("Constructing Register Location Request");
		ProtocolBuffer.RegisterLocationReq registerLocationReq = ProtocolBuffer.RegisterLocationReq.newBuilder().addLocationList(ProtocolBuffer.Location.newBuilder().build()).build();
		FractusMessage registerLocationMessage = FractusMessage.build(registerLocationReq);
	}
	public void unregisterLocation(List<Inet4Address> locations) {

	}

	private void sendMessage(FractusMessage message) {
		synchronized (this) {
			while (this.connector == null) {
				try { wait(); } catch (InterruptedException e) { }
			}	
		}
		this.connector.sendMessage(message);
	}

	/**
	 * Resolves and connects to server using FractusConnector.
	 */
	@Override
	public void run() {
		log.info("Server Connection thread alive");
		log.debug("Resolving hostname of server: " + this.address.getHostName());
		address = new InetSocketAddress(this.hostname, this.port);

		log.info("Resolved server hostname to: " + address.getAddress().getHostAddress());
		log.debug("Creating socket");
		Socket socket = new Socket();
		try {
			log.debug("Connecting to Server...");
			socket.connect(address);
		} catch (IOException ex) {
			log.warn("Error: Could not connect to server", ex);
			return;
		}
		log.debug("Creating FractusConnector for server");
		connector = new FractusConnector(socket);
		Thread connectorThread = new Thread(connector, "ServerConnection FractusConnector");
		connectorThread.start();
		synchronized (this) {
			notifyAll();	
		}

	}
}
