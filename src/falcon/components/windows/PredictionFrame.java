package falcon.components.windows;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import falcon.FalconApp;
import falcon.components.gui.LabeledField;
import falcon.components.gui.TitledPanel;

public class PredictionFrame extends Thread {
	
	private static JFrame window;
	private static LabeledField timeBurst;
	private static LabeledField burstLocation;
	private static LabeledField burstAltitude;
	private static LabeledField timeLand;
	private static LabeledField landLocation;
	
	public PredictionFrame() {
		window = new JFrame("Prediction");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setVisible(false);
				FalconApp.windowClosing("Prediction");
			}
		});
		
		JPanel panel = new JPanel();
		window.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		TitledPanel burstPanel = new TitledPanel("Burst");
		burstPanel.setLayout(new BoxLayout(burstPanel, BoxLayout.PAGE_AXIS));
		panel.add(burstPanel);
		timeBurst = new LabeledField("Time to Burst:", 9, LabeledField.INLINE, false);
		burstPanel.add(timeBurst);
		burstAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		burstPanel.add(burstAltitude);
		burstLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		burstPanel.add(burstLocation);
		
		TitledPanel landingPanel = new TitledPanel("Landing");
		landingPanel.setLayout(new BoxLayout(landingPanel, BoxLayout.PAGE_AXIS));
		panel.add(landingPanel);
		timeLand = new LabeledField("Time to Landing:", 9, LabeledField.INLINE, false);
		landingPanel.add(timeLand);
		landLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		landingPanel.add(landLocation);
		
		window.pack();
		start();
	}
	
	public void run() {
		
	}
	
	public void setVisible(boolean state) {
		window.setVisible(state);
	}

}
