/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.strategy;

import com.google.protobuf.InvalidProtocolBufferException;
import fractus.net.ProtocolBuffer;
import fractus.net.ServerConnection;
import org.apache.log4j.Logger;


/**
 *
 * @author bowenl2
 */
public class RegisterKeyResStrategy
implements PacketStrategy {
    private ServerConnection serverConnection;

    private static Logger log = Logger.getLogger(RegisterKeyResStrategy.class.getName());

    public RegisterKeyResStrategy(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void dispatch(byte[] contents) {
        log.debug("Dispatching strategy for register key response: [" + contents.length + " B]");
        ProtocolBuffer.RegisterKeyRes response;
        try {
             response = ProtocolBuffer.RegisterKeyRes.parseFrom(contents);
             log.debug("Parsed successfully");
        } catch (InvalidProtocolBufferException ex) {
            log.warn("Could not parse response.  Exiting strategy.");
            return;
        }

        
    }

}
