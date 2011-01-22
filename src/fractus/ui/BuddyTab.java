package fractus.ui;

import sun.tools.jstat.Alignment;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

import fractus.main.Fractus;

public class BuddyTab extends QWidget {

	/**
	 * @param args
	 */
	 private QTableView buddyViewer;
	 
	
	
	 Fractus f;



	private QStandardItemModel model;
	
	public void doubleclick() {
		String clickedon = (String) buddyViewer.currentIndex().data();
		//System.out.println("doubleclicked"+clickedon);
		f.switchToTab(clickedon);
	}
	
	public void addBuddy(final String buddy) {
		Runnable r = new Runnable() {
			
			public void run() {
				//QStandardItem i = new QStandardItem("off");
				//i.setEditable(false);
				QStandardItem b = new QStandardItem(buddy);
				
				
				b.setEditable(false);
				model.appendRow(b);
				
				
			}
		};
		QApplication.invokeLater(r);
		
	}
		
		
	public BuddyTab(Fractus f) {
		this(null,f);
	}
	
	public BuddyTab(QWidget parent,Fractus fractus) {
        super(parent);
        f = fractus;
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

