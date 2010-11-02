package fractus.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.xml.parsers.ParserConfigurationException;

import fractus.main.*;

import org.apache.log4j.*;

public class ServerConnection
        implements Runnable {
    private FractusConnector connector;
    private EncryptionManager em;
    private final ConcurrentLinkedQueue<FractusMessage> messsageQueue;
    private Delegate<DelegateMethod<EventData>, EventData> signOnDelegate;
    private InetSocketAddress address;
    private String hostname;

    public String getHostname() {
        return hostname;
    }
    private Integer port;

    public Integer getPort() {
        return port;
    }
    private static Logger log;

    static {
        log = Logger.getLogger(ServerConnection.class.getName());
    }

    public ServerConnection(String hostname,
            Integer port,
            EncryptionManager em) {
        this.hostname = hostname;
        this.port = port;
        this.em = em;
        messsageQueue = new ConcurrentLinkedQueue<FractusMessage>();
        signOnDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
    }

    public void sendMessage(FractusMessage fractusMessage) {
        messsageQueue.add(fractusMessage);
        synchronized(messsageQueue) {
            messsageQueue.notifyAll();
        }
    }

    public Delegate<DelegateMethod<EventData>, EventData> getSignOnDelegate() {
        return signOnDelegate;
    }

    public void signOn(RouteManager rm) {
        log.debug("Constructing Register Location Request");
        ProtocolBuffer.RegisterLocationReq registerLocationReq = ProtocolBuffer.RegisterLocationReq.newBuilder().addLocationList(ProtocolBuffer.Location.newBuilder().build()).build();
        FractusMessage registerLocationMessage = FractusMessage.build(registerLocationReq);

        String encodedKey = em.getEncodedKey();
        log.info("Attempting sign on with public key " + encodedKey);
    }

    public String identifyKey(String key)
            throws ParserConfigurationException,
            GeneralSecurityException,
            IOException,
            ProtocolException {
        ProtocolBuffer.IdentifyKeyReq request = ProtocolBuffer.IdentifyKeyReq.newBuilder().build();
        return null;
    }

    public void addRegisterLocation(RouteManager rm, FractusMessage m) {
        String upnpaddr = rm.getUpnpAddress();
        String upnpport = rm.getUpnpPort();
        log.debug("Registering location: " + address + ":" + port);
        log.debug("Registering UPnP location: " + upnpaddr + ":" + upnpport);
    }

    public void invalidLocation(RouteManager rm) {
        String address = rm.getAddress();
        String port = rm.getPort().toString();
        String upnpaddr = rm.getUpnpAddress();
        String upnpport = rm.getUpnpPort();
    }

    @Override
    public void run() {
        log.debug("Server Connection thread alive");
        // Resolve hostname
        try {
            log.debug("Resolving hostname of server: " + hostname);
            address = new InetSocketAddress(InetAddress.getByName(hostname), port);
        } catch (UnknownHostException e) {
            log.warn("Cannot resolve server hostname: server connection dying");
            return;
        }
        log.debug("Resolved server hostname to: " + address.getAddress().getHostAddress());

        while (true) {
            log.debug("Waiting for message");

            while (messsageQueue.isEmpty()) {
                try {
                    synchronized(messsageQueue) {
                        messsageQueue.wait();
                    }
                } catch (InterruptedException ex) {
                    log.debug("Interrupted in queue.");
                }
            }

            log.debug("Creating socket");
            Socket socket = new Socket();
            try {
                log.debug("Connecting to Server...");
                socket.connect(address);
            } catch (IOException ex) {
                log.error("Error: Could not connect to server", ex);
            }

            log.debug("Creating FractusConnector for server");
            connector = new FractusConnector(socket, em);
            Thread connectorThread = new Thread(connector, "ServerConnection FractusConnector");
            connectorThread.start();

            // Take out one from queue and send it
            FractusMessage message = messsageQueue.remove();
            log.debug("Sending Fractus Message to Connector");
            try {
                connector.sendMessage(message);
            } catch (IllegalBlockSizeException ex) {
                log.error("Encountered illegal block size exception with server", ex);
            } catch (BadPaddingException ex) {
                log.error("Encountered bad padding exception with server", ex);
            } catch (IOException ex) {
                log.error("Encountered IO Exception with server", ex);
            }
        }
    }
}