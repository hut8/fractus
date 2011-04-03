package fractus.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagLayout;

public class LogFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane logScrollPane = null;
	private JTable logTable = null;
	private JPanel filterPanel = null;

	/**
	 * This is the default constructor
	 */
	public LogFrame() {
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
		this.setTitle("Fractus - Log Viewer");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getFilterPanel(), null);
			jContentPane.add(getLogScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes logScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getLogScrollPane() {
		if (logScrollPane == null) {
			logScrollPane = new JScrollPane();
			logScrollPane.setViewportView(getLogTable());
		}
		return logScrollPane;
	}

	/**
	 * This method initializes logTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getLogTable() {
		if (logTable == null) {
			logTable = new JTable();
		}
		return logTable;
	}

	/**
	 * This method initializes filterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
		}
		return filterPanel;
	}

}
