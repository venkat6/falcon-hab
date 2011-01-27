package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.infonode.docking.View;

public class TrackingManager extends View {
	
	public TrackingManager() {
		super("Flight Tracking", null, null);
		JPanel root = new JPanel();
		
		root.add(new JLabel("dummy text"));
		
		setComponent(root);
	}

}
