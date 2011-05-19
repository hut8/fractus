package fractus.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractus.crypto.EncryptionManager;
import fractus.crypto.PublicKeyDirectory;
import fractus.gui.GuiManager;
import fractus.net.IncomingRoute;
import fractus.net.PacketHandler;
import fractus.net.RoutePublisher;
import fractus.net.ServerConnector;
import fractus.net.UserCredentials;

public class Fractus {
	private static Fractus instance;
	private static Logger log;
	static {
		log = Logger.getLogger(Fractus.class.getName());
	}
	public static Fractus getInstance() {
		return instance;
	}
	
	private ServerConnector serverConnection;
	private Thread serverConnectionThread;
	private IncomingRoute incomingRoute;
	private PublicKeyDirectory publicKeyDirectory;
	private RoutePublisher routePublisher;
	private Thread routePublisherThread;
	private ContactManager contactManager;
	private UserCredentials userCredentials;
	private PropertyChangeSupport propertyChangeSupport;

	private Fractus() throws
	IOException,
	GeneralSecurityException,
	ParserConfigurationException {
		log.info("Fractus Client");
		propertyChangeSupport = new PropertyChangeSupport(this);
		log.debug("Initializing Encryption Manager");
		EncryptionManager.getInstance().initialize();
		log.debug("Creating Contact Manager");
		contactManager = new ContactManager();
		log.debug("Creating Incoming Route manager");
		this.incomingRoute = new IncomingRoute();
		routePublisherThread = new Thread(this.routePublisher);
		routePublisherThread.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
	}
	
	public void initialize(String username, String password, String serverAddress, Integer port) {
		createCredentials(username, password);
		
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public ServerConnector getServerConnection() {
		return serverConnection;
	}

	public static void main(String[] args)
	throws UnknownHostException,
	IOException,
	GeneralSecurityException,
	ParserConfigurationException {
		PropertyConfigurator.configure("lib/log4j.properties");
		Fractus.instance = new Fractus();
		GuiManager.getInstance().main();
	}

	public void shutdown() {
		// TODO: Shutdown threads individually
		Runtime.getRuntime().exit(0);
	}

	public void createServerConnection(String serverAddress, Integer port) {
		log.debug("Creating server connector");
		this.serverConnection = new ServerConnector(serverAddress, port);
		log.debug("Creating Server Connection Thread");
		new Thread(serverConnection, "Server Connection").start();
		this.publicKeyDirectory = new PublicKeyDirectory(serverConnection);
	}

	public void createCredentials(String username, String password) {
		setCredentials(new UserCredentials(username, password));
	}

	public void setCredentials(UserCredentials userCredentials) {
		UserCredentials oldUc = this.userCredentials;
		this.userCredentials = userCredentials;
		propertyChangeSupport.firePropertyChange("userCredentials",
				oldUc, userCredentials);
	}

	public void addPropertyChangeListener(String string,
			PropertyChangeListener propertyChangeListener) {
		this.propertyChangeSupport.addPropertyChangeListener(string, propertyChangeListener);
	}
}
