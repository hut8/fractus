package us.fract.connection;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;


import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

import us.fract.main.*;
import us.fract.net.FractusMessage;
import us.fract.net.FractusPacket;
import us.fract.net.PublicKeyDirectory;
import us.fract.protobuf.ProtocolBuffer;

public class FractusConnector
        implements Runnable {
    private static Logger log;
    static {
        log = Logger.getLogger(FractusConnector.class.getName());
    }
    private Delegate<DelegateMethod<ClientConnectionEventData>, ClientConnectionEventData> connectionStateChangeDelegate;
    private InetSocketAddress address;
    private String addressString;
    private Integer remotePort;
    private EncryptionManager em;
    private InputStream input;
    private OutputStream output;
    private PacketHandler handler;
    private SecretKeySpec sks;
    private byte[] remotePublicKey;
    private String encodedPublicKey;
    private Socket socket;
    private ArrayList<FractusPacket> queue;
    private Thread consumer;
    private boolean initialized = false;
    private PublicKeyDirectory publicKeyDirectory;


    //////////////////////////////

    private final Object sendMutex;
    private ClientCipher clientCipher;

    public FractusConnector(Socket socket, EncryptionManager em) {
        this.socket = socket;
        this.em = em;
        sendMutex = new Object();
    }

    public void disconnect() {
        log.info("Disconnecting");
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    private void connectStreams() {
        try {
            output = socket.getOutputStream();
            input = socket.getInputStream();
        } catch (IOException e) {
            log.warn("Could not get streams from socket", e);
            disconnect();
        }
    }

    private void publishCipherData() throws IOException {
        // Make Message
        ProtocolBuffer.PublicKey pk =
                ProtocolBuffer.PublicKey.newBuilder().setEncoding(em.getEncodingFormat()).setPublicKey(ByteString.copyFrom(em.getPublicKey())).build();

        // Make FractusMessage
        FractusMessage fm = FractusMessage.build(pk);
        FractusPacket fp = new FractusPacket(fm.getSerialized());

        // Write serialized cipher data to client
        output.write(fp.serialize());
    }

    private void negotiateCipherData() throws IOException, GeneralSecurityException {
        // Get the packet
        log.debug("Negotiating symmetric key (waiting for remote side)");
        FractusPacket fp = FractusPacket.read(input);
        if (fp == null) {
            log.warn("Unable to retrieve remote public key.  Disconnecting.");
            disconnect(); return;
        }
        ProtocolBuffer.PublicKey remotePKPB = ProtocolBuffer.PublicKey.parseFrom(fp.getContents());

        String remotePKEncoding = remotePKPB.getEncoding();
        byte[] remotePKData = remotePKPB.getPublicKey().toByteArray();

        clientCipher = new ClientCipher(remotePKEncoding, remotePKData, em);
    }

    @Override
    public void run() {
        log.info("ClientConnector alive");
        connectStreams();
        try {
            publishCipherData();
        } catch (IOException ex) {
            log.warn("Could not publish cipher data to remote client", ex);
            disconnect();
            return;
        }

        try {
            negotiateCipherData();
        } catch (IOException ex) {
            log.warn("Could not receive cipher data from remote client", ex);
            disconnect();
            return;
        } catch (GeneralSecurityException ex) {
            log.warn("Encountered security exception while negotiating key", ex);
            disconnect(); return;
        }

        serveConnection();
    }

    private void serveConnection() {
        log.info("Entering main client service loop");
        while (socket.isConnected()) {
            log.debug("Waiting for packet");
            FractusPacket fp;
            try {
                fp = FractusPacket.read(input);
            } catch (IOException ex) {
                log.info("Remote host disconnected", ex);
                disconnect(); return;
            }

            if (fp == null) {
                log.info("Received null packet.  Disconnecting.");
                disconnect(); return;
            }

            log.debug("Received packet");
            
        }
    }

    public void sendMessage(FractusMessage message) throws
            IllegalBlockSizeException, BadPaddingException, IOException {
        log.debug("ClientConnector will send FractusMessage: " + message.getDescriptorName());
        byte[] plainText = message.getSerialized();
        byte[] cipherText = clientCipher.encrypt(plainText);
        FractusPacket sendPacket = new FractusPacket(cipherText);

        synchronized(sendMutex) {
            output.write(sendPacket.serialize());
        }
    }

    private void createConsumer() {
        consumer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (consumer) {
                            log.info("Pausing to be awoken to process message...");
                            consumer.wait();
                        }
                    } catch (InterruptedException e) {
                        log.info("Consumer awoken");
                    }

                    synchronized (queue) {
                        while (!FractusConnector.this.queue.isEmpty()) {
                            FractusPacket fp = FractusConnector.this.queue.remove(0);
                            try {
                                output.write(fp.serialize());
                            } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // This could be fixed
                                FractusConnector.this.queue.add(0, fp);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        consumer.start();
    }

//    @Override
//    public void run() {
//        // Connect our socket
//        if (!socket.isConnected()) {
//            try {
//                InetAddress addr;
//                try {
//                    addr = InetAddress.getByName(addressString);
//                } catch (UnknownHostException e) {
//                    log.warn("Cannot find host " + addressString);
//                    return;
//                }
//                address = new InetSocketAddress(addr, remotePort);
//                socket.connect(address);
//            } catch (IOException e1) {
//                log.warn("connection failed to: " + address);
//                return;
//            }
//        }
//
//        // Get headers and negotiate crypto data on per-connection basis
//        if (encodedPublicKey == null) {
//            try {
//                initConnection();
//            } catch (IOException ex) {
//                Logger.getLogger(FractusConnector.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        synchronized (consumer) {
//            consumer.notifyAll();
//        }
//        initialized = true;
//
//        try {
//            serveConnection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
