package us.fract.net;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;


import us.fract.connection.FractusConnector;

import javax.xml.parsers.*;
import org.apache.log4j.Logger;
import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;

public class RouteManager
        implements Runnable {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private final Object serverConnectionMutex;

    private NetListener netListener;
    private HashSet<FractusConnector> proxies;
    private ServerConnection serverConnector;
    private EncryptionManager encryptionManager;
    private PacketHandler packetHandler;
    private String address;
    private int port;
    private static Logger log;
    static {
        Logger.getLogger(RouteManager.class.getName());
    }

    public RouteManager(EncryptionManager encryptionManager,
            PacketHandler packetHandler) {
        serverConnectionMutex = new Object();
        this.encryptionManager = encryptionManager;
        this.packetHandler = packetHandler;
        established = false;
        proxies = new HashSet<FractusConnector>();
    }

    public void setServerConnector(ServerConnection serverConnector) {
        this.serverConnector = serverConnector;
        synchronized(serverConnectionMutex) {
            this.serverConnectionMutex.notifyAll();
        }
    }
    

    private void requestProxy() {

    }

    private void addProxy() {
        
    }

    /** finds a route to this specific node
     * requests proxies and stores them if necessary
     */
    public boolean findRouteToSelf() {
        log.debug("Finding route to self");
        /* create NetListener */
        netListener = new NetListener(encryptionManager, packetHandler);
        /* make NetListener attempt to accept connections
         * (if necessary, negotiating UPnP) */
        if (netListener.establishIncomingRoute()) {
            log.debug("RouteManager: established direct incoming route.  Starting listen thread...");
            address = netListener.getIP();
            port = netListener.getPort();
            setEstablished(true);
            return true;
        }
        /* if it cannot, try requesting proxies */
        return false;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String getUpnpAddress() {
        return netListener.getUpnpLoc();
    }

    public String getUpnpPort() {
        return netListener.getUpnpPort();
    }

    public void run() {
        //start netlistener
        new Thread(netListener).start();
        findRouteToSelf();

        // Wait for route to be found
        synchronized (serverConnectionMutex) {
            while (serverConnector == null) {
                log.info("No server connection data.  Waiting...");
                try {
                    serverConnectionMutex.wait();
                } catch (InterruptedException ex) { }
                log.info("Awoken.  Checking for server connection.");
            }
            log.info("Server Connection Received.");
        }

        try {
            serverConnector.signOn(this);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//		while(true) {
//			Logger.getAnonymousLogger().log(Level.INFO,"RouteManager: updating location...");
//				try {
//					//registerLocation();
//					Thread.sleep(870000);
//				}
//				catch (InterruptedException e) { return; }
//				catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}
    }

    public NetListener getNetListener() {
        return netListener;
    }
    
    protected boolean established;

    public boolean isEstablished() {
        return established;
    }

    private void setEstablished(boolean established) {
        boolean old = this.established;
        this.established = established;
        propertyChangeSupport.firePropertyChange("isEstablished", old, established);
    }
}
