package us.fract.connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;

import us.fract.main.*;
import us.fract.net.FractusMessage;
import us.fract.net.FractusPacket;
import us.fract.net.PublicKeyDirectory;


public class ClientConnector
extends Thread {
	private static Logger log;
	
	static {
		log = Logger.getLogger(ClientConnector.class.getName());
	}
	
	private Delegate<DelegateMethod<ClientConnectionEventData>, ClientConnectionEventData>
		connectionStateChangeDelegate;
	
	private InetSocketAddress address;
	private String addressString;
	private Integer remotePort;
	private EncryptionManager em;
	private InputStream input;
	private OutputStream output;
	private PacketHandler handler;
	private SecretKeySpec sks;
	private String encodedPublicKey;
	private Socket socket;
	private ArrayList<FractusMessage> queue;
	private Thread consumer;
	private boolean initialized = false;
        private PublicKeyDirectory publicKeyDirectory;
	
	public ClientConnector(String addressString,
                Integer remotePort, EncryptionManager em, PacketHandler handler,
                PublicKeyDirectory publicKeyDirectory) {
		this.connectionStateChangeDelegate = new Delegate<DelegateMethod<ClientConnectionEventData>, ClientConnectionEventData>();
		this.publicKeyDirectory = publicKeyDirectory;
                this.addressString = addressString;
		this.remotePort = remotePort;
		this.em = em;
		this.handler = handler;
		this.socket = new Socket();
		queue = new ArrayList<FractusMessage>();
		encodedPublicKey = null;
		createConsumer();
	}

	public ClientConnector(Socket socket, EncryptionManager em, PacketHandler handler) {
		this.address = (InetSocketAddress) socket.getRemoteSocketAddress();
		this.addressString = address.getAddress().getHostAddress();
		this.remotePort = address.getPort();
		this.em = em;
		this.handler = handler;
		this.socket = socket;
		queue = new ArrayList<FractusMessage>();
		encodedPublicKey = null;
		createConsumer();
	}
	
	public String getAddressString() { return addressString; }
	public Integer getRemotePort() { return remotePort; }

	public void sendMessage(FractusMessage message) throws
	UnsupportedEncodingException,
	IOException,
	NoSuchAlgorithmException,
	NoSuchPaddingException {
		synchronized(consumer) {
			queue.add(message);
			log.info("Added new FractusMessage to queue");
			if(initialized) {
				consumer.notifyAll();
				log.info("Notified consumer of new outgoing message");
			}
		}

	}

	public void disconnect() {
		log.info("Disconnecting");
		try {
			socket.close();
		} catch (IOException e) { }
	}

	private void connectStreams() {
		try {
			output = socket.getOutputStream();
			input = socket.getInputStream();
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "could not get streams from socket");
			disconnect();
		}
	}

	public void initConnection() {
		connectStreams();
		// Send headers
		new Headers().writeHeaders(output, em);

		// Receive headers
		Headers recvHeaders;
		try {
			recvHeaders = Headers.receive(input);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "encountered network error while receiving headers");
			disconnect(); return;
		} catch (ProtocolException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "encountered procol error while receiving headers");
			disconnect(); return;
		}

		PeerCryptoData pcd;
		try {
			pcd = PeerCryptoData.negotiate(recvHeaders, em);
		} catch (NoSuchAlgorithmException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "could not find algorithm to decrypt (AES)");
			disconnect(); return;
		} catch (NoSuchProviderException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "could not load BouncyCastle provider");
			disconnect(); return;
		} catch (InvalidKeySpecException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "encountered invalid key spec from remote host");
			disconnect(); return;
		}

		sks = pcd.getSecretKeySpec();
		encodedPublicKey = pcd.getEncodedKey();
	}

	private void createConsumer() {
		consumer = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						synchronized(consumer) {
							log.info("Pausing to be awoken to process message...");
							consumer.wait();
						}
					} catch (InterruptedException e) {
						log.info("Consumer awoken");
					}

					synchronized(queue) {
						while (!ClientConnector.this.queue.isEmpty()) {
							FractusMessage fm = ClientConnector.this.queue.remove(0);
							log.info("Consumer sending message: " + fm.serialize());
							try {
								output.write(new FractusPacket(fm.serialize().getBytes("UTF-8"), sks).serialize());
							} catch (InvalidKeyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchPaddingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// This could be fixed
								ClientConnector.this.queue.add(0, fm);
								e.printStackTrace();
							}
						}
					}
				}				
			}
		};
		consumer.start();
	}

	@Override
	public void run() {
		// Connect our socket
		if (!socket.isConnected()) {
			try {
				InetAddress addr;
				try {
					addr = InetAddress.getByName(addressString);
				} catch (UnknownHostException e) {
					log.warning("Cannot find host " + addressString);
					return;
				}
				address = new InetSocketAddress(addr,remotePort);
				socket.connect(address);
			} catch (IOException e1) {
				Logger.getAnonymousLogger().log(Level.INFO, "connection failed to: " + address);
				return;
			}
		}

		// Get headers and negotiate crypto data on per-connection basis
		if(encodedPublicKey == null)
			initConnection();

		synchronized (consumer) {
			consumer.notifyAll();
		}
		initialized = true;

		try {
			serveConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void serveConnection()
	throws GeneralSecurityException,
	ParserConfigurationException,
	IOException,
	ProtocolException {
		while (socket.isConnected()) {
			FractusPacket fp = new FractusPacket(sks);
			try {
				log.info("Waiting for packet...");
				fp.readPacket(input, encodedPublicKey, em);
			} catch (RuntimeException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if(fp.getMessage() == null) {
				String keyowner = publicKeyDirectory.identifyKeyOwner(encodedPublicKey);
				System.out.println("signing off: "+keyowner);
				return;
			}
			handler.handle(fp,this);
		}
		Logger.getAnonymousLogger().log(Level.INFO, "no longer serving connection");
	}

	public boolean isConnected() {
		return socket.isConnected();
	}
}
