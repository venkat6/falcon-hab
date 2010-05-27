package falcon.components.gui;

import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class TitledPanel extends JPanel {
	
	public TitledPanel(String title) {
		super();
		setBorder(BorderFactory.createTitledBorder(title));
	}
	
	public TitledPanel(String title, LayoutManager layout) {
		super(layout);
		setBorder(BorderFactory.createTitledBorder(title));
	}

}
