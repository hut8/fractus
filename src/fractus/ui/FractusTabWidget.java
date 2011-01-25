package fractus.ui;

import com.trolltech.qt.gui.QTabBar;
import com.trolltech.qt.gui.QTabWidget;

public class FractusTabWidget extends QTabWidget {
	
	public QTabBar getQTabBar() {
		return this.tabBar();
	}

}
