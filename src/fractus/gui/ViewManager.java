/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fractus.gui;

import java.io.IOException;
import org.apache.log4j.Logger;
import fractus.main.ContactManager;
import fractus.main.DelegateMethod;
import fractus.main.EventData;
import fractus.main.Fractus;

/**
 *
 * @author bowenl2
 */
public class ViewManager {
    private final Fractus fractus;
    private MainFrame mainFrame;
    private OverviewPanel overviewPanel;
    private ContactListPanel contactListPanel;
    private DelegateMethod<EventData> contactDataDelegate;

    private static Logger log;
    static {
        log = Logger.getLogger(ViewManager.class.getName());
    }

    public ViewManager(Fractus fractus) throws IOException {
        this.fractus = fractus;

        log.debug("View Manager Constructor");

        contactDataDelegate = new DelegateMethod<EventData>() {

            @Override
            public void invoke(EventData delegateData) {
                mainFrame.setView(ViewManager.this.fractus.getContactManager().isValid()
                        ? contactListPanel : overviewPanel);
            }
        };

        log.debug("Making Main Frame");
        mainFrame = new MainFrame();
        log.debug("Making Overview Panel");
        overviewPanel = new OverviewPanel(fractus);
        log.debug("Making Contact List Panel");
        contactListPanel = new ContactListPanel(fractus);


        fractus.getContactManager().getIsValidDelegate().addTarget(contactDataDelegate);

        mainFrame.setView(ViewManager.this.fractus.getContactManager().isValid()
                ? contactListPanel : overviewPanel);

        mainFrame.setVisible(true);
        log.debug("Done View Manager Constructor");
    }

    public OverviewPanel getOverviewPanel() {
        return overviewPanel;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
