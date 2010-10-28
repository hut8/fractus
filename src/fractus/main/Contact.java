package fractus.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import fractus.net.EncryptionManager;
import fractus.net.PacketHandler;

import fractus.net.FractusPacket;
import fractus.net.LocationManager;
import fractus.net.ProtocolBuffer;
import fractus.net.PublicKeyDirectory;

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
        byte[] serialMessage = ProtocolBuffer.InstantMessage.newBuilder()
                .setContents(message).build().toByteArray();
        lm.sendPacket(new FractusPacket(serialMessage));
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
        lm.addConnection(address, port);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
