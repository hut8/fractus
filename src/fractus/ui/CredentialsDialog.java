package fractus.ui;

import com.trolltech.qt.gui.*;

import fractus.main.Fractus;

public class CredentialsDialog extends QWidget {

	/**
	 * @param args
	 */
	 private QLineEdit usernameLE;
	 private QLineEdit passwordLE;
	 private QLineEdit serverLE;
	 private QPushButton loginB;
	 private QPushButton quitB;
	 private Fractus f;
	
	public static void main(String[] args) {
		 QApplication.initialize(args);

	     CredentialsDialog creds = new CredentialsDialog();
	     creds.show();
	     creds.raise();
	     QApplication.exec();


	}
	
	public void login() {
		
		String username = usernameLE.text();
		String password = passwordLE.text();
		String server = serverLE.text();
		f.login(username,password,server);
		
	}
	
	public void quit() {
		System.exit(0);
	}
	
	public CredentialsDialog() {
		this(null);
		
		
	}
	
	public CredentialsDialog(Fractus fractus) {
		this(fractus,null);
		
	}
	
	public CredentialsDialog(Fractus fractus, QWidget parent) {
        super(parent);
        f = fractus;
        QLabel userL = new QLabel(tr("User:"));
        usernameLE = new QLineEdit();
        usernameLE.setFocus();
        
        QLabel passL = new QLabel(tr("Password:"));
        passwordLE = new QLineEdit();
        passwordLE.setEchoMode(QLineEdit.EchoMode.Password);
        
        QLabel serverL = new QLabel(tr("Server:"));
        serverLE = new QLineEdit();
        serverLE.setText("fract.us");
        
        quitB = new QPushButton("quit");
        loginB = new QPushButton("login");
        
        loginB.clicked.connect(this,"login()");
        quitB.clicked.connect(this,"quit()");
        serverLE.returnPressed.connect(this, "login()");
        passwordLE.returnPressed.connect(this,"login()");
        
        QGridLayout layout = new QGridLayout();
        layout.addWidget(userL, 0, 0);
        layout.addWidget(usernameLE, 0, 1);
        layout.addWidget(passL, 1, 0);
        layout.addWidget(passwordLE, 1, 1);
        layout.addWidget(serverL, 2, 0);
        layout.addWidget(serverLE, 2, 1);
        layout.addWidget(quitB,3,0);
        layout.addWidget(loginB,3,1);
        setLayout(layout);
        
        setWindowTitle(tr("Fractus"));
       

	}

}
