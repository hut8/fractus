package fractus.gui.swing;

import java.awt.*;

import javax.swing.*;

public class MainToolbar extends JPanel {
	private static final long serialVersionUID = 7059702885175801415L;

	private ToolBarButton logoutButton;
	private ToolBarButton networkButton;
	
	
	public MainToolbar() {
		logoutButton = new ToolBarButton("lib/log-out.png");
		logoutButton.setToolTipText("Logout");
		networkButton = new ToolBarButton("lib/network.png");
		networkButton.setToolTipText("");
		
		setLayout(new FlowLayout(FlowLayout.LEADING));
		
		add(logoutButton);
		add(networkButton);
	}
	
	
}
