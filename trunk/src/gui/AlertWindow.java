package gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.infonode.docking.View;

public class AlertWindow extends View {
	
	public AlertWindow() {
		super("Status", null, null);
		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		root.add(new JLabel("Alert\nPanel"));
		
		setComponent(root);
	}

}
