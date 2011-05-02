package fractus.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fractus.gui.GuiManager;
import fractus.net.EncryptionManager;
import fractus.net.KeyPublisher;
import fractus.net.PacketHandler;
import fractus.net.PublicKeyDirectory;
import fractus.net.RouteManager;
import fractus.net.RoutePublisher;
import fractus.net.ServerConnection;
import fractus.net.UserCredentials;

public class Fractus {

	private static Logger log;
	static {
		log = Logger.getLogger(Fractus.class.getName());
	}
	private PacketHandler packetHandler;
	private PublicKeyDirectory publicKeyDirectory;
	private RouteManager routeManager;
	private ServerConnection serverConnection;
	private ContactManager contactManager;
	private UserCredentials userCredentials;
	private java.beans.PropertyChangeSupport propertyChangeSupport;
	private RoutePublisher routePublisher;
	private GuiManager guiManager;

	public ContactManager getContactManager() {
		return contactManager;
	}
	private Executor executor;

	public Fractus() throws
	IOException,
	GeneralSecurityException,
	ParserConfigurationException {
		log.info("Fractus Client");

		propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
		executor = Executors.newFixedThreadPool(5);
		log.debug("Initializing Encryption Manager");
		EncryptionManager.getInstance().initialize(executor);

		log.debug("Creating Contact Manager");
		contactManager = new ContactManager(packetHandler, publicKeyDirectory);

		log.debug("Creating Route Manager");
		routeManager = new RouteManager(packetHandler);

		ShutdownProcedure sh = new ShutdownProcedure(routeManager.getNetListener(), serverConnection);
		Runtime.getRuntime().addShutdownHook(sh);
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public PacketHandler getPacketHandler() {
		return packetHandler;
	}

	public ServerConnection getServerConnection() {
		return serverConnection;
	}

	public RouteManager getRouteManager() {
		return routeManager;
	}

	public static void main(String[] args)
	throws UnknownHostException,
	IOException,
	GeneralSecurityException,
	ParserConfigurationException {
		PropertyConfigurator.configure("lib/log4j.properties");

		log.debug("Creating Fractus object");
		Fractus fractus = new Fractus();
		log.debug("Finished Fractus Constructor");
		Thread routeThread = new Thread(fractus.routeManager);
		routeThread.start();
		
		GuiManager.getInstance().main();
	}

	public void shutdown() {
		Runtime.getRuntime().exit(0);
	}

	public void createServerConnection(String serverAddress, Integer port) {
		log.debug("Creating server connector");
		this.serverConnection = new ServerConnection(serverAddress, port);
		log.debug("Creating Server Connection Thread");
		new Thread(serverConnection, "Server Connection").start();
		publicKeyDirectory = new PublicKeyDirectory(serverConnection);
		packetHandler = new PacketHandler();
	}

	public void createCredentials(String username, String password) {
		log.debug("Creating new credentials");
		setCredentials(new UserCredentials(username, password));
	}

	public void setCredentials(UserCredentials userCredentials) {
		UserCredentials oldUc = this.userCredentials;
		this.userCredentials = userCredentials;
		propertyChangeSupport.firePropertyChange("userCredentials",
				oldUc, userCredentials);
	}
}
