package falcon.backend;

//TODO Switch to asynchronous and maybe oAuth?

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Connects to Twitter and allows you to update the user account's
 * status.
 * @author Ethan Harstad
 *
 */
public class TwitterClient {

	private Twitter t;
	
	/**
	 * Default constructor
	 */
	public TwitterClient() {
		t = null;
	}
	
	/**
	 * Create a link to the given user information
	 * @param username
	 * @param password
	 */
	public TwitterClient(String username, String password) {
		t = new TwitterFactory().getInstance(username, password);
	}
	
	/**
	 * Verify the user information is valid
	 * @return
	 */
	public boolean verifyCredentials() {
		try {
			t.verifyCredentials();
		} catch (TwitterException e) {
			if(e.getStatusCode() != 200) return false;
		}
		
		return true;
	}
	
	/**
	 * Update the account's status to the given message
	 * @param msg
	 * @return
	 */
	public boolean updateStatus(String msg) {
		if(t == null) return false;
		
		try {
			t.updateStatus(msg);
		} catch (TwitterException e) {
			System.err.println("Could not post update to Twitter! " + e);
			return false;
		}
		
		return true;
	}
	
}
