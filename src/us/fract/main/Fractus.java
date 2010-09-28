package us.fract.main;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.*;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;
import us.fract.gui.ContactListPanel;
import us.fract.gui.CredentialsFrame;
import us.fract.gui.MainFrame;
import us.fract.gui.OverviewPanel;
import us.fract.gui.ViewManager;
import us.fract.net.CallbackFactory;
import us.fract.net.PublicKeyDirectory;
import us.fract.net.RouteManager;
import us.fract.net.ServerConnection;
import us.fract.net.UserCredentials;

public class Fractus
        implements
        FractusController,
        ExceptionHandler {
    private Logger log;
    private EncryptionManager encryptionManager;
    private PacketHandler packetHandler;
    private PublicKeyDirectory publicKeyDirectory;
    private RouteManager routeManager;
    private ServerConnection sc;
    private ContactManager contactManager;
    private UserCredentials userCredentials;
    private ViewManager viewManager;
    private java.beans.PropertyChangeSupport propertyChangeSupport;

    public ContactManager getContactManager() {
        return contactManager;
    }
    public static final String version = "Fractus 0.2a";
    private Executor executor;

    public Fractus() throws
            IOException,
            GeneralSecurityException,
            ParserConfigurationException {
        log = Logger.getLogger(this.getClass().getName());
        log.info("Fractus Client");

        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                JOptionPane.showMessageDialog(viewManager.getMainFrame(),
                        t.getName() + ":" + e.getLocalizedMessage());
            }
        };

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

        executor = Executors.newFixedThreadPool(5);
        encryptionManager = new EncryptionManager();
        encryptionManager.initialize(executor);
        contactManager = new ContactManager(encryptionManager, packetHandler, publicKeyDirectory);
        routeManager = new RouteManager(encryptionManager, packetHandler);
        new Thread(routeManager).start();

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
     * Basic document object
     * @return A blank document
     */
    public static Document getDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
            return null;
        }
        DOMImplementation impl = builder.getDOMImplementation();
        return impl.createDocument(null, "fractus", null);
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            System.err.println("Unable to set UI - reverting to fugly interface.");
            t.printStackTrace();
        }
        Fractus fractus = new Fractus();
        fractus.promptForCredentials();
    }

    private void promptForCredentials() {
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

    @Override
    public void initialize(String serverAddress,
            Integer port,
            String username,
            String password) {
            CredentialsFrame.getInstance().setVisible(false);
        setCredentials(new UserCredentials(username, password));

        sc = new ServerConnection(
                userCredentials, serverAddress, port,
                CallbackFactory.getServerCallbackMap(contactManager), encryptionManager);

        sc.setExceptionHandler(this);
        new Thread(sc, "ServerConnection").start();
        routeManager.setServerConnector(sc);
        publicKeyDirectory = new PublicKeyDirectory(sc);
        packetHandler = new PacketHandler(CallbackFactory.getClientCallbackMap(contactManager), publicKeyDirectory);
        
        ShutdownProcedure sh = new ShutdownProcedure(routeManager.getNetListener(), sc);
        Runtime.getRuntime().addShutdownHook(sh);
    }

    public void setCredentials(UserCredentials userCredentials) {
        UserCredentials oldUc = this.userCredentials;
        this.userCredentials = userCredentials;
        propertyChangeSupport.firePropertyChange("userCredentials",
                oldUc, userCredentials);
    }

    @Override
    public void handle(Object source, Exception e) {
        if (source.getClass().equals(ServerConnection.class)) {
            final ServerConnection sc = (ServerConnection) source;
            JOptionPane.showMessageDialog(viewManager.getMainFrame(), e.getLocalizedMessage());
            // TODO: Warning dialog

        }
    }
}
