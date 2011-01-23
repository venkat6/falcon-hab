import gui.DatabaseManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import backend.Settings;
import connectors.database.MySQL;

public class FalconSuite extends JFrame {
	
	private static JDesktopPane desktop;
	
	public FalconSuite() {
		super("FALCON Suite");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				backend.Settings.saveSettings();
				System.exit(0);
			}
		});
		
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
				//TODO Flight prediction
				JOptionPane.showMessageDialog(null, "Not implemented yet!");
			}
		});
		tracking.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO Flight tracking
				JOptionPane.showMessageDialog(null, "Not implemented yet!");
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
				desktop.add(new DatabaseManager());
			}
		});
		
		desktop = new JDesktopPane();
		setContentPane(desktop);
		
		pack();
		setVisible(true);
		setExtendedState(getExtendedState() | FalconSuite.MAXIMIZED_BOTH);
	}
	
	public static void main(String[] args) {
		new FalconSuite();
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
