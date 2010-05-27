package falcon.components.windows;

import falcon.FalconApp;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
	private static ArrayList<Calls> mCallsigns = new ArrayList<Calls>();
	
	private static JTextField primaryLocation;
	private static JTextField primaryLastHeard;
	private static JTextArea primaryLastPacket;
	private static JTextField secondaryLocation;
	private static JTextField secondaryLastHeard;
	private static JTextArea secondaryLastPacket;
	private static JTextField recoveryLocation;
	private static JTextField recoveryLastHeard;
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
		JLabel lPrimaryLocation = new JLabel("Position:");
		primaryPanel.add(lPrimaryLocation);
		primaryLocation = new JTextField(30);
		primaryLocation.setEditable(false);
		primaryPanel.add(primaryLocation);
		JLabel lPrimaryLastHeard = new JLabel("Last Heard:");
		primaryPanel.add(lPrimaryLastHeard);
		primaryLastHeard = new JTextField(30);
		primaryLastHeard.setEditable(false);
		primaryPanel.add(primaryLastHeard);
		JLabel lPrimaryLastPacket = new JLabel("Last Packet:");
		primaryPanel.add(lPrimaryLastPacket);
		primaryLastPacket = new JTextArea(2,30);
		primaryLastPacket.setEditable(false);
		primaryPanel.add(primaryLastPacket);
		
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.PAGE_AXIS));
		JLabel lSecondaryLocation = new JLabel("Position:");
		secondaryPanel.add(lSecondaryLocation);
		secondaryLocation = new JTextField(30);
		secondaryLocation.setEditable(false);
		secondaryPanel.add(secondaryLocation);
		JLabel lSecondaryLastHeard = new JLabel("Last Heard:");
		secondaryPanel.add(lSecondaryLastHeard);
		secondaryLastHeard = new JTextField(30);
		secondaryLastHeard.setEditable(false);
		secondaryPanel.add(secondaryLastHeard);
		JLabel lSecondaryLastPacket = new JLabel("Last Packet:");
		secondaryPanel.add(lSecondaryLastPacket);
		secondaryLastPacket = new JTextArea(2,30);
		secondaryLastPacket.setEditable(false);
		secondaryPanel.add(secondaryLastPacket);
		
		recoveryPanel.setLayout(new BoxLayout(recoveryPanel, BoxLayout.PAGE_AXIS));
		JLabel lRecoveryLocation = new JLabel("Position:");
		recoveryPanel.add(lRecoveryLocation);
		recoveryLocation = new JTextField(30);
		recoveryLocation.setEditable(false);
		recoveryPanel.add(recoveryLocation);
		JLabel lRecoveryLastHeard = new JLabel("Last Heard:");
		recoveryPanel.add(lRecoveryLastHeard);
		recoveryLastHeard = new JTextField(30);
		recoveryLastHeard.setEditable(false);
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
