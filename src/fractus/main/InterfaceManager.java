package fractus.main;

public class InterfaceManager {
	public enum InterfaceType { CLI, TUI, GUI };
	private InterfaceType interfaceType;
	
	public InterfaceManager(InterfaceType interfaceType) {
		this.interfaceType = interfaceType;
	}
	
	public void run() {
		
	}
}
