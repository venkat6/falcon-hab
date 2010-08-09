package falcon.components.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import com.sun.image.codec.jpeg.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import falcon.components.datatypes.Location;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

/**
 * Handles the drawing of a slippy type map using any provided images times.
 * Default is OpenStreetMap
 * Parent class by SwingLabs/jDekstop
 * 
 * @author Ethan Harstad
 */
public class MapPanel extends JXMapKit implements Printable {

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
	
	public void saveMapImage(File file) {
		BufferedImage img = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		paint(g);
		try{
			OutputStream out = new FileOutputStream(file);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(img);
			out.close();
		} catch(Exception e) {
			System.err.println("Could not save image!");
			System.err.println(e);
		}
	}
	
	public void printMap() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if(printJob.printDialog()) {
			try {
				printJob.print();
			} catch(PrinterException e) {
				System.err.println("Error printing! " + e);
			}
		}
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if(pageIndex > 0) return(NO_SUCH_PAGE);
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		setDoubleBuffered(false);
		paint(g2);
		setDoubleBuffered(true);
		return(PAGE_EXISTS);
	}
	
}
