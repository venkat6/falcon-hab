package falcon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import falcon.components.windows.AboutFrame;

public class FalconApp extends JFrame {
	
	FalconApp me = this;
	JDesktopPane desktop;
	
	
	public FalconApp() {
		super("FALCON - Flight Tracking");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				falcon.Launcher.closing(me);
			}
		});
		createGUI();
		setVisible(true);
	}
	
	private void createGUI() {
		desktop = new JDesktopPane();
		setContentPane(desktop);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutFrame();
			}
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);
				
		setJMenuBar(menuBar);
	}

}
