package us.fract.net;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.util.encoders.Base64;



public class PeerCryptoData {
	private String encodedKey;
	private SecretKeySpec sks;
	
	private static void log(String msg) {
		System.out.println("PeerCryptoData: " + msg);
	}
	
	public SecretKeySpec getSecretKeySpec() {
		return sks;
	}
	
	public static PeerCryptoData negotiate(String keyEncoding, byte[] remotePublicKey, EncryptionManager em)
	throws NoSuchAlgorithmException,
	NoSuchProviderException,
	InvalidKeySpecException {
		PeerCryptoData pcd = new PeerCryptoData();

		if (keyEncoding == null || !keyEncoding.equals("X.509")) {
			log("error: could not recognize that key encoding.");
			return null;
		}
		
		// Create their public key object for ECDH
		KeySpec ks = new X509EncodedKeySpec(remotePublicKey);
		ECPublicKey pubkey;
			KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
			pubkey = (ECPublicKey) kf.generatePublic(ks);
		
		// Initialize cipher
		pcd.sks = em.deriveKey(pubkey);
		return pcd;
	}
	
	public String getEncodedKey() {
		return encodedKey;
	}
}
