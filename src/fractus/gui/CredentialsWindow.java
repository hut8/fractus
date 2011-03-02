package fractus.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CredentialsWindow
extends Window {
	private Text usernameText;
	private Text passwordText;
	private Text serverText;

	CredentialsWindow() {
		super((Shell)null);
	}

	@Override
	protected Control createContents(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.numColumns = 2;
		

		
		GridData gridData = null;
		
		Label titleLabel = new Label(parent, SWT.HORIZONTAL | SWT.CENTER);
		titleLabel.setText("Fractus Instant Messenger");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
		titleLabel.setLayoutData(gridData);
		
		Label usernameLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		usernameLabel.setText("Username:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		usernameLabel.setLayoutData(gridData);
		
		usernameText = new Text(parent, SWT.SINGLE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		
		usernameText.setLayoutData(gridData);
		
		Label passwordLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		passwordLabel.setText("Password:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		passwordLabel.setLayoutData(gridData);
		
		passwordText = new Text(parent, SWT.SINGLE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		passwordText.setLayoutData(gridData);
		
		Label serverLabel = new Label(parent, SWT.HORIZONTAL | SWT.RIGHT);
		serverLabel.setText("Server:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		serverLabel.setLayoutData(gridData);
		
		serverText = new Text(parent, SWT.SINGLE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		serverText.setLayoutData(gridData);
		
		parent.setLayout(gridLayout);
		
		return parent;
	}	
}
