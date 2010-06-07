package falcon;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import falcon.components.gui.*;
import falcon.components.windows.*;

public class FalconApp extends JFrame {
	
	public final static String VERSION = "Pre-Alpha Branch";
	
	static JFrame mainWindow;
	static JMenuBar cMenuBar;
	static RadioFrame radioWindow;
	static JCheckBoxMenuItem cWindowRadio;
	static TrackingFrame trackingWindow;
	static JCheckBoxMenuItem cWindowTracking;
	static PredictionFrame predictionWindow;
	static JCheckBoxMenuItem cWindowPrediction;
	static StatusFrame statusWindow;
	static JCheckBoxMenuItem cWindowStatus;
	static MapFrame mapWindow;
	static JCheckBoxMenuItem cWindowMap;
	static LogbookFrame logWindow;
	static JCheckBoxMenuItem cWindowLog;
	static LogPanel terminal;

	public static void main(String[] args) {
		
		createWindow();
		
	}
	
	private static void createWindow() {
		// Create main window and restore state information
		mainWindow = new JFrame("FALCON " + VERSION + " - Iowa State University SSCL");
		StateSaver.recallWindowState(mainWindow, new File("state.ini"));
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitDialog();
			}
		});
		mainWindow.setJMenuBar(createMenuBar());
		
		// Build other windows and restore state information
		radioWindow = new RadioFrame();
		cWindowRadio.setState(StateSaver.recallWindowState(radioWindow, new File("state.ini")));
		trackingWindow = new TrackingFrame();
		cWindowTracking.setState(StateSaver.recallWindowState(trackingWindow, new File("state.ini")));
		predictionWindow = new PredictionFrame();
		cWindowPrediction.setState(StateSaver.recallWindowState(predictionWindow, new File("state.ini")));
		statusWindow = new StatusFrame();
		cWindowStatus.setState(StateSaver.recallWindowState(statusWindow, new File("state.ini")));
		mapWindow = new MapFrame();
		cWindowMap.setState(StateSaver.recallWindowState(mapWindow, new File("state.ini")));
		logWindow = new LogbookFrame();
		cWindowLog.setState(StateSaver.recallWindowState(logWindow, new File("state.ini")));
		
		// Populate main window
		JPanel panel = new JPanel();
		mainWindow.getContentPane().add(panel);
		//TODO Add components
		terminal = new LogPanel(new Dimension(600,300));
		panel.add(terminal);
		
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
	
	private static JMenuBar createMenuBar() {
		cMenuBar = new JMenuBar();
		
		JMenu cFileMenu = new JMenu("File");
		cMenuBar.add(cFileMenu);
		JMenuItem cFileExit = new JMenuItem("Exit");
		cFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitDialog();
			}
		});
		cFileMenu.add(cFileExit);
		
		JMenu cWindowMenu = new JMenu("Window");
		cMenuBar.add(cWindowMenu);
		cWindowRadio = new JCheckBoxMenuItem("Radio");
		cWindowRadio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				radioWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowRadio);
		cWindowTracking = new JCheckBoxMenuItem("Tracking");
		cWindowTracking.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				trackingWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowTracking);
		cWindowPrediction = new JCheckBoxMenuItem("Prediction");
		cWindowPrediction.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				predictionWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowPrediction);
		cWindowMap = new JCheckBoxMenuItem("Map");
		cWindowMap.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mapWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowMap);
		JCheckBoxMenuItem cWindowTelemetry = new JCheckBoxMenuItem("Telemetry");
		cWindowMenu.add(cWindowTelemetry);
		cWindowStatus = new JCheckBoxMenuItem("Status");
		cWindowStatus.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				statusWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowStatus);
		cWindowLog = new JCheckBoxMenuItem("Logbook");
		cWindowLog.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				logWindow.setVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		cWindowMenu.add(cWindowLog);
		
		JMenu cHelpMenu = new JMenu("Help");
		cMenuBar.add(cHelpMenu);
		JMenuItem cHelpHelp = new JMenuItem("Help");
		cHelpMenu.add(cHelpHelp);
		JMenuItem cHelpAbout = new JMenuItem("About");
		cHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutFrame();
			}
		});
		cHelpMenu.add(cHelpAbout);
		
		return cMenuBar;
	}
	
	/**
	 * Takes notifications of closing windows and unchecks the appropriate item
	 * @param name
	 */
	public static void windowClosing(String name) {
		if(name.equals("Tracking")) {
			cWindowTracking.setState(false);
		} else if(name.equals("Prediction")) {
			cWindowPrediction.setState(false);
		} else if(name.equals("Status")) {
			cWindowStatus.setState(false);
		} else if(name.equals("Map")) {
			cWindowMap.setState(false);
		} else if(name.equals("Radio")) {
			cWindowRadio.setState(false);
		}
	}
	
	/**
	 * Get exit confirmation from user
	 */
	public static void exitDialog() {
		if(JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			// User confirmed exit
			// Save window state information
			FileOutputStream file = null;
			try {
				file = new FileOutputStream("state.ini");
			} catch (FileNotFoundException e) {
				System.err.println("Could not open file for window state saving");
			}
			StateSaver.saveWindowState(mainWindow, file);
			StateSaver.saveWindowState(radioWindow, file);
			StateSaver.saveWindowState(mapWindow, file);
			StateSaver.saveWindowState(predictionWindow, file);
			StateSaver.saveWindowState(trackingWindow, file);
			StateSaver.saveWindowState(statusWindow, file);
			StateSaver.saveWindowState(logWindow, file);
			
			//TODO clean up, write logs etc
			System.exit(0);
		}
	}

}
