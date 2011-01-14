package database;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import backend.Settings;

public class DatabaseManager extends JFrame {
	
	private boolean changed = false;
	
	private JTextField address;
	private JTextField port;
	private JTextField username;
	private JPasswordField password;
	private JTextField database;
	private JPanel root;
	private JPanel viewPanel;
	
	public DatabaseManager() {
		super("FALCON Suite - Database Manager");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(changed) {
					Settings.saveSettings();
					System.exit(0);
				}
				setVisible(false);
				dispose();
			}
		});
		root = new JPanel();
		getContentPane().add(root);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		JPanel connectionPanel = new JPanel(new SpringLayout());
		JLabel label = new JLabel("Database Address:", JLabel.TRAILING);
		connectionPanel.add(label);
		address = new JTextField(backend.Settings.getProperty("DB_ADD"), 20);
		label.setLabelFor(address);
		connectionPanel.add(address);
		label = new JLabel("Database Port:", JLabel.TRAILING);
		connectionPanel.add(label);
		port = new JTextField(backend.Settings.getProperty("DB_PORT"), 6);
		label.setLabelFor(port);
		connectionPanel.add(port);
		label = new JLabel("Database Username:", JLabel.TRAILING);
		connectionPanel.add(label);
		username = new JTextField(backend.Settings.getProperty("DB_USER"), 10);
		label.setLabelFor(username);
		connectionPanel.add(username);
		label = new JLabel("Database Password:", JLabel.TRAILING);
		connectionPanel.add(label);
		password = new JPasswordField(backend.Settings.getProperty("DB_PASS"), 10);
		label.setLabelFor(password);
		connectionPanel.add(password);
		label = new JLabel("Database Name:", JLabel.TRAILING);
		connectionPanel.add(label);
		database = new JTextField(backend.Settings.getProperty("DB_NAME"), 10);
		label.setLabelFor(database);
		connectionPanel.add(database);
		label = new JLabel("Changes will restart program!");
		connectionPanel.add(label);
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String addr = address.getText();
				String prt = port.getText();
				String user = username.getText();
				String pass = new String(password.getPassword());
				String name = database.getText();
				backend.Settings.setProperty("DB_ADD", addr);
				backend.Settings.setProperty("DB_PORT", prt);
				backend.Settings.setProperty("DB_USER", user);
				backend.Settings.setProperty("DB_PASS", pass);
				backend.Settings.setProperty("DB_NAME", name);
				changed = true;
				Settings.db.connect(addr, Integer.parseInt(prt), user, pass, name);
				displayDatabase();
			}
		});
		connectionPanel.add(apply);
		utils.SpringUtilities.makeCompactGrid(connectionPanel,
										6, 2,
										6, 6,
										6, 6);	
		root.add(connectionPanel);
		
		viewPanel = new JPanel();
		root.add(viewPanel);
		
		displayDatabase();
		
		pack();
		setVisible(true);
	}
	
	public static boolean testDatabase() {
		//FIXME bad connections verify correctly
		ResultSet rs = Settings.db.query("show tables");
		try {
			while(rs.next()) {
				if(rs.getString(1).equals("missions")) return true;
			}
		} catch (SQLException e) {
			System.err.println("Error validating database!");
			if(Settings.DEBUG) e.printStackTrace();
		}
		return false;
	}
	
	private void formatDatabase() {
		Settings.db.execute(
			"CREATE TABLE missions(" +
			"P_ID int NOT NULL AUTO_INCREMENT," +
			"Name varchar(255) not null," +
			"Mission varchar(255) not null," +
			"MMGR varchar(255)," +
			"FD varchar(255)," +
			"ED varchar(255)," +
			"RD varchar(255)," +
			"LaunchTime datetime," +
			"Lift int NOT NULL," +
			"Mass int NOT NULL," +
			"Balloon varchar(255) NOT NULL," +
			"PRIMARY KEY (P_ID))");
		displayDatabase();
	}
	
	private void displayDatabase() {
		root.remove(viewPanel);
		viewPanel = new JPanel(new FlowLayout());
		if(!Settings.db.isValid()) {
			viewPanel.add(new JLabel("Could not connect to the database or the database in invalid. Please try again."));
			return;
		}
		if(testDatabase()) {
			if(Settings.DEBUG) System.out.println("Database validated.");
			viewPanel.add(new JLabel("Database validated"));
		} else {
			if(Settings.DEBUG) System.err.println("Database is invalid!");
			viewPanel.add(new JLabel("The selected database is not correctly formatted for FALCON."));
			JButton format = new JButton("Format Database");
			format.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					formatDatabase();
				}
			});
			viewPanel.add(format);
		}
		root.add(viewPanel);
		pack();
	}

}
