package fractus.main;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.crypto.NoSuchPaddingException;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import fractus.crypto.PublicKeyDirectory;
import fractus.delegate.AddContactEventData;
import fractus.delegate.Delegate;
import fractus.delegate.DelegateMethod;
import fractus.delegate.EventData;

import fractus.net.PacketHandler;


public class ContactManager {
	private static final Logger log;
	static {
		log = Logger.getLogger(ContactManager.class);
	}

	private HashMap<String, WeakReference<Contact>> contactMap;
	private static HashSet<Contact> contactSet;

	private PacketHandler packetHandler;
	private PublicKeyDirectory publicKeyDirectory;

	private Delegate<DelegateMethod<AddContactEventData>, AddContactEventData> addContactDelegate;
	private Delegate<DelegateMethod<EventData>, EventData> isValidDelegate;

	public ContactManager() {
		contactMap = new HashMap<String, WeakReference<Contact>>();
		contactSet = new HashSet<Contact>();
		addContactDelegate = new Delegate<DelegateMethod<AddContactEventData>, AddContactEventData>();
		isValidDelegate = new Delegate<DelegateMethod<EventData>, EventData>();
	}

	public Delegate<DelegateMethod<AddContactEventData>, AddContactEventData> getAddContactDelegate() {
		return addContactDelegate;
	}

	public Delegate<DelegateMethod<EventData>, EventData> getIsValidDelegate() {
		return isValidDelegate;
	}

	public Contact addContact(String username) {
		synchronized(contactMap) {
			if (contactMap.containsKey(username)) {
				Contact rv = contactMap.get(username).get();
				if (rv != null)
					return contactMap.get(username).get();
			}
		}
		final Contact contact =	new Contact(username);
		synchronized (contactMap) {
			contactMap.put(username, new WeakReference<Contact>(contact));	
		}
		addContactDelegate.invoke(new AddContactEventData(contact));
		return contact;
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
}