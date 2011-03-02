package fractus.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CredentialsWindow
extends Window {

	CredentialsWindow() {
		super((Shell)null);
	}

	@Override
	protected Control createContents(Composite parent) {
		
		
		Label titleLabel = new Label(parent, SWT.HORIZONTAL | SWT.CENTER);
		titleLabel.setText("Fractus Instant Messenger");
		
		Label usernameLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		usernameLabel.setText("Username:");
		
		Label passwordLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		passwordLabel.setText("Password:");
		
		Label serverLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		serverLabel.setText("Server:");
		
		return parent;
	}	
}
