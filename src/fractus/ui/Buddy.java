package fractus.ui;

import com.trolltech.qt.gui.QIcon;

public class Buddy {
	
	private boolean signedOn;
	private String name; 
	private IMTab tab;
	private IMWindow win;
	private int tabIndex;
	
	public Buddy(String name){
		this.name = name;
		signedOn = false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isSignedOn() {
		return signedOn;
	}
	
	public void setSignedOn(boolean status){
		signedOn = status;
		
	}
	
	public void setTab(IMTab t) {
		tab = t;
	}
	
	public IMTab getTab() {
		return tab;
	}
	
	public void setWin(IMWindow w) {
		win = w;
	}
	public IMWindow getWin() {
		return win;
	}

	public void signTab(QIcon icon) {
		if (win != null && tab != null) {
			win.setTabIcon(tabIndex,icon);
		}
		
	}

	public void setTabIndex(int index) {
		tabIndex = index;		
	}
	public int getTabIndex() {
		return tabIndex;
	}

}
