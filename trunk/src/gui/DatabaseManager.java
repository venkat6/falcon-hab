package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import backend.Settings;

/**
 * Window used to manage all database functions
 * 
 * @author Ethan Harstad
 */
public class DatabaseManager extends JFrame {
	
	private boolean changed = false; // Tracks if the program needs to be restarted
	
	private JTextField address;
	private JTextField port;
	private JTextField username;
	private JPasswordField password;
	private JTextField database;
	private JPanel root;
	private JPanel viewPanel;
	
	public DatabaseManager() {
		super("FALCON Suite - Database Manager");
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            		Rectangle bounds = getBounds();
            		Settings.setProperty("DBMGR_X", Integer.toString(bounds.x));
            		Settings.setProperty("DBMGR_Y", Integer.toString(bounds.y));
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
		utils.SpringUtilities.makeCompactGrid(connectionPanel, 6, 2, 6, 6, 6, 6);	
		root.add(connectionPanel);
		
		viewPanel = new JPanel();
		root.add(viewPanel);
		
		displayDatabase();
		
		pack();
		int x = 0;
		int y = 0;
		try {
			String sx = Settings.getProperty("DBMGR_X");
			String sy = Settings.getProperty("DBMGR_Y");
			x = sx == null ? 0 : Integer.parseInt(sx);
			y = sy == null ? 0 : Integer.parseInt(sy);
		} catch(NumberFormatException e) {}
		setLocation(x, y);
		setVisible(true);
	}
	
