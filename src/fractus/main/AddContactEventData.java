package fractus.main;

public class AddContactEventData extends EventData {
	private Contact contact;
	public AddContactEventData(Contact contact) {
		this.contact = contact;
	}
	public Contact getContact() {
		return contact;
	}
}
