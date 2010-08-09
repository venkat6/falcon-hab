package falcon;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Launcher {
	
	public static final String VERSION = "PRE-ALPHA";

	private static JFrame window;
	
	private static FalconApp falcon = null;
	private static PredictionApp prediction =  null;
	private static ArchiveApp archive = null;
	private static DatabaseManager manager = null;
	
	private static void createWindow() {
		window = new JFrame("FALCON Launcher");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton predictionButton = new JButton("Flight Prediction");
		predictionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(prediction == null) {
					prediction = new PredictionApp();
				}
			}
		});
		
		JButton trackingButton = new JButton("Flight Tracking");
		trackingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(falcon == null) {
					falcon = new FalconApp();
				}
			}
		});
		
		JButton archiveButton = new JButton("Archived Flights");
		archiveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(archive == null) {
					archive = new ArchiveApp();
				}
			}
		});
		
		JButton databaseButton = new JButton("Database Manager");
		databaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(manager == null) {
					manager = new DatabaseManager();
				}
			}
		});
		
		window.setLayout(new BoxLayout(window.getContentPane(),BoxLayout.PAGE_AXIS));
		window.getContentPane().add(predictionButton);
		window.getContentPane().add(trackingButton);
		window.getContentPane().add(archiveButton);
		window.getContentPane().add(databaseButton);
		window.pack();
		window.setVisible(true);
	}
	
	public static void closing(JFrame src) {
		if(src.equals(falcon)) {
			falcon = null;
		}
	}
	
	public static void main(String args[]) {
		createWindow();
	}
	
}
