package fractus.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;
import java.awt.Insets;
import javax.swing.JPasswordField;

public class CredentialsFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel titleLabel = null;
	private JLabel usernameLabel = null;
	private JTextField usernameField = null;
	private JLabel passwordLabel = null;
	private JPasswordField passwordField = null;
	private JLabel serverLabel = null;
	private JTextField serverField = null;
	/**
	 * This is the default constructor
	 */
	public CredentialsFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Fractus - Credentials");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 3;
			serverLabel = new JLabel();
			serverLabel.setText("Server");
			serverLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			serverLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 2;
			passwordLabel = new JLabel();
			passwordLabel.setText("Password");
			passwordLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.gridy = 1;
			usernameLabel = new JLabel();
			usernameLabel.setText("Username");
			usernameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.gridy = 0;
			titleLabel = new JLabel();
			titleLabel.setText("Credentials");
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(titleLabel, gridBagConstraints);
			jContentPane.add(usernameLabel, gridBagConstraints1);
			jContentPane.add(getUsernameField(), gridBagConstraints2);
			jContentPane.add(passwordLabel, gridBagConstraints11);
			jContentPane.add(getPasswordField(), gridBagConstraints21);
			jContentPane.add(serverLabel, gridBagConstraints3);
			jContentPane.add(getServerField(), gridBagConstraints4);
		}
		return jContentPane;
	}

	/**
	 * This method initializes usernameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUsernameField() {
		if (usernameField == null) {
			usernameField = new JTextField();
		}
		return usernameField;
	}

	/**
	 * This method initializes passwordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
		}
		return passwordField;
	}

	/**
	 * This method initializes serverField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getServerField() {
		if (serverField == null) {
			serverField = new JTextField();
		}
		return serverField;
	}

}
