package us.fract.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import us.fract.connection.EncryptionManager;


public class FractusPacket {
	private SecretKeySpec sks;
	private byte[] message;
	private String encodedPublicKey;
	
	/* Each packet sent to one another is this */
	public FractusPacket(byte[] message, SecretKeySpec sks) {
		this.message = message;
		this.sks = sks;
	}
	
	public byte[] getMessage() {
		return message;
	}
	
	public String getEncodedKey() {
		return encodedPublicKey;
	}
	
	
	public FractusPacket(SecretKeySpec sks) {
		this.sks = sks;
	}
	
	private static final byte[] serializeInt(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
	}

	
	private static final int deserializeInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
	}

	
	/**
	 * Creates byte stream to be sent directly to socket
	 * @return
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 */
	public byte[] serialize() throws
	NoSuchAlgorithmException,
	NoSuchPaddingException,
	InvalidKeyException,
	IOException {
		Cipher outCipher = null;
		try {
			outCipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		outCipher.init(Cipher.ENCRYPT_MODE, sks);
		
		// 	Encrypt with symmetric key
		byte[] cipherData = null;
		try {
			cipherData = outCipher.doFinal(message);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Logger.getAnonymousLogger().log(Level.INFO, "Encrypting cipher data of " + cipherData.length + " bytes");
		
		// Wrap everything
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(serializeInt(cipherData.length));
		baos.write(cipherData);
		return baos.toByteArray();
	}
	
	private static void log(String msg) {
		System.out.println("FractusPacket: " + msg);
	}
	
	public void readPacket(InputStream input, String encodedKey, EncryptionManager em)
	throws InvalidKeyException,
	NoSuchAlgorithmException,
	NoSuchPaddingException,
	IllegalBlockSizeException,
	BadPaddingException, IOException {
		this.encodedPublicKey = encodedKey;
		
		// Read the length
		byte[] msglenBytes = new byte[4];
		int msglen;
		byte[] msgBuff;
		if (input.read(msglenBytes) != 4) {
			log("error: got wrong size of message length");
			return;
		}
		msglen = deserializeInt(msglenBytes);
		log("allocating and filling buffer of " + msglen + " bytes");
		msgBuff = new byte[msglen];
		log("waiting for message...");

		// Receive crypto-blob
		input.read(msgBuff);
		Logger.getAnonymousLogger().log(Level.INFO, "received bytes: " + new String(Base64.encode(msgBuff)));

		// msgBuff now has an encrypted fractus packet
		message = decryptMessage(msgBuff);
		Logger.getAnonymousLogger().log(Level.INFO, "decrypted as: " + new String(message, "UTF-8"));
	}
	
	private byte[] decryptMessage(byte[] ciphertext)
	throws UnsupportedEncodingException,
	NoSuchAlgorithmException,
	NoSuchPaddingException,
	InvalidKeyException,
	IllegalBlockSizeException,
	BadPaddingException {
		// Create cipher
		Cipher inCipher = null;
		try {
			inCipher = Cipher.getInstance("AES/ECB/PKCS7Padding","BC");
		} catch (NoSuchProviderException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		
		inCipher.init(Cipher.DECRYPT_MODE, sks);
		Logger.getAnonymousLogger().log(Level.INFO, "Decrypting ciphertext bytes of length " + ciphertext.length);
		return inCipher.doFinal(ciphertext);
		
	}	
}
