package falcon.components.windows;

import falcon.FalconApp;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutFrame extends JFrame {

	public AboutFrame() {
		super("About FALCON");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel aboutText = new JLabel("<html>" +
				"<h2>FALCON (Flight And Logistics CONtrol)</h2>" +
				"is an open source project created by SSCL at Iowa State University<br>" +
				"to help track and predict the flight of high altitude balloon systems.<hr>" +
				"</html>");
		JLabel authorText = new JLabel("<html>" +
				"Authors:<ul><li>Ethan Harstad</li>" +
				"<li>Joe Coleman</li>" +
				"<li>Matthew Nelson</li>" +
				"</ul><br></html>");
		JLabel versionText = new JLabel("Version: " + FalconApp.VERSION);
		JLabel linkText = new JLabel("<html>" +
				"Project Page: http://www.google.com/p/falcon-hab<br>" +
				"Space Systems and Controls Lab: http://www.sscl.iastate.edu<br>" +
				"Iowa State University: http://www.iastate.edu" +
				"<br></html>");
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panel.add(aboutText);
		panel.add(authorText);
		panel.add(linkText);
		panel.add(versionText);
		pack();
		setVisible(true);
	}
	
}
