package falcon.components.windows;

import falcon.FalconApp;
import falcon.components.gui.Clock;
import falcon.components.gui.MissionClock;
import falcon.components.gui.StatusBar;
import falcon.components.gui.StatusIndicator;
import falcon.components.gui.TitledPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A window to display all relevant payload status information in a single glance.
 * @author Ethan Harstad
 *
 */
public class StatusFrame extends Thread {

	private static JFrame window;
	private static MissionClock missionTime;
	private static StatusBar statusBar;
	private static StatusIndicator primaryGPS;
	private static StatusIndicator primaryVoltage;
	private static JTextField primaryVoltageDisplay;
	private static StatusIndicator primaryUpdate;
	private static StatusIndicator secondaryGPS;
	private static StatusIndicator secondaryVoltage;
	private static JTextField secondaryVoltageDisplay;
	private static StatusIndicator secondaryUpdate;
	private static StatusIndicator recovery;
	
	/**
	 * Default constructor
	 */
	public StatusFrame() {
		window = new JFrame("Status");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				window.setVisible(false);
				FalconApp.windowClosing("Status");
			}
		});
		
		JPanel panel = new JPanel();
		window.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel clockLabel = new JLabel("Local Time:");
		panel.add(clockLabel);
		Clock clock = new Clock(16);
		panel.add(clock);
		JLabel missionTimeLabel = new JLabel("Mission Time:");
		panel.add(missionTimeLabel);
		missionTime = new MissionClock(16);
		panel.add(missionTime);
		TitledPanel primaryPanel = new TitledPanel("Primary");
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.PAGE_AXIS));
		primaryGPS = new StatusIndicator("GPS Lock");
		primaryPanel.add(primaryGPS);
		primaryVoltage = new StatusIndicator("Voltage");
		primaryVoltage.setState(StatusIndicator.STATUS_ABNORMAL);
		primaryPanel.add(primaryVoltage);
		primaryVoltageDisplay = new JTextField("--.-v",5);
		primaryPanel.add(primaryVoltageDisplay);
		primaryUpdate = new StatusIndicator("Good Packets", "Bad Packets", "Comms Lost", true, true);
		primaryUpdate.setState(StatusIndicator.STATUS_ABNORMAL);
		primaryPanel.add(primaryUpdate);
		//TODO add primary status indicators
		panel.add(primaryPanel);
		TitledPanel secondaryPanel = new TitledPanel("Secondary");
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.PAGE_AXIS));
		secondaryGPS = new StatusIndicator("GPS Lock");
		secondaryPanel.add(secondaryGPS);
		secondaryVoltage = new StatusIndicator("Voltage");
		secondaryVoltage.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryPanel.add(secondaryVoltage);
		secondaryVoltageDisplay = new JTextField("--.-v", 5);
		secondaryPanel.add(secondaryVoltageDisplay);
		secondaryUpdate = new StatusIndicator("Good Packets", "Bad Packets", "Comms Lost", true, true);
		secondaryUpdate.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryPanel.add(secondaryUpdate);
		//TODO add secondary status indicators
		panel.add(secondaryPanel);
		TitledPanel recoveryPanel = new TitledPanel("Recovery");
		recoveryPanel.setLayout(new BoxLayout(recoveryPanel, BoxLayout.PAGE_AXIS));
		recovery = new StatusIndicator("Recovery");
		recoveryPanel.add(recovery);
		//TODO add recovery status indicators
		panel.add(recoveryPanel);
		statusBar = new StatusBar("Status");
		panel.add(statusBar, java.awt.BorderLayout.SOUTH);
		
		primaryGPS.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryGPS.setState(StatusIndicator.STATUS_ABNORMAL);
		recovery.setState(StatusIndicator.STATUS_ABNORMAL);
		
		window.pack();
		start();
	}
	
	public void setVisible(boolean state) {
		window.setVisible(state);
	}
	
	public void run() {
		
	}
	
}