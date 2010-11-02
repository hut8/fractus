package fractus.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.*;

import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import fractus.net.EncryptionManager;
import fractus.net.PacketHandler;
import fractus.gui.CredentialsFrame;
import fractus.gui.ViewManager;
import fractus.net.KeyPublisher;
import fractus.net.PublicKeyDirectory;
import fractus.net.RouteManager;
import fractus.net.RoutePublisher;
import fractus.net.ServerConnection;
import fractus.net.UserCredentials;

public class Fractus
        implements
        FractusController {

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
    private ViewManager viewManager;
    private java.beans.PropertyChangeSupport propertyChangeSupport;
    private KeyPublisher keyPublisher;
    private RoutePublisher routePublisher;

    public ContactManager getContactManager() {
        return contactManager;
    }
    public static final String version = "Fractus 0.2a";
    private Executor executor;

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

        log.debug("Creating View Manager");
        viewManager = new ViewManager(this);
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

        try {
            log.debug("Trying to set Look and Feel to native...");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            log.debug("Success");
        } catch (Throwable t) {
            log.warn("Unable to set UI - reverting to fugly interface.");
        }
        log.debug("Creating Fractus object");
        Fractus fractus = new Fractus();
        log.debug("Finished Fractus Constructor");
        Thread routeThread = new Thread(fractus.routeManager);
        routeThread.start();
        fractus.promptForCredentials();
    }

    private void promptForCredentials() {
        log.debug("Prompting for credentials...");
        CredentialsFrame cf = CredentialsFrame.getInstance();
        cf.setFractus(this);
        cf.pack();
        cf.setLocationRelativeTo(null);
        cf.setVisible(true);
    }

    @Override
    public void shutdown() {
        Runtime.getRuntime().exit(0);
    }

    

    /**
     * Set credentials and server location
     * @param serverAddress
     * @param port
     * @param username
     * @param password
     */
    @Override
    public void initialize(String serverAddress,
            Integer port,
            String username,
            String password) {
        log.debug("Setting new credentials");
        setCredentials(new UserCredentials(username, password));
        log.debug("Creating server connector");
        serverConnection = new ServerConnection(serverAddress, port, encryptionManager);
        new Thread(serverConnection, "Server Connection").start();
        keyPublisher = new KeyPublisher(userCredentials, serverConnection);
        new Thread(keyPublisher, "Key Publisher").start();
        publicKeyDirectory = new PublicKeyDirectory(serverConnection);
        packetHandler = new PacketHandler();
        ShutdownProcedure sh = new ShutdownProcedure(routeManager.getNetListener(), serverConnection);
        Runtime.getRuntime().addShutdownHook(sh);

    }

    public void setCredentials(UserCredentials userCredentials) {
        UserCredentials oldUc = this.userCredentials;
        this.userCredentials = userCredentials;
        propertyChangeSupport.firePropertyChange("userCredentials",
                oldUc, userCredentials);
    }
}
