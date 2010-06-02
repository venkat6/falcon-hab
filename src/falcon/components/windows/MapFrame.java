package falcon.components.windows;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import falcon.FalconApp;
import falcon.components.gui.MapPanel;
import falcon.components.datatypes.Location;

public class MapFrame extends Thread {
	
	JFrame frame;
	MapPanel map;
	
	public MapFrame() {
		frame = new JFrame("Map");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				FalconApp.windowClosing("Map");
			}
		});
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		map = new MapPanel(new Location(42.03, -93.63));
		map.setPreferredSize(new Dimension(800, 600));
		panel.add(map);
		
		frame.pack();
		start();
	}
	
	public void setVisible(boolean state) {
		frame.setVisible(state);
	}
	
	public void run() {
		
	}
	
}
