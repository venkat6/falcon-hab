package falcon;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import falcon.components.windows.TrackingFrame;

public class FalconApp extends JFrame {
	
	public final static String VERSION = "Pre-Alpha Branch";
	
	static JFrame mainWindow;
	static TrackingFrame trackingWindow;
	static JMenuBar cMenuBar;
	static JCheckBoxMenuItem cWindowTracking;

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
		mainWindow = new JFrame("FALCON " + VERSION + " - Iowa State University SSCL");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setMinimumSize(new Dimension(600,400));
		mainWindow.setPreferredSize(new Dimension(600,400));
		
		//TODO Add components
		mainWindow.setJMenuBar(createMenuBar());
		trackingWindow = new TrackingFrame();
		
		mainWindow.setVisible(true);
	}
	
	private static JMenuBar createMenuBar() {
		cMenuBar = new JMenuBar();
		
		JMenu cFileMenu = new JMenu("File");
		cMenuBar.add(cFileMenu);
		JMenuItem cFileExit = new JMenuItem("Exit");
		cFileMenu.add(cFileExit);
		
		JMenu cWindowMenu = new JMenu("Window");
		cMenuBar.add(cWindowMenu);
		JCheckBoxMenuItem cWindowRadio = new JCheckBoxMenuItem("Radio");
		cWindowMenu.add(cWindowRadio);
		cWindowTracking = new JCheckBoxMenuItem("Tracking");
		cWindowTracking.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					trackingWindow.setVisible(true);
				} else {
					trackingWindow.setVisible(false);
				}
			}
		});
		cWindowMenu.add(cWindowTracking);
		JCheckBoxMenuItem cWindowPrediction = new JCheckBoxMenuItem("Prediction");
		cWindowMenu.add(cWindowPrediction);
		JCheckBoxMenuItem cWindowMap = new JCheckBoxMenuItem("Map");
		cWindowMenu.add(cWindowMap);
		JCheckBoxMenuItem cWindowTelemetry = new JCheckBoxMenuItem("Telemetry");
		cWindowMenu.add(cWindowTelemetry);
		JCheckBoxMenuItem cWindowStatus = new JCheckBoxMenuItem("Status");
		cWindowMenu.add(cWindowStatus);
		
		JMenu cHelpMenu = new JMenu("Help");
		cMenuBar.add(cHelpMenu);
		JMenuItem cHelpHelp = new JMenuItem("Help");
		cHelpMenu.add(cHelpHelp);
		JMenuItem cHelpAbout = new JMenuItem("About");
		cHelpMenu.add(cHelpAbout);
		
		return cMenuBar;
	}
	
	public static void windowClosing(String name) {
		if(name.equals("Tracking")) {
			cWindowTracking.setState(false);
		}
	}

}
