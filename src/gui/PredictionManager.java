package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.infonode.docking.View;

public class PredictionManager extends View {
	
	public PredictionManager() {
		super("Flight Prediction", null, null);
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("Flight prediction window, dummy text"));
		
		setComponent(panel);
	}

}
