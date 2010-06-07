package falcon.components.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import falcon.components.gui.LabeledField;

//TODO LogbookFrame

public class LogbookFrame extends JFrame {
	
	private JTextArea logbookPanel;
	private JCheckBox me;
	private JCheckBox events;
	private JCheckBox others;
	
	private boolean showMe = true;
	private boolean showOthers = true;
	private boolean showEvents = true;
	
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
		me = new JCheckBox("Me", true);
		me.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showMe = e.getStateChange() == ItemEvent.SELECTED;
			}
		});
		configPanel.add(me);
		events = new JCheckBox("Events", true);
		events.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showEvents = e.getStateChange() == ItemEvent.SELECTED;
			}
		});
		configPanel.add(events);
		others = new JCheckBox("Others", true);
		others.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				showOthers = e.getStateChange() == ItemEvent.SELECTED;
			}
		});
		configPanel.add(others);
		
		// Setup content pane
		logbookPanel = new JTextArea(20,50);
		logbookPanel.setEditable(false);
		JScrollPane contentPanel = new JScrollPane(logbookPanel);
		contentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// Setup entry pane
		JPanel entryPanel = new JPanel();
		LabeledField name = new LabeledField("Name:", 10, LabeledField.INLINE, true);
		entryPanel.add(name, BorderLayout.WEST);
		LabeledField msg = new LabeledField("Message:", 30, LabeledField.INLINE, true);
		entryPanel.add(msg, BorderLayout.CENTER);
		JButton submit = new JButton("Add");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Logbook action listener
			}
		});
		entryPanel.add(submit, BorderLayout.EAST);
		
		panel.add(configPanel);
		panel.add(contentPanel);
		panel.add(entryPanel);
		
		pack();
	}

}
