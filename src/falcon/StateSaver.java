package falcon;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFrame;

public final class StateSaver {

	public static void saveWindowState(JFrame frame, OutputStream stream) {
		PrintStream out = new PrintStream(stream);
		out.println(frame.getTitle());
		out.println(frame.isVisible());
		Rectangle bounds = frame.getBounds();
		out.println(bounds.x + " " + bounds.y + " " + bounds.width + " " + bounds.height);
	}
	
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
