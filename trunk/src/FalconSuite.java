import gui.AlertWindow;
import gui.DatabaseManager;
import gui.LogWindow;
import gui.PredictionManager;
import gui.TrackingManager;
import gui.elements.StatusBar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.infonode.docking.RootWindow;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import backend.Settings;
import connectors.database.MySQL;

public class FalconSuite extends JFrame {
	
	private static RootWindow rootWindow;
	private static LogWindow logWindow;
	private static AlertWindow alertWindow;
	
	public FalconSuite(String[] args) {
		super("FALCON Suite");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				backend.Settings.saveSettings();
				System.exit(0);
			}
		});

		// Handle command line arguments
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("-debug")) {
				Settings.DEBUG = true;
				System.out.println("DEBUG Mode Enabled.");
			}
			if(arg.equals("-local")) {
				Settings.LOCAL = true;
				System.out.println("LOCAL Mode Enabled.");
			}
		}
		
		rootWindow = DockingUtil.createRootWindow(new ViewMap(), true);
		setContentPane(rootWindow);
		StatusBar statusBar = new StatusBar();
		rootWindow.add(statusBar, BorderLayout.SOUTH);
		logWindow = new LogWindow();
		DockingUtil.addWindow(logWindow, rootWindow);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
		rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu launchMenu = new JMenu("Launch");
		JMenuItem prediction = new JMenuItem("Flight Prediction");
		JMenuItem tracking = new JMenuItem("Flight Tracking");
		JMenuItem archive = new JMenuItem("Flight Archives");
		JMenuItem database = new JMenuItem("Database Manager");
		launchMenu.add(prediction);
		launchMenu.add(tracking);
		launchMenu.add(archive);
		launchMenu.add(database);
		menuBar.add(launchMenu);
		setJMenuBar(menuBar);
		prediction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DockingUtil.addWindow(new PredictionManager(), rootWindow);
			}
		});
		tracking.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DockingUtil.addWindow(new TrackingManager(), rootWindow);
				if(alertWindow == null) {
					alertWindow = new AlertWindow();
					DockingUtil.addWindow(alertWindow, rootWindow);
				}
			}
		});
		archive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO Flight archives
				JOptionPane.showMessageDialog(null, "Not implemented yet!");
			}
		});
		database.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DatabaseManager();
			}
		});

		
		pack();
		setVisible(true);
		setExtendedState(getExtendedState() | FalconSuite.MAXIMIZED_BOTH);
	}
	
	public static void main(String[] args) {
		new FalconSuite(args);
		
		init();
	}

	public static void init() {
		Settings.loadSettings();	// Load settings
		boolean connected = false;
		if(!Settings.LOCAL) {
			Settings.db = new MySQL(
				Settings.getProperty("DB_ADD"),
				Integer.parseInt(Settings.getProperty("DB_PORT")),
				Settings.getProperty("DB_USER"),
				Settings.getProperty("DB_PASS"),
				Settings.getProperty("DB_NAME"));
				connected = Settings.db.connect();
		} else {
			//TODO local database init
			Settings.db = new MySQL();
		}
		if(connected) {
			if(Settings.DEBUG) System.out.println("Connected to database.");
		} else {
			System.err.println("Could not connect to database!");
		}
	}

}
