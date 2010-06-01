package falcon.components.gui;

// TODO add functionality

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * A basic status bar class.  Allows basic text display
 * @author Ethan Harstad
 *
 */
public class StatusBar extends JLabel {
	
	public StatusBar(String message) {
		super(message);
		setBorder(BorderFactory.createEtchedBorder());
	}

}
