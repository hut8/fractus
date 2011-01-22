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
	QTabWidget tabs;
	Fractus f;
	private Map<String, IMTab> map;
	
	
	
	public void quit() {
		System.exit(0);
	}
	
	public synchronized void receiveMessage(final String buddy, final String message) {
		Runnable r = new Runnable() {
			
			public void run() {
				IMTab tab = map.get(buddy);
				if (tab == null) {
					tab = new IMTab(f,buddy);
					int index = tabs.addTab(tab, buddy);
					map.put(buddy, tab);
				}
				tab.receiveMessage(message);
			}
		};
		QApplication.invokeLater(r);
	
		
	}
	
	
	public IMWindow(Fractus fractus) {
		this(fractus,null);
	}
	
	public IMWindow(Fractus fractus,QWidget parent) {
        super(parent);
        f = fractus;
        tabs = new QTabWidget();
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

}

