import backend.*;
import gui.DatabaseManager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import connectors.MySQL;

public class Launcher {
	
	private static JFrame frame;
	
	public static void main(String[] args) {
		frame = new JFrame("FALCON Suite - Launcher");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				backend.Settings.saveSettings();
				System.exit(0);
			}
		});
		
		init();
		
		frame.getContentPane().setLayout(new FlowLayout());
		
		JButton prediction = new JButton("Flight Prediction");
		prediction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		frame.getContentPane().add(prediction);
		
		JButton tracking = new JButton("Flight Tracking");
		tracking.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		frame.getContentPane().add(tracking);
		
		JButton archive = new JButton("Flight Archives");
		archive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		frame.getContentPane().add(archive);
		
		JButton database = new JButton("Database Manager");
		database.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DatabaseManager();
			}
		});
		frame.getContentPane().add(database);

		frame.pack();
		frame.setVisible(true);
	}
	
	public static void init() {
		Settings.loadSettings();	// Load settings
		Settings.db = new MySQL(
				Settings.getProperty("DB_ADD"),
				Integer.parseInt(Settings.getProperty("DB_PORT")),
				Settings.getProperty("DB_USER"),
				Settings.getProperty("DB_PASS"),
				Settings.getProperty("DB_NAME"));
		boolean connected = Settings.db.connect();
		if(connected) {
			System.out.println("Connected to database.");
		} else {
			System.err.println("Could not connect to database!");
		}
	}

}
