package falcon.backend.openaprs;
//TODO OpenAPRS DCC API

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

/**
 * A class to connect to the OpenAPRS DCC API.
 * An account on http://www.openaprs.net is required to use the API.
 * Your callsign must be validated to use the transmission functions of the API.
 * 
 * @author Ethan Harstad
 *
 */
public class OpenAPRS implements Runnable {
	
	//Parameter Fields
	private String host = "dcc.openaprs.net";
	private int port = 2620;
	private String client = "Falcon-" + falcon.Launcher.VERSION;
	private boolean debug = false;
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean connected = false; 
	private String user;
	private String pass;
	private Stack<byte[]> stack;
	private int messages = 0;
	
	public OpenAPRS(String username, String password) {
		user = username;
		pass = password;
		connect();
	}
	
	public boolean connect() {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch(Exception e) {
			
		}
		// CONNECT TO THE SYSTEM
		String loginCmd = ".LN " + user + " " + pass + " \"" + client;
		try {
			boolean temp = false;
			for(int x = 1; x <= 5; x++) {
				if(in.readLine().equals("002 MS:Please login by typing ``.LN <email> <password> [client]''")) {
					sendData(loginCmd);
					temp = true;
				}
			}
			if(!temp) return false;
			System.out.println(in.readLine());
		} catch (IOException e) {
		}
		connected = true;
		new Thread(this).start();
		return true;
	}
	
	public boolean dataAvailable() {
		if(messages > 0) return true;
		return false;
	}
	
	private boolean sendData(String data) {
		if(!connected) return false;
		try {
			out.println(data);
		} catch(Exception e) {
			System.err.println("Problem sending the data to OpenAPRS:");
			System.err.println(data);
			e.printStackTrace();
		}
		return true;
	}
	
	public String getData() {
		if(messages < 1) return null;
		messages--;
		return stack.pop().toString();
	}

	@Override
	public void run() {
		while(connected) {
			try {
				Thread.sleep(2000);
				String packet = in.readLine();
				if(debug) System.out.println(packet);
				if(packet.equals("106 MS:Ping?")) {
					sendData(".PN");
					if(!in.readLine().equals("500 MS:OK!")) connected = false;
				} else {
					stack.push(packet.getBytes());
					messages++;
				}
			} catch (InterruptedException e) {
			} catch (IOException e) {
			}
		}
	}

}
