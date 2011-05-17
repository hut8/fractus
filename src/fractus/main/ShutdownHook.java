package fractus.main;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ShutdownHook
extends Thread {
	private Fractus fractus;

	public ShutdownHook(Fractus fractus) {
		this.fractus = fractus;
	}

	public void run() {
		//Logger.getAnonymousLogger().log(Level.INFO,"ShutdownHook: starting shutdown procedure");
		//fractus.getServerConnection().invalidLocation(fractus.getRouteManager());
		//Logger.getAnonymousLogger().log(Level.INFO,"ShutdownHook: asking NetListener to close UPnP if possible");
		//fractus.getRouteManager().getNetListener().closeUpnp();
	}
}
