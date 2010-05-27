package falcon.components.windows;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import falcon.FalconApp;

public class PredictionFrame extends Thread {
	
	private static JFrame window;
	
	public PredictionFrame() {
		window = new JFrame("Tracking");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setVisible(false);
				FalconApp.windowClosing("Prediction");
			}
		});
		
		window.pack();
		start();
	}
	
	public void run() {
		
	}
	
	public void setVisible(boolean state) {
		window.setVisible(state);
	}

}
