package fractus.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.*;


import fractus.main.Fractus;

public class IMWindow extends QWidget {

	/**
	 * @param args
	 */
	FractusTabWidget tabs;
	Fractus f;
	private Map<String, IMTab> map;
	private UIManager manager;
	

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
		QPushButton quit = new QPushButton(this);
		quit.setIcon(UIManager.closeIcon);
		quit.setFlat(true);
	    quit.setIconSize(new QSize(10,10));
	    quit.resize(10,	 10);
	    quit.clicked.connect(buddy, "closeTab()");
		tabs.getQTabBar().setTabButton(index, QTabBar.ButtonPosition.RightSide, quit);
		buddy.setWin(this);
		map.put(buddy.getName(), tab);
		return tab;
		
	}
	
	public void removeTab(Buddy buddy, int i) {
		
		tabs.removeTab(i);
		map.remove(buddy.getName());
		for (String name: map.keySet()) {
			Buddy b = manager.getBuddy(name);
			if (b.getTabIndex() > i)
				b.decrementTabIndex();
		}
		
	}
	
	
	public void addBuddyTab(BuddyTab t) {
		int index = tabs.addTab(t,"buddies");
		
	}
	
	public void switchToTab(Buddy b){
		IMTab tab = getTab(b.getName());
		
		if (tab == null) {
			tab = createTab(b);
			
		}
		tabs.setCurrentWidget(tab);
	}
		
	
	public IMWindow(Fractus fractus, UIManager man) {
		this(fractus,man, null);
	}
		
	public IMWindow(Fractus fractus,UIManager man,QWidget parent) {
        super(parent);
        f = fractus;
        manager = man;
        tabs = new FractusTabWidget();
        //tabs.getQTabBar().setTabsClosable(true);
       
        map = Collections.synchronizedMap(new HashMap<String, IMTab>());

        
        
        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(tabs);
        
        layout.setContentsMargins(0, 0, 0, 0);
        setLayout(layout);
       
        setWindowTitle(tr("fractus chat"));
        
        resize(300,200);
        
        

	}
	
	public void closeEvent(QCloseEvent q) {
		super.closeEvent(q);
		
		System.exit(0);
	}

	public void setTabIcon(int tabIndex, QIcon icon) {
		tabs.setTabIcon(tabIndex, icon);
		
	}

}

