package us.fract.connection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import us.fract.main.ProtocolException;
import us.fract.net.Callback;
import us.fract.net.FractusMessage;
import us.fract.net.FractusPacket;
import us.fract.net.PublicKeyDirectory;
import us.fract.net.UserCredentials;


public class PacketHandler {
	private PublicKeyDirectory publicKeyDirectory;
	
	public PacketHandler() {
            
	}
}
