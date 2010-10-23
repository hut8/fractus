package us.fract.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.ParserConfigurationException;

import us.fract.connection.EncryptionManager;
import us.fract.main.*;

import org.apache.log4j.*;
import us.fract.connection.FractusConnector;
import us.fract.protobuf.ProtocolBuffer;

public class ServerConnection
        implements Runnable {
    private FractusConnector connector;
    private ExceptionHandler exceptionHandler;
    private EncryptionManager em;
    private ConcurrentLinkedQueue<FractusMessage> sendQueue;
    private Object sockMutex;
    private UserCredentials uc;
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

    public ServerConnection(UserCredentials uc,
            String hostname,
            Integer port,
            EncryptionManager em) {
        this.uc = uc;
        this.hostname = hostname;
        this.port = port;
        this.em = em;
        sendQueue = new ConcurrentLinkedQueue<FractusMessage>();
        sockMutex = new Object();
        signOnDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
    }

    public Delegate<DelegateMethod<EventData>, EventData> getSignOnDelegate() {
        return signOnDelegate;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void registerKey(byte[] key) {
        log.debug("Constructing Register Key Request");
        ProtocolBuffer.RegisterKeyReq registerKeyReq =
                ProtocolBuffer.RegisterKeyReq
                .newBuilder()
                .setUsername(uc.getUsername())
                .setPassword(uc.getPassword())
                .build();
        FractusMessage registerKeyMessage =
                FractusMessage
                .build(registerKeyReq);

    }

    public void registerLocation(RouteManager routeManager) {
        
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
            if (exceptionHandler != null) {
                exceptionHandler.handle(this, e);
            }
            return;
        }
        log.debug("Resolved server hostname to: " + address.getAddress().getHostAddress());

        // Wait for the 

        log.debug("Connection thread finished");
    }
}
