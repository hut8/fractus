package fractus.crypto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;


import fractus.main.BinaryUtil;
import fractus.net.ServerConnector;

public class PublicKeyDirectory {
	private final static Logger log;
	static {
		log = Logger.getLogger(PublicKeyDirectory.class);
	}
	
	private Map<ECPoint, String> keyUserMap;
	private ServerConnector serverConnector;

	public PublicKeyDirectory(ServerConnector serverConnector) {
		this.serverConnector = serverConnector;
		this.keyUserMap = new HashMap<ECPoint, String>();
	}
	
	public synchronized String identifyKey(byte[] encodedKey)
	throws InvalidKeySpecException {
		log.debug("Identifying key material: " + BinaryUtil.encodeData(encodedKey));
		// Convert public key data to ECPoint
		X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedKey);
		KeyFactory kf;
		try {
			kf = java.security.KeyFactory.getInstance("ECDH");
		} catch (NoSuchAlgorithmException e) {
			log.error("Cryptography error: could not initialize ECDH keyfactory!", e);
			throw new RuntimeException(e);
		}
		ECPublicKey remotePublicKey = (ECPublicKey)kf.generatePublic(ks);
		ECPoint remotePoint = remotePublicKey.getQ();
		log.debug("Computed target Q point from given ECPublicKey");
		// Look in local table
		if (!keyUserMap.containsKey(remotePoint)) {
			String authoritiveAnswer = null;
			try {
				authoritiveAnswer = this.serverConnector.identifyKey(encodedKey);
			} catch (Exception e) {
				log.warn("Could not retrieve authoritive answer of public key from server",e);
			}
			if (authoritiveAnswer == null) {
				log.info("Could not identify public key: " + BinaryUtil.encodeData(encodedKey));
				return null;
			}
			keyUserMap.put(remotePoint, authoritiveAnswer);
		}
		return keyUserMap.get(remotePoint);
	}
}