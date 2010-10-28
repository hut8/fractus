package us.fract.net;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;


public interface Callback {
    void dispatch(String sender, Element message, FractusConnector fc) throws NumberFormatException, IOException, NoSuchAlgorithmException, NoSuchPaddingException;
}
