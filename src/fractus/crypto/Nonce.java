package fractus.crypto;

public class Nonce {
	private byte[] data;
	
	public Nonce(byte[] data) {
		if (data.length != 12) {
			
		}
		this.data = data;
	}
	
	public byte[] toByteArray() {
		return data;
	}

}
