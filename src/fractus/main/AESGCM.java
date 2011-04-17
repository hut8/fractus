package fractus.main;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AESGCM {
	public static Provider PROVIDER = new org.bouncycastle.jce.provider.BouncyCastleProvider();
	public static int NUM_TRIES = 1000;
	
	public static void runTest(GCMBlockCipher encryptCipher, GCMBlockCipher decryptCipher, String testDataStr)
	throws Exception {
		///
		/// Encryption part
		///
		
		//System.out.println(">>> Encryption Test");
		//System.out.println("Plaintext:" + BinaryUtil.encodeData(testData));
		
		byte[] testData = testDataStr.getBytes("UTF-8");
		
		// Determine size of and allocate space for ciphertext buffer 
		int outSize = encryptCipher.getOutputSize(testData.length);
		byte[] cipherText = new byte[outSize];
//		System.out.println("Trying to encrypt test data into " + outSize + "B buffer");
//		System.out.println("Ciphertext is now:" + BinaryUtil.encodeData(cipherText));
		
		// Process bytes into ciphertext buffer
		int offset = encryptCipher.processBytes(testData, 0, testData.length, cipherText, 0);
//		System.out.println("Processed " + offset + " bytes into Ciphertext");
//		System.out.println("Ciphertext is now:" + BinaryUtil.encodeData(cipherText));
		
		// Finalize cipher
//		System.out.println("Finalizing cipher");		
		encryptCipher.doFinal(cipherText, offset);
//		System.out.println("Complete.");
//		System.out.println("Ciphertext is now:" + BinaryUtil.encodeData(cipherText));
		
//		byte[] mac = encryptCipher.getMac();
//		System.out.println("MAC: " + BinaryUtil.encodeData(mac) + " [" + mac.length + " B]");
//		System.out.println("Ciphertext is " + (cipherText.length - mac.length) + " B");
		
//		System.out.println("=========");
//		System.out.println();

		///
		/// Decryption part
		///
		
//		System.out.println(">>> Decryption Test");
	
//		System.out.println("Ciphertext is now:" + BinaryUtil.encodeData(cipherText) +
//				"Size: " + cipherText.length + " B");
		int decryptPlaintextSize = decryptCipher.getOutputSize(cipherText.length);
		byte[] decryptPlaintextBuffer = new byte[decryptPlaintextSize];
		int decryptPlaintextBufferOffset = decryptCipher.processBytes(cipherText, 0, cipherText.length, decryptPlaintextBuffer, 0);		
		decryptCipher.doFinal(decryptPlaintextBuffer, decryptPlaintextBufferOffset);
		
		if (Arrays.equals(testData, decryptPlaintextBuffer)) {
//			System.out.print(" Pass ");
		} else {
			System.out.println("Plaintext and ciphertext do not match!");
			System.exit(-1);
		}
		
		//System.out.println("Plaintext: " + new String(decryptPlaintextBuffer,"UTF-8"));
		//System.out.println();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("AES-GCM Demo");
		System.out.println("Generating test data vector of size " + NUM_TRIES);
		ArrayList<String> testDataVector = new ArrayList<String>();
		Random r = new Random();
		for (int i=0; i < NUM_TRIES; i++) {
			StringBuilder randString = new StringBuilder();
			for (int j=0; j < r.nextInt(1024); j++) {
				randString.append(UUID.randomUUID().toString());
			}
			testDataVector.add(randString.toString());
		}
			
		//System.out.println("Creating RNG");
		SecureRandom rng = new SecureRandom();

		System.out.println("Creating digest engine");
		SHA256Digest digestEngine = new SHA256Digest();
		byte[] input = "LOL".getBytes("UTF-8");
		System.out.println("Digest (key) input:" + BinaryUtil.encodeData(input));
		byte[] digest = new byte[256/8];
		digestEngine.update(input, 0, input.length);
		digestEngine.doFinal(digest, 0);
		System.out.println("Digest (key) output:" + BinaryUtil.encodeData(digest)
				+ "Size: " + (digest.length*8) + " bits");
	
		System.out.println("Creating key parameters");
		CipherParameters keyParameters = new KeyParameter(digest);
		
		System.out.println("Creating initialization vector");
		byte[] iv = new byte[12];
		rng.nextBytes(iv);
		
		System.out.println("Creating cipher parameters");
		ParametersWithIV parameters = new ParametersWithIV(keyParameters, iv);
		
		// Make forward cipher
		System.out.println("Creating and initializing forward cipher");
		GCMBlockCipher encryptCipher = new GCMBlockCipher(new AESFastEngine());
		encryptCipher.init(true, parameters);
		
		// Make reverse cipher
		System.out.println("Creating and initializing reverse cipher");
		GCMBlockCipher decryptCipher = new GCMBlockCipher(new AESFastEngine());
		decryptCipher.init(false, parameters);
				
		for (int i=0; i < NUM_TRIES; i++) {
			System.out.println('.');
			runTest(encryptCipher, decryptCipher, testDataVector.get(i));
		}
		System.out.println("Done.");
		System.out.println();
	}

}
