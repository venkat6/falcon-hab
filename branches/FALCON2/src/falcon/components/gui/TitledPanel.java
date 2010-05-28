package falcon.components.gui;

import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A basic panel class with a default text label.
 * @author Ethan Harstad
 *
 */
public class TitledPanel extends JPanel {
	
	/**
	 * Creates a panel with the standard layout manager and the given title.
	 * @param title
	 */
	public TitledPanel(String title) {
		super();
		setBorder(BorderFactory.createTitledBorder(title));
	}
	
	/**
	 * Creates a panel with the given title and layout manager.
	 * @param title
	 * @param layout
	 */
	public TitledPanel(String title, LayoutManager layout) {
		super(layout);
		setBorder(BorderFactory.createTitledBorder(title));
	}

}
