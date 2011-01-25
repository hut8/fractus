package fractus.ui;

import java.util.HashMap;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPixmap;

import fractus.main.Fractus;


public class UIManager {
	
	private HashMap<String, Buddy> map;
	private IMWindow window;
	private BuddyTab buddytab;
	private Fractus f;
	public static QIcon offline = new QIcon(new QPixmap("lib/offline.png"));
	public static QIcon online = new QIcon(new QPixmap("lib/online.png"));
	
	public UIManager(Fractus f) {
		 
		map = new HashMap<String,Buddy>();
		window = new IMWindow(f);
    	window.show();
	    window.raise();
	    buddytab = new BuddyTab(this);
	    window.addBuddyTab(buddytab);
	}
	
	private Buddy getBuddy(String buddy) {
		return map.get(buddy);
	}
	

	public void sendMessage(String buddy, String message) {
		f.sendMessage(buddy, message);
	}
	public Buddy addBuddy(String buddy) {
		Buddy newBuddy = new Buddy(buddy);
		newBuddy.setWin(window);
		map.put(buddy, newBuddy);
		buddytab.addBuddy(buddy,offline);
		return newBuddy;
		
	}
	public void signOnBuddy(final String buddy) {

		Runnable r = new Runnable() {
			
			public void run() {
				buddytab.signBuddy(buddy,online);
				Buddy b = getBuddy(buddy);
				b.setSignedOn(true);
				b.signTab(online);
			}
		};
		QApplication.invokeLater(r);
	}
	
	public void signOffBuddy(final String buddy) {
		Runnable r = new Runnable() {
			
			public void run() {
				buddytab.signBuddy(buddy,offline);
				Buddy b = getBuddy(buddy);
				b.setSignedOn(false);
				b.signTab(offline);
			}
		};
		QApplication.invokeLater(r);
	}
	
	
    public void receiveMessage(final String buddy, final String message) {
    
		Runnable r = new Runnable() {
			
			public void run() {
				Buddy b = getBuddy(buddy);
				if (b == null)
					b = addBuddy(buddy);
				IMTab tab = b.getTab();
				if (tab == null) {
					tab = window.createTab(b);
					b.setTab(tab);
				}
				tab.receiveMessage(message);
			}
		};
		QApplication.invokeLater(r);
	
		
	}
    
    public void switchToTab(String buddy){
		Buddy b = getBuddy(buddy);
		window.switchToTab(b);
	}

}
