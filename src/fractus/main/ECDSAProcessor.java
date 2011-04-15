package fractus.main;

import java.security.KeyPair;

import fractus.net.FractusPacket;

/**
 * Signs and verifies signatures of incoming FractusPackets
 * @author 14581
 *
 */
public class ECDSAProcessor {
	private KeyPair keyPair;

	public ECDSAProcessor(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	
	public void signPacket() {
		
	}
	
	public boolean verifySignature(FractusPacket fractusPacket) {
		
		return false;
	}
	
}
