package falcon.components.windows;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
				//FalconApp.windowClosing("Map");
			}
		});
		super.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				//TODO MapFrame handle window resize
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		super.getContentPane().add(panel);
		map = new MapPanel(new Location(42.03, -93.63));
		map.setPreferredSize(new Dimension(800, 600));
		map.setDataProviderCreditShown(true);
		panel.add(map);
		
		JPanel settingsPanel = new JPanel();
		JButton auto = new JButton("Auto");
		auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO MapFrame auto zoom
			}
		});
		settingsPanel.add(auto);
		JCheckBox showOrig = new JCheckBox("Show Original Prediction");
		showOrig.setSelected(true);
		showOrig.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//TODO
			}
		});
		settingsPanel.add(showOrig);
		JCheckBox showRec = new JCheckBox("Show Recovery");
		showRec.setSelected(true);
		showRec.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//TODO
			}
		});
		settingsPanel.add(showRec);
		JCheckBox showSec = new JCheckBox("Show Secondary");
		showSec.setSelected(true);
		showSec.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//TODO
			}
		});
		settingsPanel.add(showSec);
		JButton save = new JButton("Save");
		final JFileChooser fc = new JFileChooser();
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fc.showSaveDialog(MapFrame.this) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					map.saveMapImage(file);
				}
			}
		});
		settingsPanel.add(save);
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.printMap();
			}
		});
		settingsPanel.add(print);
		panel.add(settingsPanel);
		
		super.pack();
	}
	
}
