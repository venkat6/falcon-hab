package falcon.components.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import falcon.FalconApp;
import falcon.components.gui.LabeledField;

public class RadioFrame extends JFrame {
	
	private LabeledField host;
	private LabeledField port;
	private JTextArea logPane;
	private JButton connect;
	private JButton disconnect;
	private LabeledField txCall;
	private JComboBox txPort;
	
	public RadioFrame() {
		super("Radio");
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				FalconApp.windowClosing("Radio");
			}
		});
		
		JTabbedPane tabs = new JTabbedPane();
		super.getContentPane().add(tabs);
		
		JPanel pAGWPE = new JPanel();
		tabs.addTab("AGWPE", pAGWPE);
		host = new LabeledField("Host:", 15, LabeledField.INLINE, true);
		pAGWPE.add(host);
		port = new LabeledField("Port:", 6, LabeledField.INLINE, true);
		pAGWPE.add(port);
		txCall = new LabeledField("Transmit Call:", 10, LabeledField.INLINE, true);
		pAGWPE.add(txCall);
		JLabel txpLabel = new JLabel("Transmit Port:");
		pAGWPE.add(txpLabel);
		String[] items = {"Select transmit port", ""};
		txPort = new JComboBox(items);
		pAGWPE.add(txPort);
		JPanel buttonPanel = new JPanel();
		connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		buttonPanel.add(connect);
		disconnect = new JButton("Disconnect");
		disconnect.setEnabled(false);
		disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		});
		buttonPanel.add(disconnect);
		pAGWPE.add(buttonPanel, BorderLayout.SOUTH);
		
		JPanel pLog = new JPanel();
		tabs.addTab("Log", pLog);
		logPane = new JTextArea(10,40);
		logPane.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logPane);
		logScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pLog.add(logScroll);
		
		super.pack();
	}
	
	private boolean connect() {
		//TODO AGWPE connect
		
		// if successful
		connect.setEnabled(false);
		disconnect.setEnabled(true);
		host.setEnabled(false);
		port.setEnabled(false);
		txCall.setEnabled(false);
		return true;
	}
	
	private void disconnect() {
		//TODO AGWPE disconnect
		
		disconnect.setEnabled(false);
		connect.setEnabled(true);
		host.setEnabled(true);
		port.setEnabled(true);
		txCall.setEnabled(true);
	}

}
