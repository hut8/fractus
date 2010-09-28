package us.fract.net;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;


import us.fract.main.ProtocolException;

public class PublicKeyDirectory {
	private HashMap<String, String> keyOwnerMap;
	private ServerConnection sfc;

	public PublicKeyDirectory(ServerConnection sfc) {
		keyOwnerMap = new HashMap<String,String>();
		this.sfc = sfc;
	}

	public String identifyKeyOwner(String encodedKey) {
		if (keyOwnerMap.containsKey(encodedKey)) {
			Logger.getAnonymousLogger().log(Level.INFO,"identified key [" + encodedKey + "] belonging to: " + keyOwnerMap.get(encodedKey));
			return keyOwnerMap.get(encodedKey);
		} else {
			/* if not null, add to map */
			Logger.getAnonymousLogger().log(Level.INFO,"key cache miss: asking server to identify key [" + encodedKey + "]");
			String serverId = null;
			try {
				serverId = sfc.identifyKey(encodedKey);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (serverId != null) {
				synchronized(this) {
					keyOwnerMap.put(encodedKey, serverId);
				}
			}
			return serverId; 
		}
	}

	public synchronized void addKey(String username, String key) {
		keyOwnerMap.put(key, username);
	}

}