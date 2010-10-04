package us.fract.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;
import us.fract.main.*;

public class ServerConnection
        implements Runnable {

    private Logger log;
    private ExceptionHandler exceptionHandler;
    private InetSocketAddress address;
    private EncryptionManager em;
    private Object sockMutex;
    private HashMap<String, Callback> callbacks;
    private PacketHandler packetHandler;
    private UserCredentials uc;
    private final ConcurrentLinkedQueue<FractusMessage> queue;
    private Delegate<DelegateMethod<EventData>, EventData> signOnDelegate;
    private String hostname;

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
        this.callbacks = callbacks;
        sockMutex = new Object();
        packetHandler = new PacketHandler(callbacks, null);
        log = Logger.getLogger(this.getClass().getName());
        queue = new ConcurrentLinkedQueue<FractusMessage>();
        signOnDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
    }

    public Delegate<DelegateMethod<EventData>, EventData> getSignOnDelegate() {
        return signOnDelegate;
    }

    public void process(final FractusMessage message) {
        log.info("Adding message for processing on queue");
        queue.add(message);
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    private String syncProcess(final FractusMessage message)
            throws IOException,
            ProtocolException,
            NumberFormatException,
            GeneralSecurityException {
        // Create actual connection with streams
        Logger.getAnonymousLogger().log(Level.INFO, "Connecting to server at " + address);
        Socket s = new Socket();
        s.connect(address);
        InputStream input = s.getInputStream();
        OutputStream output = s.getOutputStream();
        Logger.getAnonymousLogger().log(Level.INFO, "Connected successfully");

        Logger.getAnonymousLogger().log(Level.INFO, "Negotiating headers with " + address);
        // Transfer headers
        Headers sendHeaders = new Headers();
        sendHeaders.writeHeaders(output, em);
        Headers recvHeaders = Headers.receive(input);

        // Make crypto data
        PeerCryptoData pcd = PeerCryptoData.negotiate(recvHeaders, em);
        Logger.getAnonymousLogger().log(Level.INFO, "Cryptographic parameters established with " + address);
        FractusPacket fp = null;
        Logger.getAnonymousLogger().log(Level.INFO, "Sending message: " + message.serialize());
        synchronized (sockMutex) {
            try {
                output.write(
                        new FractusPacket(
                        message.serialize().getBytes("UTF-8"),
                        pcd.getSecretKeySpec()).serialize());
            } catch (InvalidKeyException e) {
                Logger.getAnonymousLogger().log(Level.WARNING, "error: realized key was invalid while serializing packet");
                e.printStackTrace();
            }

            fp = new FractusPacket(pcd.getSecretKeySpec());
            fp.readPacket(input, pcd.getEncodedKey(), em);
        }
        String replyMessage = packetHandler.handle(fp);
        Logger.getAnonymousLogger().log(Level.INFO, "Received reply from " + address + ": " + replyMessage);
        s.close();
        return replyMessage;
    }

    public void signOn(RouteManager rm) throws ParserConfigurationException {
        signOnDelegate.invoke(new EventData());
        String encodedKey = em.getEncodedKey();
        Logger.getAnonymousLogger().log(Level.INFO,
                "Attempting sign on with public key " + encodedKey);

        FractusMessage m = new FractusMessage();
        Element el = m.getDocumentElement();
        Element newElement2 = m.getDocument().createElement("register-key");
        newElement2.setAttribute("key", encodedKey);
        el.appendChild(newElement2);

        this.addRegisterLocation(rm, m);

        Element newElement = m.getDocument().createElement("send-contact-data");
        el.appendChild(newElement);

        m.setUserCredentials(uc);
        process(m);
    }

    public String identifyKey(String key)
            throws ParserConfigurationException,
            GeneralSecurityException,
            IOException,
            ProtocolException {
        FractusMessage m = new FractusMessage();
        Element el = m.getDocumentElement();
        Element newElement = m.getDocument().createElement("identify-key");
        newElement.setAttribute("key", key);
        el.appendChild(newElement);
        m.setUserCredentials(uc);
        return syncProcess(m);
    }

    public void addRegisterLocation(RouteManager rm, FractusMessage m) {
        String address = rm.getAddress();
        String port = rm.getPort().toString();
        String upnpaddr = rm.getUpnpAddress();
        String upnpport = rm.getUpnpPort();
        System.out.println("Registering location: " + address + ":" + port);
        System.out.println("Registering upnp location: " + upnpaddr + ":" + upnpport);
        Element el = m.getDocumentElement();
        Element newElement = m.getDocument().createElement("register-location");
        newElement.setAttribute("address", address);
        newElement.setAttribute("port", port.toString());
        el.appendChild(newElement);

        if (!address.equals(upnpaddr)) {
            Element regUpnpElm = m.getDocument().createElement("register-location");
            regUpnpElm.setAttribute("address", upnpaddr);
            regUpnpElm.setAttribute("port", upnpport);
            el.appendChild(regUpnpElm);
        }
    }

    public void registerLocation(RouteManager rm) throws ParserConfigurationException {
        FractusMessage m = new FractusMessage();
        addRegisterLocation(rm, m);
        m.setUserCredentials(uc);
        process(m);
    }

    public void invalidLocation(RouteManager rm) {
        String address = rm.getAddress();
        String port = rm.getPort().toString();
        String upnpaddr = rm.getUpnpAddress();
        String upnpport = rm.getUpnpPort();
        System.out.println("unRegistering location: " + address + ":" + port);
        System.out.println("unRegistering upnp location: " + upnpaddr + ":" + upnpport);
        FractusMessage m = null;
        try {
            m = new FractusMessage();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }
        Element el = m.getDocumentElement();
        Element newElement = m.getDocument().createElement("invalidate-location");
        newElement.setAttribute("address", address);
        newElement.setAttribute("port", port.toString());
        el.appendChild(newElement);

        if (!address.equals(upnpaddr)) {
            Element newElement2 = m.getDocument().createElement("invalidate-location");
            newElement2.setAttribute("address", upnpaddr);
            newElement2.setAttribute("port", upnpport);
            el.appendChild(newElement2);
        }
        m.setUserCredentials(uc);
        try {
            process(m);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // First resolve hostname
        try {
            address = new InetSocketAddress(InetAddress.getByName(hostname), port);
        } catch (UnknownHostException e) {
            log.severe("Cannot resolve server hostname: server connection dying");
            if (exceptionHandler != null) {
                exceptionHandler.handle(this, e);
            }
            return;
        }

        // Consume incoming messages
        for (;;) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            FractusMessage fm = queue.remove();
            try {
                syncProcess(fm);
            } catch (GeneralSecurityException e) {
                log.warning("Could not communicate with server due to security exception: " + e.getMessage());
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
