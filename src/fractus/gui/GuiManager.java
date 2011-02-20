package fractus.gui;

import org.eclipse.swt.widgets.Display;

public class GuiManager {
	private Display display;
	private volatile boolean active;
	private CredentialsWindow credentialsWindow;
	
	public GuiManager() {
		this.display = new Display();
		this.credentialsWindow = new CredentialsWindow();
		active = true;
	}
	
	public void shutdown() {
		active = false;
	}
	
	public Display getDisplay() {
		return this.display;
	}
	
	public CredentialsWindow getCredentialsWindow() {
		return this.credentialsWindow;
	}
	
    public void dispatchEvents() {
    	while (active) {
    		if (!display.readAndDispatch()) {
    			display.sleep();
    		}
    	}
    	display.dispose();
    }
}
