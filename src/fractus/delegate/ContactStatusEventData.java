package fractus.delegate;

import fractus.main.Contact;

public class ContactStatusEventData extends EventData {
	private Contact contact;
	public ContactStatusEventData(Contact contact) {
		this.contact = contact;
	}
	public Contact getContact() { return contact; }
}
