package fractus.net;

public class UserCredentials {
	public UserCredentials (String username, String password) {
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	private String username;
	private String password;
}
