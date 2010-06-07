package falcon.backend;

import com.google.code.jdde.client.ClientConversation;
import com.google.code.jdde.client.DdeClient;
import com.google.code.jdde.client.event.ClientDisconnectListener;
import com.google.code.jdde.event.DisconnectEvent.ClientDisconnectEvent;
import com.google.code.jdde.misc.DdeException;

/**
 * A class to control a az/el rotor through HRDRotator
 * Uses the HRDRotate DDE Server
 * 
 * @author Ethan Harstad
 */
public class RotorController {
	
	// Connection definition
	private static String HRDRotatorDDEServer = "HRDRotator";
	private static String HRDRotatorDDETopic  = "Position";
	private static String HRDRotatorDDEItem   = "PositionData";
	private static int	  CommandDelay		  = 1000;
	
	static DdeClient client;
	static ClientConversation conv;
	
	/**
	 * Send a positioning command to the rotor controller.  There is a 1 second pause between the
	 * azimuth and elevation commands.  If moving the rotor takes longer than this pause, the command will be
	 * interrupted.  Call this function often to ensure the rotor is near the intended direction.
	 * @param az
	 * @param el
	 * @return
	 */
	public static boolean setAzEl(double az, double el) {
		boolean a = setAz(az);
		try{
			Thread.sleep(CommandDelay);
		} catch(InterruptedException e) {
			// do nothing
		}
		boolean b = setEl(el);
		return(a || b);
	}
	
	public static boolean setAz(double az) {
		return ddeCommand("SET-AZ:" + az);
	}
	
	public static boolean setEl(double el) {
		return ddeCommand("SET-EL:" + el);
	}
	
	private static boolean ddeCommand(String cmd) {
		try {
			System.loadLibrary("jDDE");
		} catch(UnsatisfiedLinkError e) {
			System.err.println("jDDE.dll was not found!");
			return false;
		}
		
		client = new DdeClient();
		conv = null;
		try {
			conv = client.connect(HRDRotatorDDEServer, HRDRotatorDDETopic);
			System.out.println("DDE Conversation: " + conv);
			
			conv.setDisconnectListener(new ClientDisconnectListener() {
				public void onDisconnect(ClientDisconnectEvent e) {
					System.err.println("DDE Disconnect!");
				}
			});
			System.out.println("DDE Execute: " + cmd);
			try {
				conv.pokeAsync(HRDRotatorDDEItem, cmd.getBytes(), null);
				System.out.println("DDE Send: " + cmd);
				conv.disconnect();
				client.uninitialize();
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		} catch(DdeException e) {
			System.err.println("DDE Exception!");
			client.uninitialize();
			return false;
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
