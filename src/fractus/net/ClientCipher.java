package fractus.net;


import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 * @author bowenl2
 */
public class ClientCipher {

    private boolean initialized;
    public boolean isInitialized() {
        return initialized;
    }
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private ECPoint remotePublicPoint;
    private ECPublicKey remotePublicKey;
    private SecretKeySpec secretKeySpec;
    
    // AES-GCM
    private GCMBlockCipher encryptCipher;
    private byte[] encryptNonce;
    private GCMBlockCipher decryptCipher;
    private byte[] decryptNonce;
    
    private static Logger log;

    static {
        log = Logger.getLogger(ClientCipher.class.getName());
    }

    public ClientCipher() {
        this.initialized = false;
    }

    public void negotiate(String keyEncoding, byte[] remotePublicKey, byte[] remoteNonce)
    throws GeneralSecurityException {
                if (!"X.509".equals(keyEncoding)) {
            log.warn("Could not recognize that key encoding [Not X.509]: " + keyEncoding);
            throw new GeneralSecurityException("Key not X.509 (Unsupported)");
        }

        log.debug("Trying to derive secret key from ours and " + remotePublicKey);

        // Create their public key object for ECDH
        X509EncodedKeySpec ks = new X509EncodedKeySpec(remotePublicKey);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        try {
            this.remotePublicKey = (ECPublicKey)kf.generatePublic(ks);
        } catch (ClassCastException ex) {
            log.warn("Not given an EC Public Key!", ex);
            return;
        }

        this.remotePublicPoint = this.remotePublicKey.getQ();

        // Extract CipherParameters
        this.secretKeySpec = EncryptionManager.getInstance().deriveKey(this.remotePublicKey);

        // TODO Create ParametersWithIV
        
        // TODO Create Encryption Cipher
        // TODO Create Decryption Cipher


        initialized = true;
    }

    public byte[] encrypt(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
        return encryptCipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
        return decryptCipher.doFinal(ciphertext);
    }
}
