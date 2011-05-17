package fractus.main;

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
	private static Logger log;
	static {
		log = Logger.getLogger(Fractus.class.getName());
	}
	private ServerConnector serverConnection;
	private IncomingRoute incomingRoute;
	private PublicKeyDirectory publicKeyDirectory;
	private RoutePublisher routePublisher;
	private Thread routePublisherThread;
	private ContactManager contactManager;
	private UserCredentials userCredentials;
	private java.beans.PropertyChangeSupport propertyChangeSupport;
	private GuiManager guiManager;

	public Fractus() throws
	IOException,
	GeneralSecurityException,
	ParserConfigurationException {
		log.info("Fractus Client");
		propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		log.debug("Creating Encryption Manager");
		EncryptionManager.getInstance();
		log.debug("Creating Contact Manager");
		contactManager = new ContactManager();
		log.debug("Creating Incoming Route manager");
		this.incomingRoute = new IncomingRoute();
		routePublisherThread = new Thread(this.routePublisher);
		routePublisherThread.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
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
		new Fractus();
		GuiManager.getInstance().main();
	}

	public void shutdown() {
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
}
