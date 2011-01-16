package gui.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

//HIGH Map Panel

public class OSMPanel extends JXMapKit {
	
	public OSMPanel(Dimension size) {
		// Setup superclass
		super();
		super.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		super.setPreferredSize(size);
		
		// Setup overlay painter
		super.getMainMap().setOverlayPainter(new Painter<JXMapViewer>() {
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
            		// Setup rendering parameters
            		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
            						   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            						   RenderingHints.VALUE_ANTIALIAS_ON);
            		
            		// Add source attribution
                    Font font = new Font("SanSerif", Font.PLAIN, 8);
                    FontMetrics metrics = getFontMetrics(font);
                    g.setPaint(Color.black);
                    String s = "Map by OpenStreetMap";
                    Rectangle2D bounds = metrics.getStringBounds(s, g);
                    g.drawString(s, w-(int)bounds.getWidth()-45, (int)(bounds.getHeight()));
            }
		});
	}

}
