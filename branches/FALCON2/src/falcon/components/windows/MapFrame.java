package falcon.components.windows;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import falcon.FalconApp;
import falcon.components.gui.MapPanel;
import falcon.components.datatypes.Location;

public class MapFrame extends JFrame {
	
	MapPanel map;
	
	public MapFrame() {
		super("Map");
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				FalconApp.windowClosing("Map");
			}
		});
		
		JPanel panel = new JPanel();
		super.getContentPane().add(panel);
		map = new MapPanel(new Location(42.03, -93.63));
		map.setPreferredSize(new Dimension(800, 600));
		panel.add(map);
		
		super.pack();
	}
	
}
