package fractus.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

import java.util.ArrayList;
import org.apache.log4j.Logger;


public class NetListener implements Runnable {
	private final static Logger log;
	static {
        log = Logger.getLogger(NetListener.class.getName());
    }
	
    private ServerSocket serverSocket;
    
    public NetListener() { }

    public int getListenPort() {
        if (serverSocket == null) {
            return -1;
        }
        return serverSocket.getLocalPort();
    } 

    @Override
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                FractusConnector newconnection =
                        new FractusConnector(clientSocket);
                new Thread(newconnection).start();
            } catch (IOException e) {
                log.debug("Client AcceptConnection failed");
            }
        }
    }
}
