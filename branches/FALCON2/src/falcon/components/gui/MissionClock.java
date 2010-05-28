package falcon.components.gui;

//TODO finish and document

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import javax.swing.JTextField;
import javax.swing.Timer;

public class MissionClock extends JTextField {
	
	private static Calendar initialTime = Calendar.getInstance();
	private static boolean running = true;
	private static Timer timer;
	
	public MissionClock(int size) {
		super(9);
		setFont(new Font("Monospaced", Font.PLAIN, size));
		setEditable(false);
		setHorizontalAlignment(JTextField.CENTER);
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar time = Calendar.getInstance();
				long missionTime = time.getTimeInMillis() - initialTime.getTimeInMillis();
				String x = missionTime < 0 ? "-" : "+";
				missionTime = Math.abs(missionTime / 1000);
				int hours = (int)(missionTime / 3600);
				missionTime -= hours * 3600;
				int minutes = (int)(missionTime / 60);
				missionTime -= minutes * 60;
				int seconds = (int)missionTime;
				String h = hours > 10 ? Integer.toString(hours) : "0" + Integer.toString(hours); 
				String m = minutes > 10 ? Integer.toString(minutes) : "0" + Integer.toString(minutes);
				String s = seconds > 10 ? Integer.toString(seconds) : "0" + Integer.toString(seconds);
				setText(x + h + ":" + m + ":" + s);
			}
		});
		timer.start();
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				//FIXME
				//TODO more advanced functionality for mission timer
				if(e.getButton() == MouseEvent.BUTTON1) {
					if(running) {
						timer.stop();
						running = false;
					} else {
						timer.start();
						running = true;
					}
				} else {
					initialTime = Calendar.getInstance();
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
	}
	
}
