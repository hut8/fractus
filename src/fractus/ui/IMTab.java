package fractus.ui;

import java.sql.Date;
import java.sql.Time;

import sun.tools.jstat.Alignment;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import fractus.main.Fractus;

public class IMTab extends QWidget {

	/**
	 * @param args
	 */
	 private QTextEdit textViewer;
	 private QLineEdit entryLE;
	 String buddy;
	
	 Fractus f;
	 
	 public String getTime() {
		return UIManager.now();
		
	 }
	
	public void sendmessage() {
		
		String text = entryLE.text();
		textViewer.append(f.getme()+" ("+getTime()+"): "+text);
		entryLE.setText("");
		f.sendMessage(buddy, text);
		
	}
	
	public void receiveMessage(String message) {
		textViewer.append(buddy+" ("+getTime()+"): "+message);
	}
	
		
	public IMTab(Fractus f, String buddy) {
		this(null,f,buddy);
	}
	
	public IMTab(QWidget parent,Fractus fractus, String buddy) {
        super(parent);
        f = fractus;
        this.buddy = buddy;
        textViewer = new QTextEdit();
       // textViewer.resize(200, 150);
        textViewer.setReadOnly(true);
        
      
        entryLE = new QLineEdit();
        entryLE.setFocus();
        
        entryLE.setAlignment(Qt.AlignmentFlag.AlignRight);
        //resize(300,200);
        entryLE.returnPressed.connect(this, "sendmessage()");
        
        QVBoxLayout layout = new QVBoxLayout();
       
        layout.addWidget(textViewer);
        //layout.addStretch(1);
        layout.addWidget(entryLE);
        layout.setContentsMargins(5, 0, 5, 5);
        setLayout(layout);
        
        
       

	}

}

