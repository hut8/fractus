package us.fract.main;

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


import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;
import us.fract.gui.CredentialsFrame;
import us.fract.gui.ViewManager;
import us.fract.net.KeyPublisher;
import us.fract.net.PublicKeyDirectory;
import us.fract.net.RouteManager;
import us.fract.net.RoutePublisher;
import us.fract.net.ServerConnection;
import us.fract.net.UserCredentials;

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
    private ServerConnection sc;
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
        return sc;
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
        sc = new ServerConnection(userCredentials, serverAddress, port, encryptionManager);
        new Thread(sc, "Server Connection").start();
        keyPublisher = new KeyPublisher();
        publicKeyDirectory = new PublicKeyDirectory(sc);
        packetHandler = new PacketHandler();
        ShutdownProcedure sh = new ShutdownProcedure(routeManager.getNetListener(), sc);
        Runtime.getRuntime().addShutdownHook(sh);
    }

    public void setCredentials(UserCredentials userCredentials) {
        UserCredentials oldUc = this.userCredentials;
        this.userCredentials = userCredentials;
        propertyChangeSupport.firePropertyChange("userCredentials",
                oldUc, userCredentials);
    }
}
