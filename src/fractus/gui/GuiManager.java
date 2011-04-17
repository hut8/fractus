package fractus.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

public class GuiManager {
	
	// Singleton
	private static GuiManager instance = new GuiManager();
	public static GuiManager getInstance() {
		return instance;
	}
	
	// Log
	private static Logger log;
	static {
		log = Logger.getLogger(GuiManager.class.getName());
	}
	
	// Frame instances
	private CredentialsFrame credentialsDialog;
	
	private GuiManager() {
		credentialsDialog = new CredentialsFrame();
	}
	
	public void main() {
		String lafName = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lafName);
		} catch (Exception e) {
			log.warn("Could not find native look and feel; reverting to cross-platform", e);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				credentialsDialog.setVisible(true);
			}
		});
	}
}
