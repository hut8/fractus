package fractus.gui;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.Dimension;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel titleLabel = null;
	private JLabel subtitleLabel = null;
	private JLabel authorsLabel = null;

	/**
	 * @param owner
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(352, 202);
		this.setTitle("Fractus - About");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 2;
			authorsLabel = new JLabel();
			authorsLabel.setText("JLabel");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 145, 0);
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.ipadx = -104;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(15, 20, 1, 0);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.ipadx = 5;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.ipady = 5;
			gridBagConstraints.gridx = 0;
			subtitleLabel = new JLabel();
			subtitleLabel.setText("A proactively secure, cryptographically hardened, peer-to-peer instant messenger");
			subtitleLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			titleLabel = new JLabel();
			titleLabel.setText("Fractus");
			titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(titleLabel, gridBagConstraints);
			jContentPane.add(subtitleLabel, gridBagConstraints1);
			jContentPane.add(authorsLabel, gridBagConstraints2);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="19,19"
