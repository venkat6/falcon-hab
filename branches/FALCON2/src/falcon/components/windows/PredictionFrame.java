package falcon.components.windows;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import falcon.FalconApp;
import falcon.components.gui.LabeledField;
import falcon.components.gui.TitledPanel;

public class PredictionFrame extends JFrame {
	
	private static JComboBox activeCallsign;
	private static LabeledField timeBurst;
	private static LabeledField burstLocation;
	private static LabeledField burstAltitude;
	private static LabeledField timeLand;
	private static LabeledField landLocation;
	
	public PredictionFrame() {
		super("Prediction");
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				FalconApp.windowClosing("Prediction");
			}
		});
		
		JPanel panel = new JPanel();
		super.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		activeCallsign = new JComboBox(new String[] {"Primary", "Secondary"});
		activeCallsign.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(activeCallsign);
		//TODO PredictionFrame callsign listener
		TitledPanel burstPanel = new TitledPanel("Burst");
		burstPanel.setLayout(new BoxLayout(burstPanel, BoxLayout.PAGE_AXIS));
		burstPanel.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(burstPanel);
		timeBurst = new LabeledField("Time to Burst:", 9, LabeledField.INLINE, false);
		timeBurst.setAlignmentX(LEFT_ALIGNMENT);
		burstPanel.add(timeBurst);
		burstAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		burstAltitude.setAlignmentX(LEFT_ALIGNMENT);
		burstPanel.add(burstAltitude);
		burstLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		burstLocation.setAlignmentX(LEFT_ALIGNMENT);
		burstPanel.add(burstLocation);
		
		TitledPanel landingPanel = new TitledPanel("Landing");
		landingPanel.setLayout(new BoxLayout(landingPanel, BoxLayout.PAGE_AXIS));
		landingPanel.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(landingPanel);
		timeLand = new LabeledField("Time to Landing:", 9, LabeledField.INLINE, false);
		timeLand.setAlignmentX(LEFT_ALIGNMENT);
		landingPanel.add(timeLand);
		landLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		landLocation.setAlignmentX(LEFT_ALIGNMENT);
		landingPanel.add(landLocation);
		
		super.pack();
	}
	
}
