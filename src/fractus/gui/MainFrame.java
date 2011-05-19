package fractus.gui;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JList;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fractus.main.Fractus;
import fractus.net.UserCredentials;

import javax.swing.ListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class MainFrame extends JFrame {

	private Fractus fractus;  //  @jve:decl-index=0:
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JList contactList = null;
	private JLabel titleLabel = null;
	private JButton systemStatusButton = null;
	private JPanel toolbarPanel = null;
	private JButton removeContactButton = null;
	private JButton addContactButton = null;
	private JMenuBar mainMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenu helpMenu = null;
	private JMenuItem aboutMenuItem = null;
	private JMenu contactsMenu = null;
	private JMenuItem systemStatusMenuItem = null;
	private JMenuItem addContactMenuItem = null;
	private JMenuItem removeContactMenuItem = null;
	private JCheckBoxMenuItem showOfflineContactsMenuItem = null;
	private JMenu sortContactMenu = null;
	private JRadioButtonMenuItem sortAlphabeticallyMenuItem = null;
	private JRadioButtonMenuItem sortStatusMenuItem = null;

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initialize();
	}

	public void setFractus(Fractus fractus) {
		this.fractus = fractus;
		this.fractus.addPropertyChangeListener("userCredentials",
				new CredentialsChangeListener());
	}
	
	private class CredentialsChangeListener
	implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!"userCredentials".equals(evt.getPropertyName())) {
				return;
			}
			MainFrame.this.setTitle("Fractus [" +
					((UserCredentials)evt.getNewValue()).getUsername() + "]");
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 600);
		this.setJMenuBar(getMainMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("Fractus");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.CENTER;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints3.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(10, 10, 10, 0);
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			titleLabel = new JLabel();
			titleLabel.setText("Fractus");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(0, 5, 5, 5);
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getContactList(), gridBagConstraints);
			jContentPane.add(titleLabel, gridBagConstraints1);
			jContentPane.add(getToolbarPanel(), gridBagConstraints3);
		}
		return jContentPane;
	}

	/**
	 * This method initializes contactList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getContactList() {
		if (contactList == null) {
			contactList = new JList();
			contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return contactList;
	}

	/**
	 * This method initializes systemStatusButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSystemStatusButton() {
		if (systemStatusButton == null) {
			systemStatusButton = new JButton();
			systemStatusButton.setToolTipText("System Status");
			systemStatusButton.setIcon(new ImageIcon(getClass().getResource("/utilities-system-monitor.png")));
		}
		return systemStatusButton;
	}

	/**
	 * This method initializes toolbarPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = new JPanel();
			toolbarPanel.setLayout(new BoxLayout(getToolbarPanel(), BoxLayout.X_AXIS));
			toolbarPanel.add(getSystemStatusButton(), null);
			toolbarPanel.add(getRemoveContactButton(), null);
			toolbarPanel.add(getAddContactButton(), null);
		}
		return toolbarPanel;
	}

	/**
	 * This method initializes removeContactButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveContactButton() {
		if (removeContactButton == null) {
			removeContactButton = new JButton();
			removeContactButton.setIcon(new ImageIcon(getClass().getResource("/list-remove.png")));
			removeContactButton.setToolTipText("Remove Contact");
		}
		return removeContactButton;
	}

	/**
	 * This method initializes addContactButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddContactButton() {
		if (addContactButton == null) {
			addContactButton = new JButton();
			addContactButton.setIcon(new ImageIcon(getClass().getResource("/list-add.png")));
			addContactButton.setHorizontalAlignment(SwingConstants.TRAILING);
			addContactButton.setToolTipText("Add Contact...");
			addContactButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Prompt for name of new contact
					// TODO
				}
			});
		}
		return addContactButton;
	}

	/**
	 * This method initializes mainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.add(getContactsMenu());
			mainMenuBar.add(getHelpMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getSystemStatusMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes exitMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Confirm choice
					int choice = JOptionPane.showConfirmDialog(
							MainFrame.this, "Are you sure you want to exit?",
							"Exit Confirmation", JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.NO_OPTION) return;
					
					if (fractus != null) {
						fractus.shutdown();
					} else {
						System.exit(0);
					}
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes helpMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes aboutMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About...");
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes contactsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getContactsMenu() {
		if (contactsMenu == null) {
			contactsMenu = new JMenu();
			contactsMenu.setText("Contacts");
			contactsMenu.add(getAddContactMenuItem());
			contactsMenu.add(getRemoveContactMenuItem());
			contactsMenu.addSeparator();
			contactsMenu.add(getShowOfflineContactsMenuItem());
			contactsMenu.add(getSortContactMenu());
			contactsMenu.addSeparator();
		}
		return contactsMenu;
	}

	/**
	 * This method initializes systemStatusMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSystemStatusMenuItem() {
		if (systemStatusMenuItem == null) {
			systemStatusMenuItem = new JMenuItem();
			systemStatusMenuItem.setText("System Status...");
		}
		return systemStatusMenuItem;
	}

	/**
	 * This method initializes addContactMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAddContactMenuItem() {
		if (addContactMenuItem == null) {
			addContactMenuItem = new JMenuItem();
			addContactMenuItem.setText("Add Contact");
			addContactMenuItem.setIcon(new ImageIcon(getClass().getResource("/list-add-16.png")));
		}
		return addContactMenuItem;
	}

	/**
	 * This method initializes removeContactMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRemoveContactMenuItem() {
		if (removeContactMenuItem == null) {
			removeContactMenuItem = new JMenuItem();
			removeContactMenuItem.setText("Remove Contact");
			removeContactMenuItem.setIcon(new ImageIcon(getClass().getResource("/list-remove-16.png")));
		}
		return removeContactMenuItem;
	}

	/**
	 * This method initializes showOfflineContactsMenuItem	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getShowOfflineContactsMenuItem() {
		if (showOfflineContactsMenuItem == null) {
			showOfflineContactsMenuItem = new JCheckBoxMenuItem();
			showOfflineContactsMenuItem.setText("Show offline contacts");
			showOfflineContactsMenuItem.setSelected(true);
		}
		return showOfflineContactsMenuItem;
	}

	/**
	 * This method initializes sortContactMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getSortContactMenu() {
		if (sortContactMenu == null) {
			sortContactMenu = new JMenu();
			sortContactMenu.setText("Sort Contacts");
			sortContactMenu.add(getSortAlphabeticallyMenuItem());
			sortContactMenu.add(getSortStatusMenuItem());
		}
		return sortContactMenu;
	}

	/**
	 * This method initializes sortAlphabeticallyMenuItem	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getSortAlphabeticallyMenuItem() {
		if (sortAlphabeticallyMenuItem == null) {
			sortAlphabeticallyMenuItem = new JRadioButtonMenuItem();
			sortAlphabeticallyMenuItem.setText("Alphabetically");
		}
		return sortAlphabeticallyMenuItem;
	}

	/**
	 * This method initializes sortStatusMenuItem	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getSortStatusMenuItem() {
		if (sortStatusMenuItem == null) {
			sortStatusMenuItem = new JRadioButtonMenuItem();
			sortStatusMenuItem.setText("By Status");
		}
		return sortStatusMenuItem;
	}

}
