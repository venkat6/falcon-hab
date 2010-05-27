package falcon.components.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Clock extends JTextField implements ActionListener {

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public Clock(int size) {
		super(9);
		setFont(new Font("Monospaced", Font.PLAIN, size));
		setEditable(false);
		setHorizontalAlignment(JTextField.CENTER);
		setText(sdf.format(Calendar.getInstance().getTime()));
		Timer timer = new Timer(1000, this);
		timer.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		setText(sdf.format(Calendar.getInstance().getTime()));
		repaint();
	}
	
}
