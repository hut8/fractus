package fractus.ui;

import fractus.main.Fractus;

public class DemoThread implements Runnable {
	
	Fractus f;
	
	 public DemoThread(Fractus f) {
		 this.f = f;
	 }
	
	 public void run() {
		 UIManager winman = f.getWinman();
		 winman.addBuddy("test");
		 winman.addBuddy("test2");
		 winman.signOnBuddy("test2");
		 winman.addBuddy("test3");
         try {
			Thread.sleep(3000);
			 f.receiveMessage("test", "test message");
			 Thread.sleep(3000);
			 winman.signOnBuddy("test");
			 f.receiveMessage("test2", "test 2 message");
			 Thread.sleep(3000);
			 winman.signOffBuddy("test2");
			 f.receiveMessage("test", "test 3 message");
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
        
         
     }


}
