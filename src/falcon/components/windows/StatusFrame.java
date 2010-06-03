package falcon.components.windows;

import falcon.FalconApp;
import falcon.components.gui.Clock;
import falcon.components.gui.MissionClock;
import falcon.components.gui.StatusBar;
import falcon.components.gui.StatusIndicator;
import falcon.components.gui.TitledPanel;
import java.awt.Component;
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
public class StatusFrame extends JFrame {

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
		super("Status");
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				FalconApp.windowClosing("Status");
			}
		});
		
		JPanel panel = new JPanel();
		super.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JLabel clockLabel = new JLabel("Local Time:");
		clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(clockLabel);
		Clock clock = new Clock(16);
		clock.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(clock);
		JLabel missionTimeLabel = new JLabel("Mission Time:");
		missionTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(missionTimeLabel);
		missionTime = new MissionClock(16);
		missionTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(missionTime);
		TitledPanel primaryPanel = new TitledPanel("Primary");
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.PAGE_AXIS));
		primaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		primaryGPS = new StatusIndicator("GPS Lock");
		primaryGPS.setState(StatusIndicator.STATUS_ABNORMAL);
		primaryGPS.setAlignmentX(Component.CENTER_ALIGNMENT);
		primaryPanel.add(primaryGPS);
		primaryVoltage = new StatusIndicator("Voltage");
		primaryVoltage.setState(StatusIndicator.STATUS_ABNORMAL);
		primaryVoltage.setAlignmentX(Component.CENTER_ALIGNMENT);
		primaryPanel.add(primaryVoltage);
		primaryVoltageDisplay = new JTextField("--.-v",5);
		primaryVoltageDisplay.setEditable(false);
		primaryVoltageDisplay.setHorizontalAlignment(JTextField.CENTER);
		primaryVoltageDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
		primaryPanel.add(primaryVoltageDisplay);
		primaryUpdate = new StatusIndicator("Good Packets", "Bad Packets", "Comms Lost", true, true);
		primaryUpdate.setState(StatusIndicator.STATUS_ABNORMAL);
		primaryUpdate.setAlignmentX(Component.CENTER_ALIGNMENT);
		primaryPanel.add(primaryUpdate);
		//TODO add primary status indicators
		panel.add(primaryPanel);
		TitledPanel secondaryPanel = new TitledPanel("Secondary");
		secondaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.PAGE_AXIS));
		secondaryGPS = new StatusIndicator("GPS Lock");
		secondaryGPS.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryGPS.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondaryPanel.add(secondaryGPS);
		secondaryVoltage = new StatusIndicator("Voltage");
		secondaryVoltage.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryVoltage.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondaryPanel.add(secondaryVoltage);
		secondaryVoltageDisplay = new JTextField("--.-v", 5);
		secondaryVoltageDisplay.setEditable(false);
		secondaryVoltageDisplay.setHorizontalAlignment(JTextField.CENTER);
		secondaryVoltageDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondaryPanel.add(secondaryVoltageDisplay);
		secondaryUpdate = new StatusIndicator("Good Packets", "Bad Packets", "Comms Lost", true, true);
		secondaryUpdate.setState(StatusIndicator.STATUS_ABNORMAL);
		secondaryUpdate.setAlignmentX(Component.CENTER_ALIGNMENT);
		secondaryPanel.add(secondaryUpdate);
		//TODO add secondary status indicators
		panel.add(secondaryPanel);
		TitledPanel recoveryPanel = new TitledPanel("Recovery");
		recoveryPanel.setLayout(new BoxLayout(recoveryPanel, BoxLayout.PAGE_AXIS));
		recoveryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		recovery = new StatusIndicator("Recovery");
		recovery.setState(StatusIndicator.STATUS_ABNORMAL);
		recovery.setAlignmentX(Component.CENTER_ALIGNMENT);
		recoveryPanel.add(recovery);
		//TODO add recovery status indicators
		panel.add(recoveryPanel);
		statusBar = new StatusBar("Status");
		statusBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(statusBar, java.awt.BorderLayout.SOUTH);
		
		super.pack();
	}
	
}
