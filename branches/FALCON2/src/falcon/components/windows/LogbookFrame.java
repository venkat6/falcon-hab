package falcon.components.windows;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//TODO LogbookFrame

public class LogbookFrame extends JFrame {
	
	public LogbookFrame() {
		// Setup window
		super("Logbook");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		super.getContentPane().add(panel);
				
		// Setup config menu
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.LINE_AXIS));
		JLabel lShow = new JLabel("Show:");
		configPanel.add(lShow);
		JCheckBox me = new JCheckBox("Me");
		configPanel.add(me);
		JCheckBox events = new JCheckBox("Events");
		configPanel.add(events);
		JCheckBox others = new JCheckBox("Others");
		configPanel.add(others);
		
		// Setup content pane
		JScrollPane contentPanel = new JScrollPane();
		contentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		panel.add(configPanel);
		panel.add(contentPanel);
		
		pack();
	}

}
