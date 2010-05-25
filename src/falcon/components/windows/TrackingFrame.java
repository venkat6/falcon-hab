package falcon.components.windows;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import falcon.FalconApp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * 
 * @author Ethan Harstad
 *
 */
public class TrackingFrame extends Thread implements ActionListener {
	
	private static JFrame window;
	
	static JMenuItem mTrackingCallsigns;
	
	public TrackingFrame() {
		window = new JFrame("Tracking");
		window.setPreferredSize(new Dimension(400,400));
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setVisible(false);
				FalconApp.windowClosing("Tracking");
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		JMenu mTracking = new JMenu("Tracking");
		menuBar.add(mTracking);
		mTrackingCallsigns = new JMenuItem("Callsigns");
		mTrackingCallsigns.addActionListener(this);
		mTracking.add(mTrackingCallsigns);
		window.setJMenuBar(menuBar);
		start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mTrackingCallsigns) {
			System.out.println("Callsigns item clicked");
		} else {
			
		}
	}
	
	public void setVisible(boolean v) {
		window.setVisible(v);
	}
	
	public void run() {
		
	}

}
