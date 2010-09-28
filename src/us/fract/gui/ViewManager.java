/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.fract.gui;

import java.io.IOException;
import us.fract.main.ContactManager;
import us.fract.main.DelegateMethod;
import us.fract.main.EventData;
import us.fract.main.Fractus;

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

    public ViewManager(Fractus fractus) throws IOException {
        this.fractus = fractus;

        contactDataDelegate = new DelegateMethod<EventData>() {

            @Override
            public void invoke(EventData delegateData) {
                mainFrame.setView(ViewManager.this.fractus.getContactManager().isValid()
                        ? contactListPanel : overviewPanel);
            }
        };


        mainFrame = new MainFrame();
        overviewPanel = new OverviewPanel(fractus);
        contactListPanel = new ContactListPanel(fractus);

        fractus.getContactManager().getIsValidDelegate().addTarget(contactDataDelegate);

        mainFrame.setView(ViewManager.this.fractus.getContactManager().isValid()
                ? contactListPanel : overviewPanel);

        mainFrame.setVisible(true);
    }

    public OverviewPanel getOverviewPanel() {
        return overviewPanel;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
