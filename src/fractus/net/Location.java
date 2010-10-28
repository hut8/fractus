package fractus.net;

public class Location {
	private String address;
	private Integer port;
	
	public Location(String address, Integer port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Integer getPort() {
		return port;
	}
}
