package us.fract.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.HashMap;


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
    private InetSocketAddress address;
    private EncryptionManager em;
    private Object sockMutex;
    private UserCredentials uc;
    private Delegate<DelegateMethod<EventData>, EventData> signOnDelegate;
    private String hostname;
    
    private static Logger log;
    static {
        log = Logger.getLogger(ServerConnection.class.getName());
    }

    public String getHostname() {
        return hostname;
    }

    private Integer port;
    public Integer getPort() {
        return port;
    }

    public ServerConnection(UserCredentials uc,
            String hostname,
            Integer port,
            HashMap<String, Callback> callbacks,
            EncryptionManager em) {
        this.uc = uc;
        this.hostname = hostname;
        this.port = port;
        this.em = em;
        sockMutex = new Object();
        signOnDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
    }

    public Delegate<DelegateMethod<EventData>, EventData> getSignOnDelegate() {
        return signOnDelegate;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    public void signOn(RouteManager rm) throws ParserConfigurationException {
        signOnDelegate.invoke(new EventData());

        log.debug("Constructing Register Key Request");
        ProtocolBuffer.RegisterKeyReq registerKeyReq = ProtocolBuffer.RegisterKeyReq.newBuilder()
                .setUsername(uc.getUsername())
                .setPassword(uc.getPassword())
                .build();
        FractusMessage registerKeyMessage = FractusMessage.build(registerKeyReq);

        log.debug("Constructing Register Location Request");
        ProtocolBuffer.RegisterLocationReq registerLocationReq = ProtocolBuffer
                .RegisterLocationReq
                .newBuilder()
                .addLocationList(ProtocolBuffer.Location.newBuilder().build())
                .build();
        FractusMessage registerLocationMessage = FractusMessage.build(registerLocationReq);

        String encodedKey = em.getEncodedKey();
        log.info("Attempting sign on with public key " + encodedKey);
    }

    public String identifyKey(String key)
            throws ParserConfigurationException,
            GeneralSecurityException,
            IOException,
            ProtocolException {
        ProtocolBuffer.IdentifyKeyReq request = ProtocolBuffer
                .IdentifyKeyReq
                .newBuilder()
                .build();
        return null;
        
    }

    public void addRegisterLocation(RouteManager rm, FractusMessage m) {
        String upnpaddr = rm.getUpnpAddress();
        String upnpport = rm.getUpnpPort();
        System.out.println("Registering location: " + address + ":" + port);
        System.out.println("Registering upnp location: " + upnpaddr + ":" + upnpport);
    }

    public void registerLocation(RouteManager rm) throws ParserConfigurationException {
        
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
        log.debug("Resolving hostname of server: " + hostname);
        // First resolve hostname
        try {
            address = new InetSocketAddress(InetAddress.getByName(hostname), port);
        } catch (UnknownHostException e) {
            log.warn("Cannot resolve server hostname: server connection dying");
            if (exceptionHandler != null) {
                exceptionHandler.handle(this, e);
            }
            return;
        }

        

        log.debug("Connection thread finished");
    }
}
