package us.fract.main;

import us.fract.net.EncryptionManager;

public class KeyGeneratedEventData extends EventData {
	private EncryptionManager encryptionManager;
	public KeyGeneratedEventData(EncryptionManager encryptionContext) {
		this.encryptionManager = encryptionContext;
	}
	public EncryptionManager getEncryptionManager() { return encryptionManager; }
}
