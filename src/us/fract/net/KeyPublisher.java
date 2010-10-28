package us.fract.net;

import org.apache.log4j.Logger;
import us.fract.protobuf.ProtocolBuffer;

/**
 * Responsible for publishing public key to server with
 * given authentication credentials
 * @author bowenl2
 */
public class KeyPublisher
        implements Runnable {

    private UserCredentials userCredentials;
    private ServerConnection serverConnection;
    private static Logger log;

    static {
        log = Logger.getLogger(KeyPublisher.class.getName());
    }

    public KeyPublisher(UserCredentials userCredentials,
            ServerConnection serverConnection) {
        this.userCredentials = userCredentials;
        this.serverConnection = serverConnection;
    }

    @Override
    public void run() {
        while (true) {
            log.debug("Publishing key to server");

            log.debug("Constructing Register Key Request");
            ProtocolBuffer.RegisterKeyReq registerKeyReq =
                    ProtocolBuffer.RegisterKeyReq.newBuilder().setUsername(userCredentials.getUsername()).setPassword(userCredentials.getPassword()).build();
            FractusMessage registerKeyMessage =
                    FractusMessage.build(registerKeyReq);

            log.debug("Sending message to Server Connector...");
            serverConnection.sendMessage(registerKeyMessage);

            try {
                Thread.sleep(1000 * 60 * 60);
            } catch (InterruptedException ex) { }
        }
    }
}
