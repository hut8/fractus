package fractus.net;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;



import org.apache.log4j.Logger;

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

    private NetListener netListener;
    private EncryptionManager encryptionManager;
    private PacketHandler packetHandler;
    private String address;
    private int port;
    private static Logger log;
    static {
        log = Logger.getLogger(RouteManager.class.getName());
    }

    public RouteManager(PacketHandler packetHandler) {
        this.encryptionManager = EncryptionManager.getInstance();
        this.packetHandler = packetHandler;
        established = false;
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

    @Override
    public void run() {
        //start netlistener
        new Thread(netListener, "Network Listener").start();
        log.info("Finding route to self");
        findRouteToSelf();
        log.info("Done finding route to self");
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
