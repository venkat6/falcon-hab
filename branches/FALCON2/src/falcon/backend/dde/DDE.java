package falcon.backend.dde;

import com.google.code.jdde.client.ClientConversation;
import com.google.code.jdde.client.DdeClient;
import com.google.code.jdde.client.event.ClientDisconnectListener;
import com.google.code.jdde.event.DisconnectEvent.ClientDisconnectEvent;
import com.google.code.jdde.misc.DdeException;

/**
 * A class to handle generic DDE client conversations.
 * Utilizes the jDDE library.
 * 
 * @author Ethan Harstad
 *
 */
public class DDE {
	
	private boolean connected;
	private ClientConversation conv;
	
	public DDE(String serverName, String topicName) {
		try {
			System.loadLibrary("jDDE");
			conv = new DdeClient().connect(serverName, topicName);
			conv.setDisconnectListener(new ClientDisconnectListener() {
				public void onDisconnect(ClientDisconnectEvent e) {
					connected = false;
					System.err.println("DDE Disconnect!");
				}
			});
			connected = true;
		} catch(UnsatisfiedLinkError e) {
			System.err.println("Could not load the library!");
			connected = false;
			return;
		} catch(DdeException e) {
			System.err.println("DDE Exception!");
			System.err.println(e.getStackTrace());
			connected = false;
			conv.disconnect();
		}
	}
	
	public boolean sendCommand(String cmd) {
		if(!connected) return false;
		//TODO
		return true;
	}

}
