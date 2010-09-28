package us.fract.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import us.fract.connection.EncryptionManager;
import us.fract.connection.PacketHandler;

import us.fract.net.FractusMessage;
import us.fract.net.LocationManager;
import us.fract.net.PublicKeyDirectory;

public class Contact {	
	private String username;
	private boolean online = false;
	private LocationManager lm;
	
	private Delegate<DelegateMethod<IncomingMessageEventData>, IncomingMessageEventData> incomingMessageDelegate;
	private Delegate<DelegateMethod<ContactStatusEventData>, ContactStatusEventData> statusChangeDelegate;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	
	public Contact(String username,
                EncryptionManager encryptionManager,
                PacketHandler packetHandler,
                PublicKeyDirectory publicKeyDirectory) {
		this.username = username;
		lm = new LocationManager(encryptionManager, packetHandler, publicKeyDirectory);
		online = false;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void signOn() {
		online = true;
		propertyChangeSupport.firePropertyChange("isOnline", false, true);
	}
	
	public void signOff() {
		online = false;
		propertyChangeSupport.firePropertyChange("isOnline", true, false);
	}
	
	public String getName() {
		return username;
	}
	
	public void sendMessage(String message) {
		FractusMessage fm = null;
		try {
			fm = new FractusMessage();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element el = fm.getDocument().createElement("message");
		el.setTextContent(message);
		fm.getDocumentElement().appendChild(el);
		
		lm.sendMessage(fm);
	}
	
	public void receiveMessage(String message) {
		incomingMessageDelegate.invoke(new IncomingMessageEventData(this, message));
	}
	
	public boolean isOnline() {
		return online;
	}

	
	public LocationManager getLocationManager() {
		return lm;
	}

	public void addLocation(String address, int port) {
		lm.addConnection(address,port);
	}
	
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
