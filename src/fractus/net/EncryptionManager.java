package fractus.net;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.Executor;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.Base64;

import fractus.main.ECDHKeyGenerator;


public class EncryptionManager {
	
	private static EncryptionManager instance = new EncryptionManager();
	public static EncryptionManager getInstance() {
		return instance;
	}

    public static final String ELLIPTIC_CURVE = "secp521r1";
    private KeyPair dsaKeyPair;
    private KeyPair dhKeyPair;
    private String encodingType;
    private String encodedDsaPublicKey;
    private String encodedDhPublicKey;
    private ECDHBasicAgreement agreement;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private EncryptionManager() {
    }
    
    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    private boolean keyPairReady = false;

    public boolean isKeyPairReady() {
        return keyPairReady;
    }

    public void initialize(Executor c) {
        c.execute(new Runnable() {
            @Override
            public void run() {
                Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
                Security.addProvider(provider);
                try {
                    dhKeyPair = ECDHKeyGenerator.generateECDHKeyPair();
                    generateFields();
                    propertyChangeSupport.firePropertyChange("keyPairReady", false, true);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    System.exit(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        });
    }

    public EncryptionManager(String keyfile)
            throws IOException,
            ClassNotFoundException,
            GeneralSecurityException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyfile));
        dhKeyPair = (KeyPair) ois.readObject();
        generateFields();
    }

    private void generateFields()
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Base64.encode(dhKeyPair.getPublic().getEncoded(), baos);

        encodedDsaPublicKey = baos.toString("UTF-8");
        encodingType = dhKeyPair.getPublic().getFormat();

        // Reference Private Key
        ECPrivateKey privKey = (ECPrivateKey) dhKeyPair.getPrivate();

        // Initialize ECDH
        agreement = new ECDHBasicAgreement();
        ECParameterSpec spec = privKey.getParameters();
        ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
        ECPrivateKeyParameters pkp = new ECPrivateKeyParameters(privKey.getD(), dp);
        agreement.init(pkp);
    }

    public byte[] getPublicKey() {
        return dhKeyPair.getPublic().getEncoded();
    }

    public SecretKeySpec deriveKey(CipherParameters cp) {
        BigInteger bi = agreement.calculateAgreement(cp);
        byte[] key = bi.toByteArray();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.reset();
        byte[] hash = digest.digest(key);

        return new SecretKeySpec(hash, 0, hash.length, "AES");
    }

    public String getEncodingFormat() {
        return encodingType;
    }

    public String getEncodedKey() {
        return encodedDsaPublicKey;
    }

    public SecretKeySpec deriveKey(ECPublicKey pubkey) {
        ECParameterSpec spec = pubkey.getParameters();
        ECDomainParameters dp = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN(), spec.getH(), spec.getSeed());
        ECPublicKeyParameters pkp = new ECPublicKeyParameters(pubkey.getQ(), dp);
        return deriveKey(pkp);
    }

    public static byte[] decrypt(byte[] ciphertext, SecretKeySpec sks) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Create cipher
        Cipher inCipher = null;
        try {
            inCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        }

        inCipher.init(Cipher.DECRYPT_MODE, sks);
        return inCipher.doFinal(ciphertext);
    }

    public static byte[] encrypt(byte[] plaintext, SecretKeySpec sks)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher outCipher = null;
        try {
            outCipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        outCipher.init(Cipher.ENCRYPT_MODE, sks);


        // 	Encrypt with symmetric key
        byte[] cipherData = null;
        try {
            cipherData = outCipher.doFinal(plaintext);
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cipherData;
    }
}