	/**
	 * Test if the database is setup correctly for use by FALCON
	 * @return True if the database is ready for use
	 */
	public static boolean testDatabase() {
		//FIXME bad connections verify correctly
		//FIXME add validity tag to database connection
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
	
	/**
	 * Format the database for use by FALCON
	 */
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
			"Lift double NOT NULL," +
			"Mass double NOT NULL," +
			"Balloon varchar(255) NOT NULL," +
			"Status ENUM('Ready', 'Pending', 'Active', 'Completed', 'Missing')," +
			"PRIMARY KEY (P_ID))");
		displayDatabase();
	}
	
	/**
	 * Display the database in the GUI
	 */
	private void displayDatabase() {
		//TODO improve table display: auto col width, etc
		root.remove(viewPanel);
		viewPanel = new JPanel();
		viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
		if(!Settings.db.isValid()) {
			// Could not connect to the database
			viewPanel.add(new JLabel("Could not connect to the database or the database in invalid. Please try again."));
			return;
		}
		if(testDatabase()) {
			// Valid database, display the database
			if(Settings.DEBUG) System.out.println("Database validated.");
			// Populate table with data from database
			ResultSet rs = Settings.db.query("SELECT * FROM missions ORDER BY P_ID");
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			try {
				while(rs.next()) {
					int fields = 12;
					Vector<String> line = new Vector<String>(fields);
					for(int i = 0; i < fields; i++) {
						line.add(rs.getString(i+1));
					}
					data.add(line);
				}
			} catch (SQLException e1) {
				System.err.println("SQL Exception while fetching missions!");
				if(Settings.DEBUG) e1.printStackTrace();
			}
			// Create GUI
			Vector<String> names = new Vector<String>();
			names.add("ID");
			names.add("Table");
			names.add("Flight");
			names.add("MMGR");
			names.add("FD");
			names.add("ED");
			names.add("RD");
			names.add("Launch Time");
			names.add("Lift");
			names.add("Mass");
			names.add("Balloon");
			names.add("Status");
			final JTable table = new JTable(data, names);
			table.setFillsViewportHeight(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.doLayout();
			JScrollPane scroller = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scroller.setPreferredSize(new Dimension(600,500));
			viewPanel.add(scroller);
			JPanel buttons = new JPanel();
			viewPanel.add(buttons);
			JButton add = new JButton("Add");
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final JFrame frame = new JFrame("Add Mission");
					JPanel root = new JPanel();
					root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
					frame.getContentPane().add(root);
					JPanel panel = new JPanel(new SpringLayout());
					JLabel lbl = new JLabel("Mission Name:");
					final JTextField mission = new JTextField(10);
					lbl.setLabelFor(mission);
					panel.add(lbl);
					panel.add(mission);
					lbl = new JLabel("MMGR:");
					final JTextField mmgr = new JTextField(20);
					lbl.setLabelFor(mmgr);
					panel.add(lbl);
					panel.add(mmgr);
					lbl = new JLabel("Launch Time:");
					final JTextField time = new JTextField("YYYY-MM-DD HH:MM:SS");
					lbl.setLabelFor(time);
					panel.add(lbl);
					panel.add(time);
					lbl = new JLabel("FD:");
					final JTextField fd = new JTextField(20);
					lbl.setLabelFor(fd);
					panel.add(lbl);
					panel.add(fd);
					lbl = new JLabel("Balloon:");
					final JTextField balloon = new JTextField();
					lbl.setLabelFor(balloon);
					panel.add(lbl);
					panel.add(balloon);
					lbl = new JLabel("ED:");
					final JTextField ed = new JTextField(20);
					lbl.setLabelFor(ed);
					panel.add(lbl);
					panel.add(ed);
					lbl = new JLabel("Lift:");
					final JTextField lift = new JTextField();
					lbl.setLabelFor(lift);
					panel.add(lbl);
					panel.add(lift);
					lbl = new JLabel("RD:");
					final JTextField rd = new JTextField();
					lbl.setLabelFor(rd);
					panel.add(lbl);
					panel.add(rd);
					lbl = new JLabel("Mass:");
					final JTextField mass = new JTextField();
					lbl.setLabelFor(mass);
					panel.add(lbl);
					panel.add(mass);
					lbl = new JLabel("Status:");
					String[] statuses = {"Ready", "Pending", "Active", "Completed", "Missing"};
					final JComboBox status = new JComboBox(statuses);
					lbl.setLabelFor(status);
					panel.add(lbl);
					panel.add(status);
					utils.SpringUtilities.makeCompactGrid(panel, 5, 4, 5, 5, 5, 5);
					root.add(panel);
					JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
					JButton add = new JButton("Add");
					add.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addMission(mission.getText(), mmgr.getText(),
									fd.getText(), ed.getText(), rd.getText(),
									time.getText(), Double.parseDouble(lift.getText()), Double.parseDouble(mass.getText()),
									balloon.getText(), status.getSelectedItem().toString());
							displayDatabase();
							frame.dispose();
							return;
						}
					});					
					buttons.add(add);
					JButton cancel = new JButton("Cancel");
					cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							frame.dispose();
							return;
						}
					});
					buttons.add(cancel);
					root.add(buttons);
					frame.pack();
					frame.setVisible(true);
				}
			});
			buttons.add(add);
			JButton edit = new JButton("Edit");
			edit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//TODO database edit button
					JOptionPane.showMessageDialog(viewPanel, "Not implemented yet!");
				}
			});
			buttons.add(edit);
			JButton remove = new JButton("Remove");
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int sel = table.getSelectedRow();
					if(sel < 0) {
						JOptionPane.showMessageDialog(viewPanel, "Please select a mission to remove!");
						return;
					}
					String m = table.getValueAt(sel,2).toString();
					if(JOptionPane.showConfirmDialog(viewPanel, "Are you sure you want to remove " + m + "?",
							"Are you sure?", JOptionPane.YES_NO_OPTION) != 0) return;
					String cmd = "DELETE FROM missions WHERE P_ID=" + table.getValueAt(sel,0);
					if(Settings.DEBUG) System.out.println("CMD: " + cmd);
					Settings.db.execute(cmd);
					displayDatabase();
				}
			});
			buttons.add(remove);
		} else {
			// Invalid database, offer to format the database
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
	
	/**
	 * Add a mission to the database
	 * @param mission
	 * @param mmgr
	 * @param fd
	 * @param ed
	 * @param rd
	 * @param time
	 * @param lift
	 * @param mass
	 * @param balloon
	 * @param status
	 */
	private void addMission(String mission, String mmgr, String fd, String ed, String rd, String time,
			double lift, double mass, String balloon, String status) {
		//TODO add new table for flight
		String cmd = "INSERT INTO missions (Name,Mission,MMGR,FD,ED,RD,LaunchTime,Lift,Mass,Balloon,Status) VALUES (";
		String name = mission.replace(' ', '_');
		name = name.replace('-', '_');
		cmd += "'" + name + "',";
		cmd += "'" + mission + "',";
		cmd += "'" + mmgr + "',";
		cmd += "'" + fd + "',";
		cmd += "'" + ed + "',";
		cmd += "'" + rd + "',";
		cmd += "'" + time + "',";
		cmd += lift + ",";
		cmd += mass + ",";
		cmd += "'" + balloon + "',";
		cmd += "'" + status + "')";
		if(Settings.DEBUG) System.out.println("CMD: " + cmd);
		Settings.db.execute(cmd);
	}

}
