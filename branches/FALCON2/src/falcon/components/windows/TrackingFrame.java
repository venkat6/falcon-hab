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
public class TrackingFrame extends JFrame implements ActionListener {
	
	private static ArrayList<Calls> mCallsigns = new ArrayList<Calls>();
	
	private static LabeledField primaryLocation;
	private static LabeledField primaryAltitude;
	private static LabeledField primaryHeading;
	private static LabeledField primaryAscent;
	private static LabeledField primaryAzEl;
	private static LabeledField primaryLastHeard;
	private static JTextArea primaryLastPacket;
	private static LabeledField secondaryLocation;
	private static LabeledField secondaryAltitude;
	private static LabeledField secondaryHeading;
	private static LabeledField secondaryAscent;
	private static LabeledField secondaryAzEl;
	private static LabeledField secondaryLastHeard;
	private static JTextArea secondaryLastPacket;
	private static LabeledField recoveryLocation;
	private static LabeledField recoveryLastHeard;
	private static JTextArea recoveryLastPacket;
	
	static JMenuItem mTrackingCallsigns;
	
	public TrackingFrame() {
		super("Tracking");
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				FalconApp.windowClosing("Tracking");
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		JMenu mTracking = new JMenu("Tracking");
		menuBar.add(mTracking);
		mTrackingCallsigns = new JMenuItem("Callsigns");
		mTrackingCallsigns.addActionListener(this);
		mTracking.add(mTrackingCallsigns);
		super.setJMenuBar(menuBar);
		
		JTabbedPane mainPane = new JTabbedPane();
		JPanel primaryPanel = new JPanel();
		mainPane.addTab("Primary", primaryPanel);
		JPanel secondaryPanel = new JPanel();
		mainPane.addTab("Secondary", secondaryPanel);
		JPanel recoveryPanel = new JPanel();
		mainPane.addTab("Recovery", recoveryPanel);
		super.getContentPane().add(mainPane);
		
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.PAGE_AXIS));
		primaryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		primaryLocation.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryLocation);
		primaryAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		primaryAltitude.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryAltitude);
		primaryHeading = new LabeledField("Heading:", 4, LabeledField.INLINE, false);
		primaryHeading.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryHeading);
		primaryAscent = new LabeledField("Ascent Rate:", 6, LabeledField.INLINE, false);
		primaryAscent.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryAscent);
		primaryAzEl = new LabeledField("Az/El:", 6, LabeledField.INLINE, false);
		primaryAzEl.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryAzEl);
		primaryLastHeard = new LabeledField("Last Heard:", 8, LabeledField.INLINE, false);
		primaryLastHeard.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryLastHeard);
		JLabel lPrimaryLastPacket = new JLabel("Last Packet:");
		lPrimaryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(lPrimaryLastPacket);
		primaryLastPacket = new JTextArea(2,30);
		primaryLastPacket.setEditable(false);
		primaryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		primaryPanel.add(primaryLastPacket);
		
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.PAGE_AXIS));
		secondaryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		secondaryLocation.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryLocation);
		secondaryAltitude = new LabeledField("Altitude:", 9, LabeledField.INLINE, false);
		secondaryAltitude.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryAltitude);
		secondaryHeading = new LabeledField("Heading:", 4, LabeledField.INLINE, false);
		secondaryHeading.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryHeading);
		secondaryAscent = new LabeledField("Ascent Rate:", 6, LabeledField.INLINE, false);
		secondaryAscent.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryAscent);
		secondaryAzEl = new LabeledField("Az/El:", 6, LabeledField.INLINE, false);
		secondaryAzEl.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryAzEl);
		secondaryLastHeard = new LabeledField("Last Heard:", 8, LabeledField.INLINE, false);
		secondaryLastHeard.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryLastHeard);
		JLabel lSecondaryLastPacket = new JLabel("Last Packet:");
		lSecondaryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(lSecondaryLastPacket);
		secondaryLastPacket = new JTextArea(2,30);
		secondaryLastPacket.setEditable(false);
		secondaryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		secondaryPanel.add(secondaryLastPacket);
		
		recoveryPanel.setLayout(new BoxLayout(recoveryPanel, BoxLayout.PAGE_AXIS));
		recoveryLocation = new LabeledField("Position:", 18, LabeledField.STACKED, false);
		recoveryLocation.setAlignmentX(LEFT_ALIGNMENT);
		recoveryPanel.add(recoveryLocation);
		recoveryLastHeard = new LabeledField("Last Heard:", 9, LabeledField.INLINE, false);
		recoveryLastHeard.setAlignmentX(LEFT_ALIGNMENT);
		recoveryPanel.add(recoveryLastHeard);
		JLabel lRecoveryLastPacket = new JLabel("Last Packet:");
		lRecoveryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		recoveryPanel.add(lRecoveryLastPacket);
		recoveryLastPacket = new JTextArea(2,30);
		recoveryLastPacket.setEditable(false);
		recoveryLastPacket.setAlignmentX(LEFT_ALIGNMENT);
		recoveryPanel.add(recoveryLastPacket);
		
		super.pack();
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
		//TODO updateCallsigns
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
