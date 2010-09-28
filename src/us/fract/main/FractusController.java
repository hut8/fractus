package us.fract.main;

public interface FractusController {
	void shutdown();
	void initialize(String serverAddress, Integer port,  String username, String password);
}
