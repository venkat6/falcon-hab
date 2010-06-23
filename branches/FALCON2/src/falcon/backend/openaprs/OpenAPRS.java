package falcon.backend.openaprs;

/**
 * A class to connect to the OpenAPRS DCC API.
 * An account on http://www.openaprs.net is required to use the API.
 * Your callsign must be validated to use the transmission functions of the API.
 * 
 * @author Ethan Harstad
 *
 */
public class OpenAPRS {
	
	private boolean connected; 
	private String user;
	private String pass;
	private String call;
	
	public OpenAPRS(String username, String password, String callsign) {
		user = username;
		pass = password;
		call = callsign;
	}
	
	public boolean connect() {
		//TODO
		return false;
	}

}
