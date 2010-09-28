package us.fract.main;

public class IncomingMessageEventData extends EventData {
	private Contact contact;
	private String message;
	public IncomingMessageEventData(Contact contact, String message) {
		this.contact = contact;
		this.message = message;
	}
	public String getMessage() { return message; }
	public Contact getContact() { return contact; }
}
