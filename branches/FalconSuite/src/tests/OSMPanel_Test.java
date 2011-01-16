package tests;

import java.awt.Dimension;

import gui.map.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class OSMPanel_Test {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Map Panel Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel root = new JPanel();
		frame.getContentPane().add(root);
		
		OSMPanel map = new OSMPanel(new Dimension(500, 500));
		root.add(map);
		
		frame.pack();
		frame.setVisible(true);
	}

}
