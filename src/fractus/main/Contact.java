package fractus.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import fractus.crypto.PublicKeyDirectory;
import fractus.delegate.ContactStatusEventData;
import fractus.delegate.Delegate;
import fractus.delegate.DelegateMethod;
import fractus.delegate.IncomingMessageEventData;
import fractus.net.PacketHandler;

import fractus.net.FractusPacket;
import fractus.net.LocationManager;
import fractus.net.ProtocolBuffer;

public class Contact {
    private String username;
    private boolean online;
    private LocationManager locationManager;
    private PropertyChangeSupport propertyChangeSupport;
    
    public Contact(String username) {
        this.username = username;
        this.locationManager = new LocationManager();
        this.online = false;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
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
    	
    }

    public void receiveMessage(String message) {
    	
    }

    public boolean isOnline() {
        return online;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
