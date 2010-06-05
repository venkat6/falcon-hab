package falcon.components.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import falcon.components.datatypes.Location;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

public class MapPanel extends JXMapKit {
	
	

	public MapPanel(Location center) {
		super();
		super.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		super.setCenterPosition(new GeoPosition(center.getLatitude(), center.getLongitude()));
		super.setZoom(5);
		
		// Create the map attribution text
		super.getMainMap().setOverlayPainter(new Painter<JXMapViewer>() {
			public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
				Font font = new Font("SanSerif", Font.PLAIN, 8);
				FontMetrics metrics = getFontMetrics(font);
				g.setPaint(Color.black);
				String s = "Map by OpenStreetMap";
				Rectangle2D bounds = metrics.getStringBounds(s, g);
				g.drawString(s, w-(int)bounds.getWidth()-45, (int)(bounds.getHeight()));
			}
		});
	}
	
	public void drawPath(LinkedList<Location> path) {
		//TODO MapPanel-drawPath
	}
	
	public void drawMarker(Location point, int icon) {
		//TODO MapPanel-drawMarker
	}
	
	public void drawMarker(Location point, Color color) {
		
	}
	
}
