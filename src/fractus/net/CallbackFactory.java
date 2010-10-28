package fractus.net;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fractus.main.Contact;
import fractus.main.ContactManager;
import fractus.main.Fractus;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;

public class CallbackFactory {
//	public static HashMap<String,Callback> getClientCallbackMap(final ContactManager tracker) {
//		HashMap<String,Callback> map = new HashMap<String,Callback>();
//		/* message */
//		registerCallback(map, "message", new Callback() {
//			@Override
//			public void dispatch(String sender, Element message, FractusConnector cc) {
//				if (sender == null) {
//					Logger.getAnonymousLogger().log(Level.INFO,"SmartCallback: message: received message from unknown sender");
//					return;
//				}
//				String messageContent = message.getTextContent();
//				if (messageContent == null) {
//					messageContent = "";
//				}
//				//tracker.receiveMessage(sender, messageContent);
//			}
//
//		});
//
//		/* sign-on */
//		registerCallback(map, "sign-on", new Callback() {
//			@Override
//			public void dispatch(String username, Element message, FractusConnector cc) {
//				if (username == null) {
//					Logger.getAnonymousLogger().log(Level.INFO,"SmartCallback: sign-on: a user signed on, but could not be identified");
//					return;
//				}
//				Logger.getAnonymousLogger().log(Level.INFO,"SmartCallback: sign-on: signing on "+username);
//
//				//tracker.getContact(username).getLocationManager().addConnection(cc);
//				//stracker.getContact(username).signOn();
//			}
//		});
//
//		/* start-proxy */
//		registerCallback(map, "start-proxy", new Callback() {
//			public void dispatch(String username, Element message, FractusConnector cc) {
//				if (username == null) {
//					Logger.getAnonymousLogger().log(Level.INFO,"SmartCallback: start-proxy: a user signed on, but could not be identified");
//					return;
//				}
//			}
//		});
//		return map;
//	}
//
//	public static HashMap<String,Callback> getServerCallbackMap(final ContactManager tracker) {
//		HashMap<String,Callback> map = new HashMap<String,Callback>();
//		registerCallback(map, "register-key-response", new Callback() {
//			@Override
//			public void dispatch(String sender, Element message, FractusConnector cc) {
//
//			}
//
//		});
//		registerCallback(map, "contact-data", tracker.getContactDataCallback());
//		registerCallback(map, "register-location-response", new Callback() {
//			@Override
//			public void dispatch(String sender, Element message, FractusConnector cc) {
//
//			}
//		});
//		registerCallback(map, "identify-key-response", new Callback() {
//			@Override
//			public void dispatch(String sender, Element message, FractusConnector cc) {
//
//			}
//		});
//		return map;
//	}
//
//	public static void registerCallback(HashMap<String,Callback> map, String identifier, Callback cb) {
//		map.put(identifier, cb);
//	}

	
}

