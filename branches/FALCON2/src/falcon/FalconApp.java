package falcon;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	static TrackingFrame trackingWindow;
	static JCheckBoxMenuItem cWindowTracking;
	static PredictionFrame predictionWindow;
	static JCheckBoxMenuItem cWindowPrediction;
	static StatusFrame statusWindow;
	static JCheckBoxMenuItem cWindowStatus;
	static LogPanel terminal;

	public static void main(String[] args) {
		
		createWindow();
		
		while(true) {
			//window.repaint();
		}
		
	}
	
	private static void createWindow() {
		mainWindow = new JFrame("FALCON " + VERSION + " - Iowa State University SSCL");
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitDialog();
			}
		});
		
		mainWindow.setJMenuBar(createMenuBar());
		trackingWindow = new TrackingFrame();
		predictionWindow = new PredictionFrame();
		statusWindow = new StatusFrame();
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
		cWindowPrediction = new JCheckBoxMenuItem("Prediction");
		cWindowPrediction.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					predictionWindow.setVisible(true);
				} else {
					predictionWindow.setVisible(false);
				}
			}
		});
		cWindowMenu.add(cWindowPrediction);
		JCheckBoxMenuItem cWindowMap = new JCheckBoxMenuItem("Map");
		cWindowMenu.add(cWindowMap);
		JCheckBoxMenuItem cWindowTelemetry = new JCheckBoxMenuItem("Telemetry");
		cWindowMenu.add(cWindowTelemetry);
		cWindowStatus = new JCheckBoxMenuItem("Status");
		cWindowStatus.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					statusWindow.setVisible(true);
				} else {
					statusWindow.setVisible(false);
				}
			}
		});
		cWindowMenu.add(cWindowStatus);
		
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
		}
	}
	
	public static void exitDialog() {
		if(JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			//TODO clean up, write logs etc
			System.exit(0);
		}
	}

}
