package fractus.ui;

import fractus.main.Fractus;

public class DemoThread implements Runnable {
	
	Fractus f;
	
	 public DemoThread(Fractus f) {
		 this.f = f;
	 }
	
	 public void run() {
         try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
         f.receiveMessage("test", "test message");
         
     }


}
