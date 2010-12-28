package fractus.gui.swing;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import fractus.main.ContactManager;
import fractus.main.Delegate;
import fractus.main.DelegateMethod;
import fractus.main.EventData;

public class MainFrame
        extends JFrame implements ComponentListener {

    private static final long serialVersionUID = -6012853101278602199L;
    private MainToolbar toolbar;
    private JPanel container;
    private Preferences preferences;
    private ContactManager contactManager;
    

    public MainFrame() {
        super("Fractus");
        preferences = Preferences.userNodeForPackage(this.getClass());

        initializeControls();
        addComponentListener(this);
    }

    public void setView(JPanel panel) {
        setContentPane(panel);
        validate();
    }

    private void initializeControls() {
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        toolbar = new MainToolbar();
        container = new JPanel(true);

        add(toolbar);
        add(container);

        setPreferredSize(
                new Dimension(preferences.getInt("mainFrameWidth", 200),
                preferences.getInt("mainFrameHeight", 500)));

        pack();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        preferences.putInt("mainFrameWidth", this.getWidth());
        preferences.putInt("mainFrameHeight", this.getHeight());
        try {
            preferences.flush();
        } catch (Exception x) {
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
