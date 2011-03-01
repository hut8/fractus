package fractus.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GuiManager {
	private Display display;
	private volatile boolean active;
	
	private CredentialsWindow credentialsWindow;
	
	public GuiManager() {
		this.display = new Display();
		active = true;
		
		// Create windows
		credentialsWindow = new CredentialsWindow();
	}
	
	public void shutdown() {
		active = false;
	}
	
	public Display getDisplay() {
		return this.display;
	}
	
    public void dispatchEvents() {
    	while (active) {
    		if (!display.readAndDispatch()) {
    			display.sleep();
    		}
    	}
    	display.dispose();
    }
    
    /**
     * 
     */
    public synchronized void showCredentials() {
    	credentialsWindow.open();
    }
    
    public static void main(String args[]) {
    	GuiManager guiTest = new GuiManager();
    	guiTest.showCredentials();
    	guiTest.dispatchEvents();
    }
}
