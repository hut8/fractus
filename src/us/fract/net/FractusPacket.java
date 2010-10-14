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
    private byte[] message;

    /**
     * Each packet sent to remote host
     */
    public FractusPacket(byte[] message) {
        this.message = message;
    }
    
    public byte[] getMessage() {
        return message;
    }

    private static final byte[] serializeInt(int value) {
        return new byte[]{
                    (byte) (value >>> 24),
                    (byte) (value >>> 16),
                    (byte) (value >>> 8),
                    (byte) value};
    }

    private static final int deserializeInt(byte[] b) {
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
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(serializeInt(message.length));
        baos.write(message);
        return baos.toByteArray();
    }

    private static void log(String msg) {
        System.out.println("FractusPacket: " + msg);
    }

    private FractusPacket() {
    }

    public static FractusPacket readPacket(InputStream input)
            throws IOException {
        FractusPacket fp = new FractusPacket();
        // Read the length
        byte[] msglenBytes = new byte[4];
        int msglen;
        byte[] msgBuff;
        if (input.read(msglenBytes) != 4) {
            log("Received invalid message length (not 32-bit integer)");
            return null;
        }

        msglen = deserializeInt(msglenBytes);
        log("allocating and filling buffer of " + msglen + " bytes");
        msgBuff = new byte[msglen];
        log("waiting for message...");

        // Receive crypto-blob
        input.read(fp.message);
        Logger.getAnonymousLogger().log(Level.INFO, "received bytes: " + new String(Base64.encode(msgBuff)));

        return fp;
    }
}
