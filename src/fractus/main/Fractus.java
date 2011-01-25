package fractus.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.*;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.trolltech.qt.QThread;
import com.trolltech.qt.gui.QApplication;

import fractus.net.EncryptionManager;
import fractus.net.PacketHandler;
import fractus.net.KeyPublisher;
import fractus.net.PublicKeyDirectory;
import fractus.net.RouteManager;
import fractus.net.RoutePublisher;
import fractus.net.ServerConnection;
import fractus.net.UserCredentials;
import fractus.ui.BuddyTab;
import fractus.ui.CredentialsDialog;
import fractus.ui.DemoThread;
import fractus.ui.IMWindow;
import fractus.ui.UIManager;

public class Fractus {

    private static Logger log;
    static {
        log = Logger.getLogger(Fractus.class.getName());
    }
    private EncryptionManager encryptionManager;
    private PacketHandler packetHandler;
    private PublicKeyDirectory publicKeyDirectory;
    private RouteManager routeManager;
    private ServerConnection serverConnection;
    private ContactManager contactManager;
    private UserCredentials userCredentials;
    private java.beans.PropertyChangeSupport propertyChangeSupport;
    private KeyPublisher keyPublisher;
    private RoutePublisher routePublisher;

    public ContactManager getContactManager() {
        return contactManager;
    }
    public static final String version = "Fractus 0.3b";
    private Executor executor;
	private String username;
	private UIManager winman;
	private CredentialsDialog creds;
	

    public Fractus() throws
    IOException,
    GeneralSecurityException,
    ParserConfigurationException {
        log.info("Fractus Client");

        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        executor = Executors.newFixedThreadPool(5);
        log.debug("Creating Encryption Manager");
        encryptionManager = new EncryptionManager();
        encryptionManager.initialize(executor);

        log.debug("Creating Contact Manager");
        contactManager = new ContactManager(encryptionManager, packetHandler, publicKeyDirectory);

        log.debug("Creating Route Manager");
        routeManager = new RouteManager(encryptionManager, packetHandler);

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

    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public RouteManager getRouteManager() {
        return routeManager;
    }

    /**
     * @param args
     * @throws IOException s
     * @throws UnknownHostException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws ParserConfigurationException
     */
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
        QApplication.initialize(args);
        
        fractus.promptForCredentials(fractus);
        QApplication.exec();
    }
    
    
    public void login(String username, String password, String server) {
    	this.username = username;
    	System.out.println("login: "+username+"@"+server);
    	//TODO login to server
    	creds.hide();
    	
    	winman = new UIManager(this);
    	
	    
	    //uncomment this to run the ui demo thread
	    Runnable t = new DemoThread(this);
	    QThread thread = new QThread(t);
	    thread.start();
	    
    }
    
    public UIManager getWinman() {
    	return winman;
    }
    
        
    public void sendMessage(String buddy, String message) {
    	System.out.println("send("+buddy+"): "+message);
    	//TODO actually send message
    }
    
    public void receiveMessage(String buddy, String message) {
    	System.out.println("receive("+buddy+"): "+message);
    	winman.receiveMessage(buddy, message);
    }
    
    public String getme() {
    	return username;
    }

    private void promptForCredentials(Fractus fractus) {
        log.debug("Prompting for credentials...");
        creds = new CredentialsDialog(fractus);
	    creds.show();
	    creds.raise();
    }

    public void shutdown() {
        Runtime.getRuntime().exit(0);
    }

    public void createServerConnection(String serverAddress, Integer port) {
        log.debug("Creating server connector");
        this.serverConnection = new ServerConnection(serverAddress, port, encryptionManager);
        log.debug("Creating Server Connection Thread");
        new Thread(serverConnection, "ServerConnection").start();
        publicKeyDirectory = new PublicKeyDirectory(serverConnection);
        packetHandler = new PacketHandler();
        createKeyPublisher();
    }

    private void createKeyPublisher() {
        keyPublisher = new KeyPublisher(userCredentials, serverConnection);
        new Thread(keyPublisher, "Key Publisher").start();
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
