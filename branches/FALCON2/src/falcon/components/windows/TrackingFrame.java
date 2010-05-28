package falcon.components.windows;

import falcon.FalconApp;
import falcon.components.gui.LabeledField;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * A window to display all the relevant tracking information for any object
 * being tracked by the system.
 * @author Ethan Harstad
 *
 */
public class TrackingFrame extends Thread implements ActionListener {
	
	private static JFrame window;
	private static ArrayList<Calls> mCallsigns = new ArrayList<Calls>();
	
	private static LabeledField primaryLocation;
	private static LabeledField primaryAltitude;
	private static LabeledField primaryLastHeard;
	private static JTextArea primaryLastPacket;
	private static LabeledField secondaryLocation;
	private static LabeledField secondaryAltitude;
	private static LabeledField secondaryLastHeard;
	private static JTextArea secondaryLastPacket;
	private static LabeledField recoveryLocation;
	private static LabeledField recoveryLastHeard;
	private static JTextArea recoveryLastPacket;
	
	static JMenuItem mTrackingCallsigns;
	
	public TrackingFrame() {
		window = new JFrame("Tracking");
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
		
		JTabbedPane mainPane = new JTabbedPane();
		JPanel primaryPanel = new JPanel();
		mainPane.addTab("Primary", primaryPanel);
		JPanel secondaryPanel = new JPanel();
		mainPane.addTab("Secondary", secondaryPanel);
		JPanel recoveryPanel = new JPanel();
		mainPane.addTab("Recovery", recoveryPanel);
		window.getContentPane().add(mainPane);
		
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.PAGE_AXIS));
		primaryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		primaryPanel.add(primaryLocation);
		primaryAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		primaryPanel.add(primaryAltitude);
		primaryLastHeard = new LabeledField("Last Heard:", 8, LabeledField.INLINE, false);
		primaryPanel.add(primaryLastHeard);
		JLabel lPrimaryLastPacket = new JLabel("Last Packet:");
		primaryPanel.add(lPrimaryLastPacket);
		primaryLastPacket = new JTextArea(2,30);
		primaryLastPacket.setEditable(false);
		primaryPanel.add(primaryLastPacket);
		
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.PAGE_AXIS));
		secondaryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		secondaryPanel.add(secondaryLocation);
		secondaryAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		secondaryPanel.add(secondaryAltitude);
		secondaryLastHeard = new LabeledField("Last Heard:", 8, LabeledField.INLINE, false);
		secondaryPanel.add(secondaryLastHeard);
		JLabel lSecondaryLastPacket = new JLabel("Last Packet:");
		secondaryPanel.add(lSecondaryLastPacket);
		secondaryLastPacket = new JTextArea(2,30);
		secondaryLastPacket.setEditable(false);
		secondaryPanel.add(secondaryLastPacket);
		
		recoveryPanel.setLayout(new BoxLayout(recoveryPanel, BoxLayout.PAGE_AXIS));
		recoveryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		recoveryPanel.add(recoveryLocation);
		recoveryLastHeard = new LabeledField("Last Heard:", 9, LabeledField.INLINE, false);
		recoveryPanel.add(recoveryLastHeard);
		JLabel lRecoveryLastPacket = new JLabel("Last Packet:");
		recoveryPanel.add(lRecoveryLastPacket);
		recoveryLastPacket = new JTextArea(2,30);
		recoveryLastPacket.setEditable(false);
		recoveryPanel.add(recoveryLastPacket);
		
		window.pack();
		start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mTrackingCallsigns) {
			new CallsignsTrackingFrame(this, mCallsigns);
		} else {
			
		}
	}
	
	public void updateCallsigns(ArrayList<Calls> callsigns) {
		mCallsigns = callsigns;
		//TODO
	}
	
	public void setVisible(boolean v) {
		window.setVisible(v);
	}
	
	public void run() {
		
	}

}

class Calls {
	private String mCallsign;
	private int mType;
	
	public Calls(String callsign, int type) {
		mCallsign = callsign;
		mType = type;
	}
	
	public String getCallsign() {
		return mCallsign;
	}
	
	public int getType() {
		return mType;
	}
}
