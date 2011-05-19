package fractus.strategy;

import fractus.main.FractusMessage;
import fractus.net.FractusConnector;
import fractus.net.ServerConnector;
import fractus.net.ProtocolBuffer.RegisterKeyReq;

public class RegisterKeyReqStrategy {
	RegisterKeyReq registerKeyRequest;
	
	public RegisterKeyReqStrategy(String username, String password) {
		this.registerKeyRequest = RegisterKeyReq.newBuilder()
		.setUsername(username)
		.setPassword(password)
		.build();
	}
	public void send(FractusConnector fractusConnector) {
		FractusMessage registerKeyRequestMessage =
			FractusMessage.build(registerKeyRequest);
		// Modify state in order to accept reply
		fractusConnector.sendMessage(registerKeyRequestMessage);
	}
}
