package fractus.main;
import java.util.logging.Level;
import java.util.logging.Logger;

import fractus.net.*;

public class ShutdownProcedure
	extends Thread {
	private NetListener listener;
	private ServerConnection server;
	
	public ShutdownProcedure(NetListener listener, ServerConnection server) {
		this.listener = listener;
		this.server = server;
	}
	
	public void run() {
		Logger.getAnonymousLogger().log(Level.INFO,"ShutdownHook: starting shutdown procedure");
		Logger.getAnonymousLogger().log(Level.INFO,"ShutdownHook: asking NetListener to close UPnP if possible");
		listener.closeUpnp();
	}
}
