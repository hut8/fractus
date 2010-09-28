package us.fract.connection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import us.fract.main.ProtocolException;
import us.fract.net.Callback;
import us.fract.net.FractusMessage;
import us.fract.net.FractusPacket;
import us.fract.net.PublicKeyDirectory;
import us.fract.net.UserCredentials;


public class PacketHandler {
	private HashMap<String,Callback> callbackMap;
	private PublicKeyDirectory publicKeyDirectory;
	
	public PacketHandler(HashMap<String, Callback> callbackMap, PublicKeyDirectory tracker) {
		this.callbackMap = callbackMap;	
		this.publicKeyDirectory = tracker;
	}
	
	
	public void handle(FractusPacket fp, ClientConnector fc) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ParserConfigurationException, IOException, ProtocolException {
		// Identity of remote user
		String username;
		
		// One message will get one response
		FractusMessage response;
		try {
			 response = new FractusMessage();
		} catch (ParserConfigurationException e2) {
			e2.printStackTrace();
			return;
		}
		
		// Identify sender based on public key
		String encodedKey = fp.getEncodedKey();
		username = publicKeyDirectory.identifyKeyOwner(encodedKey);
		
		// If not identified by public key, fine --
		// we can still see if it has its own authentication data
		
		// Turn FractusPacket message into XML Document		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return;
		}
		Document doc;
		try {
			doc = docBuilder.parse(new ByteArrayInputStream(fp.getMessage()));
		} catch (SAXException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
	    // Extract FractusMessage from packet
		FractusMessage msg = new FractusMessage(doc);
		
		// Extract authentication data if there is any
		UserCredentials uc = msg.getUserCredentials();
		
		// Make sure that the key and usercredentials agree if both are present
		if (username != null && uc != null) {
			if (!username.equals(uc.getUsername())) {
				// TODO: THIS IS VERY BAD.
				System.out.println("mismatch:"+username+":"+uc.getUsername());
				System.out.println("PacketHandler: serious error: username mismatched");
				return;
			}
		}
		
		if (username == null) {
			// Error: we have no idea who this is.
			FractusErrorResponse res = new FractusErrorResponse("authentication-required");
			response.appendElement(res.serialize(response.getDocument()));
			try {
				fc.sendMessage(response);
			} catch (Exception e) { e.printStackTrace(); }
			return;
		}
		
	    // Loop through root level elements
		Element root = doc.getDocumentElement();
		NodeList operations = root.getChildNodes();
		for (int i=0; i < operations.getLength(); i++) {
			Node n = operations.item(i); 
			if (n.getNodeType() != Node.ELEMENT_NODE) { continue; }
			
			Element e = (Element)n;
		    Callback cb = callbackMap.get(e.getTagName());
		    if (cb == null) {
		    	// TODO: Error: unknown tag name
		    	/* send an error to the fractusconnector */
		    	continue;
		    }
		    cb.dispatch(username, e, fc);
		}
		
	}
	
	public String handle(FractusPacket fp)
	throws NumberFormatException,
	IOException, GeneralSecurityException {
		// Turn FractusPacket message into XML Document		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}
		Document doc;
		try {
			doc = docBuilder.parse(new ByteArrayInputStream(fp.getMessage()));
		} catch (SAXException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		 // Loop through root level elements
		Element root = doc.getDocumentElement();
		NodeList operations = root.getChildNodes();
		String username = null;
		for (int i=0; i < operations.getLength(); i++) {
			Node n = operations.item(i); 
			if (n.getNodeType() != Node.ELEMENT_NODE) { continue; }
			
			Element e = (Element)n;
		    Callback cb = callbackMap.get(e.getTagName());
		    if(e.getTagName().equals("identify-key-response")) {		    	
				username = e.getAttribute("username");
				System.out.println("registered key for "+username);
				String key = e.getAttribute("key");
				publicKeyDirectory.addKey(username,key);
		    } else if (cb == null) {
		    	Logger.getAnonymousLogger().log(Level.WARNING, "Unknown callback reference: " + e.getTagName() + " - Ignoring");
		    	/* send an error to the fractusconnector */
		    	continue;
		    }
		    cb.dispatch("server", e, null);
		}
		return username;
		
	}
}
