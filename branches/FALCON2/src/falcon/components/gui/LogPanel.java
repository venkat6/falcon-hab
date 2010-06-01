package falcon.components.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * A component that allows formatted logging of important events.  Orders events with newest events at the bottom.
 * Displays standard messages, warnings, alerts and system events differently.
 * @author Ethan Harstad
 *
 */
public class LogPanel extends JScrollPane {
	
	private JTextPane textPane = new JTextPane();
	
	// Create the different styles
	// Standard text used for regular messages
	private static SimpleAttributeSet PLAIN = new SimpleAttributeSet();
	// Green text used for events
	private static SimpleAttributeSet EVENT = new SimpleAttributeSet();
	// Yellow text used for warnings
	private static SimpleAttributeSet WARNING = new SimpleAttributeSet();
	// Red text used for alerts
	private static SimpleAttributeSet ALERT = new SimpleAttributeSet();
	// Blue text used for system debug messages
	private static SimpleAttributeSet DEBUG = new SimpleAttributeSet();
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
		StyleConstants.setFontFamily(DEBUG, "Monospaced");
		StyleConstants.setFontSize(DEBUG, 10);
		StyleConstants.setForeground(DEBUG, Color.BLUE);
		StyleConstants.setItalic(DEBUG, true);
	}
	
	/**
	 * Creates a LogPanel instance with the given preferred size.
	 * @param size
	 */
	public LogPanel(Dimension size) {
		super();
		setPreferredSize(size);
		setViewportView(textPane);
		textPane.setEditable(false);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * Add a standard message to the log
	 * @param message
	 */
	public void addMessage(String message) {
		addString(message);
	}
	
	/**
	 * Add an event to the log
	 * @param message
	 */
	public void addEvent(String message) {
		addString(message, EVENT);
	}
	
	/**
	 * Add a warning to the log
	 * @param message
	 */
	public void addWarning(String message) {
		addString(message, WARNING);
	}
	
	/**
	 * Add an alert to the log
	 * @param message
	 */
	public void addAlert(String message) {
		addString(message, ALERT);
	}
	
	/**
	 * Add a debug message to the log
	 * @param message
	 */
	public void addDebug(String message) {
		addString(message, DEBUG);
	}
	
	/**
	 * Default draw method, uses standard font.
	 * @param message
	 */
	private void addString(String message) {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			if(doc.getLength() > 0) message = "\n" + message;
			doc.insertString(doc.getLength(), message, PLAIN);
		} catch (BadLocationException e) {
			// Shouldn't happen
		}
	}
	
	/**
	 * Specific draw method, uses the given font.
	 * @param message
	 * @param a
	 */
	private void addString(String message, SimpleAttributeSet a) {
		StyledDocument doc = textPane.getStyledDocument();
		try {
			if(doc.getLength() > 0) message = "\n" + message;
			doc.insertString(doc.getLength(), message, a);
		} catch (BadLocationException e) {
			// Shouldn't happen
		}
	}

}
