package falcon.components.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CallsignsTrackingFrame extends JFrame {

	private static TrackingFrame mParent;
	private static JTextField primaryCallsign;
	private static JTextField secondaryCallsign;
	private static JTextField recoveryCallsign;
	
	public CallsignsTrackingFrame(TrackingFrame parent, ArrayList<Calls> tracking) {
		super("Callsigns");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		mParent = parent;
		
		setPreferredSize(new Dimension(200,220));
		this.getContentPane().setLayout(new FlowLayout());
		
		JLabel primaryLabel = new JLabel("Primary Tracking Callsign:");
		this.getContentPane().add(primaryLabel);
		primaryCallsign = new JTextField(10);
		this.getContentPane().add(primaryCallsign);
		JLabel secondaryLabel = new JLabel("Secondary Tracking Callsign:");
		this.getContentPane().add(secondaryLabel);
		secondaryCallsign = new JTextField(10);
		this.getContentPane().add(secondaryCallsign);
		JLabel recoveryLabel = new JLabel("Recovery Callsign");
		this.getContentPane().add(recoveryLabel);
		recoveryCallsign = new JTextField(10);
		this.getContentPane().add(recoveryCallsign);
		for(Calls x : tracking) {
			if(x.getType() == 0) {
				primaryCallsign.setText(x.getCallsign());
			} else if(x.getType() == 1) {
				secondaryCallsign.setText(x.getCallsign());
			} else if(x.getType() == 2) {
				recoveryCallsign.setText(x.getCallsign());
			}
		}
		
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirm();
				close();
			}
		});
		this.getContentPane().add(confirmButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		this.getContentPane().add(cancelButton);
		
		pack();
		setVisible(true);
	}
	
	private void confirm() {
		ArrayList<Calls> calls = new ArrayList<Calls>();
		calls.add(new Calls(primaryCallsign.getText(), 0));
		calls.add(new Calls(secondaryCallsign.getText(), 1));
		mParent.updateCallsigns(calls);
	}
	
	private void close() {
		setVisible(false);
	}
	
}
