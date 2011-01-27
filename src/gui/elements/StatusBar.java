package gui.elements;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
	
	private JLabel leftStatus;
	private JLabel rightStatus;
	
	public StatusBar() {
		super();
		setLayout(new BorderLayout());
		leftStatus = new JLabel("Left message");
		rightStatus = new JLabel("Right Message", JLabel.TRAILING);
		add(leftStatus, BorderLayout.WEST);
		add(rightStatus, BorderLayout.EAST);
	}

}
