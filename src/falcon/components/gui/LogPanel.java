package falcon.components.gui;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LogPanel extends JScrollPane {
	
	private JTextPane textPane = new JTextPane();
	
	private static SimpleAttributeSet PLAIN = new SimpleAttributeSet();
	private static SimpleAttributeSet EVENT = new SimpleAttributeSet();
	private static SimpleAttributeSet WARNING = new SimpleAttributeSet();
	private static SimpleAttributeSet ALERT = new SimpleAttributeSet();
	
	static {
		StyleConstants.setFontFamily(PLAIN, "Monospaced");
		StyleConstants.setFontSize(PLAIN, 10);
		StyleConstants.setForeground(PLAIN, Color.BLACK);
		StyleConstants.setFontFamily(EVENT, "Monospaced");
		StyleConstants.setFontSize(EVENT, 10);
		StyleConstants.setForeground(EVENT, Color.GREEN);
		StyleConstants.setFontFamily(WARNING, "Monospaced");
		StyleConstants.setFontSize(WARNING, 10);
		StyleConstants.setForeground(WARNING, Color.YELLOW);
		StyleConstants.setFontFamily(ALERT, "Monospaced");
		StyleConstants.setFontSize(ALERT, 10);
		StyleConstants.setBold(ALERT, true);
		StyleConstants.setForeground(ALERT, Color.RED);
	}
	
	public LogPanel() {
		super();
		setViewportView(textPane);
		textPane.setEditable(false);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	public void addMessage(String message) {
		addString(message);
	}
	
	public void addEvent(String message) {
		addString(message, EVENT);
	}
	
	public void addWarning(String message) {
		addString(message, WARNING);
	}
	
	public void addAlert(String message) {
		addString(message, ALERT);
	}
	
	private void addString(String message) {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			if(doc.getLength() > 0) message = "\n" + message;
			doc.insertString(doc.getLength(), message, PLAIN);
		} catch (BadLocationException e) {
			//TODO do something with exception
		}
	}
	
	private void addString(String message, SimpleAttributeSet a) {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			if(doc.getLength() > 0) message = "\n" + message;
			doc.insertString(doc.getLength(), message, a);
		} catch (BadLocationException e) {
			//TODO do something with exception
		}
	}

}
