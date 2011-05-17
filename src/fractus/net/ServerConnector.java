package fractus.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.xml.parsers.ParserConfigurationException;

import fractus.delegate.Delegate;
import fractus.delegate.DelegateMethod;
import fractus.delegate.EventData;
import fractus.main.*;

import org.apache.log4j.*;

public class ServerConnector
        implements Runnable {
    private FractusConnector connector;
    private InetSocketAddress address;
    
    private static Logger log;

    static {
        log = Logger.getLogger(ServerConnector.class.getName());
    }

    public ServerConnector(String hostname, Integer port) {
        address = InetSocketAddress.createUnresolved(hostname, port);
    }

    // Key management
    public void registerKey(String keyEncoding, byte[] encodedKey) {
    	
    }
    public void unregisterKey(String keyEncoding, byte[] encodedKey) {
    	
    }
    public String identifyKey(byte[] encodedKey) {
        ProtocolBuffer.IdentifyKeyReq request = ProtocolBuffer.IdentifyKeyReq.newBuilder().build();
        return null;
    }
    
    // Contact management
    public void getContactData() {
    	
    }
    public void addContact() {
    	
    }
    public void removeContact() {
    	
    }
    public void getNonreciprocalContacts() {
    	
    }

    // Location management
    public void registerLocation(List<Inet4Address> locations) {
        log.debug("Constructing Register Location Request");
        ProtocolBuffer.RegisterLocationReq registerLocationReq = ProtocolBuffer.RegisterLocationReq.newBuilder().addLocationList(ProtocolBuffer.Location.newBuilder().build()).build();
        FractusMessage registerLocationMessage = FractusMessage.build(registerLocationReq);
    }
    public void unregisterLocation(List<Inet4Address> locations) {

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
