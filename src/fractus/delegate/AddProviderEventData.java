package fractus.delegate;

import java.security.Provider;


public class AddProviderEventData extends EventData {
	private Provider provider;
	public AddProviderEventData(Provider provider) {
		this.provider = provider;
	}
	public Provider getProvider() { return provider; }
}
