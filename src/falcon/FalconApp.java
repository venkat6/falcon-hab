package falcon;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import falcon.components.gui.*;

public class FalconApp extends JFrame{
	
	public final static String VERSION = "Pre-Alpha Branch";
	
	static JFrame window;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		createWindow();
		
		while(true) {
			//window.repaint();
		}
		
	}
	
	private static void createWindow() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		window = new JFrame("FALCON " + VERSION + " - Iowa State University SSCL");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMaximizedBounds(env.getMaximumWindowBounds());
		window.setExtendedState(window.getExtendedState() | Frame.MAXIMIZED_BOTH);
		
		//TODO Add components
		LogPanel console = new LogPanel();
		window.add(console);
		console.addMessage("Testing the log panel!");
		console.addEvent("A random event has happened!");
		console.addWarning("Something bad is about to happen!");
		console.addAlert("A noes! Something bad has happened!");
		
		window.setVisible(true);
	}

}
