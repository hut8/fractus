package fractus.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

import fractus.main.Fractus;

public class BuddyTab extends QWidget {

	/**
	 * @param args
	 */
	 private QTableView buddyViewer;
	 
	
	
	 UIManager winman;
	 private Map<String, QStandardItem> map;


	private QStandardItemModel model;
	
	public void doubleclick() {
		String clickedon = (String) buddyViewer.currentIndex().data();
		//System.out.println("doubleclicked"+clickedon);
		winman.switchToTab(clickedon);
	}
		
	public void signBuddy(final String buddy, final QIcon icon) {
		Runnable r = new Runnable() {
			public void run() {
				QStandardItem item = map.get(buddy);
				if (item == null)
					return;
				item.setIcon(icon);
			}
		};
		QApplication.invokeLater(r);
	}
	public void addBuddy(final String buddy, final QIcon icon) {
		Runnable r = new Runnable() {
			public void run() {
				QStandardItem b = new QStandardItem(buddy);
				b.setIcon(icon);
				b.setEditable(false);
				model.appendRow(b);
				map.put(buddy, b);
			}
		};
		QApplication.invokeLater(r);
		
	}		
		
	public BuddyTab(UIManager f) {
		this(null,f);
	}
	
	public BuddyTab(QWidget parent,UIManager fractus) {
        super(parent);
        winman = fractus;
        map = new HashMap<String, QStandardItem>();
       
        buddyViewer = new QTableView();
        buddyViewer.setAutoScroll(true);
        buddyViewer.setShowGrid(false);
        buddyViewer.resizeColumnsToContents();
        model = new QStandardItemModel(0, 1, this);
        
        buddyViewer.horizontalHeader().hide();
        buddyViewer.verticalHeader().hide();
        
        buddyViewer.setModel(model);
        
        buddyViewer.doubleClicked.connect(this, "doubleclick()");
        
        QVBoxLayout layout = new QVBoxLayout();
        layout.addWidget(buddyViewer);
        layout.setContentsMargins(5, 0, 5, 5);
        setLayout(layout);
        
        
       

	}

}

