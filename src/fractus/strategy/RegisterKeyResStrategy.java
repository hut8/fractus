///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package fractus.strategy;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//import fractus.net.ProtocolBuffer;
//import fractus.net.ProtocolBuffer.RegisterKeyRes.ResponseCode;
//import fractus.net.ServerConnector;
//import javax.swing.JDialog;
//import org.apache.log4j.Logger;
//
//
///**
// *
// * @author bowenl2
// */
//public class RegisterKeyResStrategy
//implements PacketStrategy {
//    private ServerConnector serverConnection;
//
//    private static Logger log = Logger.getLogger(RegisterKeyResStrategy.class.getName());
//
//    public RegisterKeyResStrategy(ServerConnector serverConnection) {
//        this.serverConnection = serverConnection;
//    }
//
//    @Override
//    public void dispatch(byte[] contents) {
//        log.debug("Dispatching strategy for register key response: [" + contents.length + " B]");
//        ProtocolBuffer.RegisterKeyRes response;
//        try {
//             response = ProtocolBuffer.RegisterKeyRes.parseFrom(contents);
//             log.debug("Parsed successfully");
//        } catch (InvalidProtocolBufferException ex) {
//            log.warn("Could not parse response.  Exiting strategy.");
//            return;
//        }
//        ResponseCode code = response.getCode();
//
//        if (code.equals(ResponseCode.AUTHENTICATION_FAILURE)) {
//            
//        }
//
//        if (code.equals(ResponseCode.DUPLICATE_KEY)) {
//            // Someone else has this key
//        }
//
//        if (code.equals(ResponseCode.INTERNAL_ERROR)) {
//            // Server is down
//        }
//
//        if (code.equals(ResponseCode.SUCCESS)) {
//            
//        }
//    }
//
//}
