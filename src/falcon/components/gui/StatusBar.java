package falcon.components.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class StatusBar extends JLabel {
	
	public StatusBar(String message) {
		super(message);
		setBorder(BorderFactory.createEtchedBorder());
	}

}
