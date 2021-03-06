package fractus.gui;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;
import java.awt.Insets;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import fractus.main.Fractus;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CredentialsFrame extends JFrame {
	private String username;
	private String password;
	private String serverAddress;
	private Integer serverPort;

	private static final int USERNAME_LENGTH = 32;
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel titleLabel = null;
	private JLabel usernameLabel = null;
	private JTextField usernameField = null;
	private JLabel passwordLabel = null;
	private JPasswordField passwordField = null;
	private JLabel serverLabel = null;
	private JTextField serverField = null;
	private JButton cancelButton = null;
	private JButton confirmButton = null;
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
		this.setMaximumSize(new Dimension(400, 350));
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
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 2;
			gridBagConstraints22.anchor = GridBagConstraints.EAST;
			gridBagConstraints22.ipadx = 0;
			gridBagConstraints22.ipady = 0;
			gridBagConstraints22.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints22.gridy = 4;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.insets = new Insets(10, 0, 0, 10);
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.weightx = 0.5D;
			gridBagConstraints12.gridy = 4;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.ipadx = 10;
			gridBagConstraints3.gridy = 3;
			serverLabel = new JLabel();
			serverLabel.setText("Server");
			serverLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			serverLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints21.gridwidth = 2;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.ipadx = 10;
			gridBagConstraints11.gridy = 2;
			passwordLabel = new JLabel();
			passwordLabel.setText("Password");
			passwordLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			passwordLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.insets = new Insets(0, 10, 0, 10);
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.ipadx = 10;
			gridBagConstraints1.gridy = 1;
			usernameLabel = new JLabel();
			usernameLabel.setText("Username");
			usernameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			usernameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.ipady = 20;
			gridBagConstraints.weighty = 0.0D;
			gridBagConstraints.gridy = 0;
			titleLabel = new JLabel();
			titleLabel.setText("Credentials");
			titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
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
			jContentPane.add(getCancelButton(), gridBagConstraints12);
			jContentPane.add(getConfirmButton(), gridBagConstraints22);
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
			usernameField.setText("bowenl2");
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
			passwordField.setText("1337");
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
			serverField.setColumns(0);
			serverField.setText("localhost:1337");
		}
		return serverField;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
			cancelButton.addActionListener(new ActionListener() {   
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Fractus.getInstance().shutdown();
				}			
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes confirmButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = new JButton();
			confirmButton.setText("Connect");
			confirmButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Parse out port from server
					String server = serverField.getText().contains(":") ?
							serverField.getText().substring(0, serverField.getText().indexOf(":"))
							: serverField.getText();
					int port = 1337;
					if (serverField.getText().contains(":")) {
						port = Integer.parseInt(
								serverField.getText().substring(
										serverField.getText().indexOf(":")+1));
					}
					Fractus.getInstance().initialize(usernameField.getText(),
							new String(passwordField.getPassword()),
							server,
							port);
				}
			});
		}
		return confirmButton;
	}
}
