package fractus.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.trolltech.qt.gui.*;

import fractus.main.Fractus;

public class IMWindow extends QWidget {

	/**
	 * @param args
	 */
	FractusTabWidget tabs;
	Fractus f;
	private Map<String, IMTab> map;
	

	public IMTab getTab(String buddy) {
		IMTab tab = map.get(buddy);
		
		return tab;
	}
	
	public IMTab createTab(Buddy buddy) {
		IMTab tab = new IMTab(f,buddy.getName());
		int index = tabs.addTab(tab, buddy.getName());
		if (buddy.isSignedOn())
			tabs.setTabIcon(index, UIManager.online);
		else
			tabs.setTabIcon(index, UIManager.offline);
		buddy.setTabIndex(index);
		buddy.setWin(this);
		map.put(buddy.getName(), tab);
		return tab;
		
	}
	
	
	public void addBuddyTab(BuddyTab t) {
		int index = tabs.addTab(t,"buddies");
	}
	
	public void switchToTab(Buddy b){
		IMTab tab = getTab(b.getName());
		if (tab == null)
			createTab(b);
		tabs.setCurrentWidget(tab);
	}
		
	
	public IMWindow(Fractus fractus) {
		this(fractus,null);
	}
		
	public IMWindow(Fractus fractus,QWidget parent) {
        super(parent);
        f = fractus;
        tabs = new FractusTabWidget();
        
        map = Collections.synchronizedMap(new HashMap<String, IMTab>());

        
        
        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(tabs);
        layout.addStretch();
        layout.setContentsMargins(0, 0, 0, 0);
        setLayout(layout);
       // tabs.resize(300, 200);
        setWindowTitle(tr("fractus chat"));
        resize(300,200);
        

	}

	public void setTabIcon(int tabIndex, QIcon icon) {
		tabs.setTabIcon(tabIndex, icon);
		
	}

}

