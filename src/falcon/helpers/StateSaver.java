package falcon.helpers;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFrame;

/**
 * This class allows the state of individual windows and
 * various settings that may be changed curing the user session
 * to be persistent.  This prevents the user from having to change
 * everything as needed every time the application is launched.
 * @author Ethan Harstad
 */
public final class StateSaver {

	/**
	 * Save the state of the given window to the given output stream (usually a file)
	 * @param frame
	 * @param stream
	 */
	public static void saveWindowState(JFrame frame, OutputStream stream) {
		PrintStream out = new PrintStream(stream);
		out.println(frame.getTitle());
		out.println(frame.isVisible());
		Rectangle bounds = frame.getBounds();
		out.println(bounds.x + " " + bounds.y + " " + bounds.width + " " + bounds.height);
	}
	
	/**
	 * Recall the state of the given window from the given settings file.
	 * Returns a boolean for the visibility of the window.  If settings
	 * for the specified window are not found in the given file, nothing happens
	 * and false is returned.
	 * @param frame
	 * @param in
	 * @return
	 */
	public static boolean recallWindowState(JFrame frame, File in) {
		Scanner file;
		try{
			file = new Scanner(in);
		} catch(FileNotFoundException e) {
			return false;
		}
		String title = frame.getTitle();
		while(file.hasNextLine()) {
			String line = file.nextLine();
			if(title.equals(line)) {
				String v = file.next();
				boolean visible = false;
				if(v.equals("true")) visible = true;
				Rectangle bounds = new Rectangle();
				try {
					bounds = new Rectangle(file.nextInt(), file.nextInt(), file.nextInt(), file.nextInt());
				} catch(Exception e) {
					return false;
				}
				frame.setBounds(bounds);
				frame.setVisible(visible);
				
				return visible;
			}
		}
		return false;
	}
	
}
