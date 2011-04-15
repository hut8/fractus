package fractus.main;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import fractus.net.EncryptionManager;

public class ECDHKeyGenerator {
	public static KeyPair generateECDHKeyPair()
	throws GeneralSecurityException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(EncryptionManager.ELLIPTIC_CURVE);
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDH", "BC");
        g.initialize(ecSpec, new SecureRandom());
        return g.generateKeyPair();
	}
	
}
