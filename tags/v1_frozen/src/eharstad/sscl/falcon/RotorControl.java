package eharstad.sscl.falcon;

import com.google.code.jdde.client.ClientConversation;
import com.google.code.jdde.client.DdeClient;
import com.google.code.jdde.client.event.ClientDisconnectListener;
import com.google.code.jdde.event.DisconnectEvent.ClientDisconnectEvent;
import com.google.code.jdde.misc.DdeException;

/**
 * A class to control an azimuth/elevation rotator
 * Uses the HRD Rotator DDE Server for control
 * Connects via the jDDE library available at
 * http://code.google.com/p/jdde/
 * 
 * @author Ethan Harstad
 */
public class RotorControl {
	
	private static boolean DEBUG = false;
	
	// Connection definition
	private static String HRDRotatorDDEServer = "HRDRotator";
	private static String HRDRotatorDDETopic  = "Position";
	private static String HRDRotatorDDEItem   = "PositionData";
	// How much to pause between commands
	private static int	  CommandDelay		  = 1000;
	
	static DdeClient client;
	static ClientConversation conv;
	
	/**
	 * Send a positioning command to the rotor controller.  There is a 1 second pause between the
	 * azimuth and elevation commands.  If moving the rotor takes longer than this pause, the command will be
	 * interrupted.  Call this function often to ensure the rotor is near the intended direction.
	 * @param az
	 * @param el
	 * @return True if successful
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
	
	/**
	 * Set the rotor to the desired azimuth
	 * @param az
	 * @return True if successful
	 */
	public static boolean setAz(double az) {
		return ddeCommand("SET-AZ:" + az);
	}
	
	/**
	 * Set the rotor to the desired elevation
	 * @param el
	 * @return True if successful
	 */
	public static boolean setEl(double el) {
		return ddeCommand("SET-EL:" + el);
	}

	/**
	 * Send the given command to the DDE server defined by the static variables above
	 * @param cmd
	 * @return True if successful
	 */
	private static boolean ddeCommand(String cmd) {
		// Load the jDDE library
		try {
			System.loadLibrary("jDDE");
		} catch(UnsatisfiedLinkError e) {
			System.err.println("jDDE.dll was not found!");
			return false;
		}
		
		// Create the client connection
		client = new DdeClient();
		conv = null;
		try {
			conv = client.connect(HRDRotatorDDEServer, HRDRotatorDDETopic);
			if(DEBUG) System.out.println("DDE Conversation: " + conv);
			
			conv.setDisconnectListener(new ClientDisconnectListener() {
				public void onDisconnect(ClientDisconnectEvent e) {
					System.err.println("DDE Disconnect!");
				}
			});
			
			try {
				conv.pokeAsync(HRDRotatorDDEItem, cmd.getBytes(), null);
				if(DEBUG) System.out.println("DDE Send: " + cmd);
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