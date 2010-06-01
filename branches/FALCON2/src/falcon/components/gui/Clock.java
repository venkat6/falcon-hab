package falcon.components.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * A basic text component that displays the current time using the default locale.
 * @author Ethan Harstad
 */
public class Clock extends JTextField {

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Creates a standard instance of the Clock with the given font size.
	 * @param size
	 */
	public Clock(int size) {
		super(9);
		// Create the font used to display the time
		setFont(new Font("Monospaced", Font.PLAIN, size));
		setEditable(false);
		setHorizontalAlignment(JTextField.CENTER);
		// Set initial text
		setText(sdf.format(Calendar.getInstance().getTime()));
		// Create a timer to automatically handle updating and redrawing
		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Time to redraw the clock
				setText(sdf.format(Calendar.getInstance().getTime()));
				repaint();
			}
		});
		timer.start();
	}
	
}
