package falcon.components.gui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A convenience class to make building GUI's easier.
 * Places a label above or next to the text field.
 * @author Ethan Harstad
 *
 */
public class LabeledField extends JPanel {
	
	private JTextField field;
	
	// Layout constants
	public static int STACKED = 0;	// Label above text field
	public static int INLINE = 1;	// Label to left of text field
	
	/**
	 * Creates a field with the given label and the given number of columns.
	 * @param name
	 * @param columns
	 * @param layout STACKED or INLINE
	 * @param editable
	 */
	public LabeledField(String name, int columns, int layout, boolean editable) {
		super();
		JLabel label = new JLabel(name);
		field = new JTextField(columns);
		field.setEditable(editable);
		if(layout == STACKED) {
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			field.setAlignmentX(Component.LEFT_ALIGNMENT);
		} else {
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		}
		add(label);
		add(field);
	}
	
	/**
	 * Creates a field with the given label, given initial text and given number of columns.
	 * @param name
	 * @param text
	 * @param columns
	 * @param layout
	 * @param editable
	 */
	public LabeledField(String name, String text, int columns, int layout, boolean editable) {
		this(name, columns, layout, editable);
		field.setText(text);
	}

	/**
	 * Sets the text of the JTextField.
	 * @param text
	 */
	public void setText(String text) {
		field.setText(text);
	}
	
	/**
	 * Returns the text of the JTextField.
	 * @return
	 */
	public String getText() {
		return field.getText();
	}
	
	/**
	 * Set whether the JTextField is editable.
	 * @param b
	 */
	public void setEditable(boolean b) {
		field.setEditable(b);
	}
	
	/**
	 * Sets whether the JTextField is enabled.
	 */
	@Override
	public void setEnabled(boolean b) {
		field.setEnabled(b);
	}

}
