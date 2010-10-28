package us.fract.main;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.swing.table.AbstractTableModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import us.fract.net.FractusConnector;
import us.fract.net.EncryptionManager;
import us.fract.net.PacketHandler;
import us.fract.net.Callback;
import us.fract.net.PublicKeyDirectory;


public class ContactManager {
	private boolean isValid;
	private Logger log;

	private HashMap<String, WeakReference<Contact>> contactMap;
	private static HashSet<Contact> contactSet;

        private PacketHandler packetHandler;
        private EncryptionManager encryptionManager;
        private PublicKeyDirectory publicKeyDirectory;
	
	private Delegate<DelegateMethod<AddContactEventData>, AddContactEventData> addContactDelegate;
	private Delegate<DelegateMethod<EventData>, EventData> isValidDelegate;

	public ContactManager(EncryptionManager encryptionManager,
                PacketHandler packetHandler,
                PublicKeyDirectory publicKeyDirectory) {
		log = Logger.getLogger(this.getClass().getName());

                this.encryptionManager = encryptionManager;
                this.packetHandler = packetHandler;
                this.publicKeyDirectory = publicKeyDirectory;

		contactMap = new HashMap<String, WeakReference<Contact>>();
		contactSet = new HashSet<Contact>();
				
		isValid = false;
		addContactDelegate = new Delegate<DelegateMethod<AddContactEventData>, AddContactEventData>();
		isValidDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
	}

	public Delegate<DelegateMethod<AddContactEventData>, AddContactEventData> getAddContactDelegate() {
		return addContactDelegate;
	}
	
	public Delegate<DelegateMethod<EventData>, EventData> getIsValidDelegate() {
		return isValidDelegate;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
		isValidDelegate.invoke(new EventData());
	}

//	public void receiveMessage(String originUsername, String message) {
//		log.info("received message for " + originUsername + " [" + message + "]");
//		if(message != null && originUsername != null) {
//			Contact sender = getContact(originUsername);
//			if (sender == null) {
//				log.warning("No such contact on contact list!");
//				return;
//			}
//			sender.receiveMessage(message);
//		}
//	}

	public Contact addContact(String username) {
		synchronized(contactMap) {
			if (contactMap.containsKey(username)) {
				Contact rv = contactMap.get(username).get();
				if (rv != null)
					return contactMap.get(username).get();
			}
		}
		final Contact contact =
                        new Contact(username, encryptionManager, packetHandler, publicKeyDirectory);
		synchronized (contactMap) {
			contactMap.put(username, new WeakReference<Contact>(contact));	
		}
		addContactDelegate.invoke(new AddContactEventData(contact));
		return contact;
	}
	
	public Callback getContactDataCallback() {
		return new ContactDataCallback();
	}
	
	public static class ContactManagerTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 5389629419749882088L;
		private ArrayList<Contact> contactCache;
		private ContactManager contactManager;		
		
		public ContactManagerTableModel(ContactManager contactManager) {
			contactCache = new ArrayList<Contact>();
			this.contactManager = contactManager;
		}
		
		@Override
		public void fireTableDataChanged() {
			// Rebuild Cache
			super.fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return contactSet.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex)
			{
			case 0:
				
				break;
			case 1:
				break;
			default:
				return null;
			}return null;
		}
		
	}

		
	public class ContactDataCallback implements Callback {
		@Override
		public void dispatch(String sender, Element message, FractusConnector fc)
		throws NumberFormatException, IOException,
		NoSuchAlgorithmException, NoSuchPaddingException {
			setValid(true);
			NodeList operations = message.getChildNodes();
			for (int i=0; i < operations.getLength(); i++) {
				Node n = operations.item(i); 
				if (n.getNodeType() != Node.ELEMENT_NODE) { continue; }
				Element e = (Element)n;
				String tag = e.getTagName();				    
				if (tag.equals("contact")) {
					String name = e.getAttribute("username");
					Contact buddy = addContact(name);
					NodeList inneroperations = e.getChildNodes();
					for (int j=0; j < inneroperations.getLength(); j++) {
						Node in = inneroperations.item(j); 
						if (in.getNodeType() != Node.ELEMENT_NODE) { continue; }
						Element ie = (Element)in;
						String innertag = ie.getTagName();
						if(innertag.equals("location")) {
							String address = ie.getAttribute("address");
							String port = ie.getAttribute("port");
							buddy.addLocation(address, Integer.parseInt(port));
						}
					}
				}
			}
		}
	}
}
