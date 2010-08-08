package falcon;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Launcher {

	private static JFrame window;
	
	private static void createWindow() {
		window = new JFrame("FALCON Launcher");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton predictionButton = new JButton("Flight Prediction");
		predictionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton trackingButton = new JButton("Flight Tracking");
		trackingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		JButton archiveButton = new JButton("Archived Flights");
		archiveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		window.setLayout(new BoxLayout(window.getContentPane(),BoxLayout.PAGE_AXIS));
		window.getContentPane().add(predictionButton);
		window.getContentPane().add(trackingButton);
		window.getContentPane().add(archiveButton);
		window.pack();
		window.setVisible(true);
	}
	
	public static void main(String args[]) {
		createWindow();
	}
	
}
