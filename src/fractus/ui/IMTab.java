package fractus.ui;

import sun.tools.jstat.Alignment;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

import fractus.main.Fractus;

public class IMTab extends QWidget {

	/**
	 * @param args
	 */
	 private QTextEdit textViewer;
	 private QLineEdit entryLE;
	 String buddy;
	
	 Fractus f;
	
	public void sendmessage() {
		
		String text = entryLE.text();
		textViewer.append(f.getme()+":"+text);
		entryLE.setText("");
		f.sendMessage(buddy, text);
		
	}
	
	public void receiveMessage(String message) {
		textViewer.append(buddy+":"+message);
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
        
        QGridLayout layout = new QGridLayout();
        layout.addWidget(textViewer, 0, 0);
        layout.addWidget(entryLE, 1, 0);
        layout.setContentsMargins(5, 0, 5, 5);
        setLayout(layout);
        
        
       

	}

}

