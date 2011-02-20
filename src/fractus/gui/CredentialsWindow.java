package fractus.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CredentialsWindow
extends ApplicationWindow {
	private GridLayout gridLayout;
	
	public CredentialsWindow() {
		this(null);
	}

	public CredentialsWindow(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText("Fractus Credentials");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		
		Label usernameLabel = new Label(shell, SWT.NONE);
		usernameLabel.setText("Username:");
		Text usernameText = new Text(shell, SWT.SINGLE);
		
		Label passwordLabel = new Label(shell, SWT.NONE);
		passwordLabel.setText("Password:");
		Text passwordText = new Text(shell, SWT.SINGLE);
		passwordText.setEchoChar('*');
		
		Label serverLabel = new Label(shell, SWT.NONE);
		serverLabel.setText("Server:");
		Text serverText = new Text(shell, SWT.SINGLE);
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	


}
