package gui;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.infonode.docking.View;
import backend.Settings;

public class LogWindow extends View {

	//TODO LogWindow handle resize
	//TODO LogWindow add more tabs
	//FIXME LogWindow occasionally misses some updates
	
	PipedInputStream piOut;
	PipedInputStream piErr;
	PipedOutputStream poOut;
	PipedOutputStream poErr;
	
	JTextArea console = new JTextArea();
	
	public LogWindow() {
		super("Log", null, null);
		JTabbedPane root = new JTabbedPane();
		if(Settings.DEBUG) {
			JPanel consoleTab = new JPanel();
			
			try {
			piOut = new PipedInputStream();
	        poOut = new PipedOutputStream(piOut);
	        System.setOut(new PrintStream(poOut, true));
	        piErr = new PipedInputStream();
	        poErr = new PipedOutputStream(piErr);
	        System.setErr(new PrintStream(poErr, true));
			} catch(IOException e) {
				if(Settings.DEBUG) e.printStackTrace();
			}
			console.setEditable(false);
			console.setPreferredSize(new java.awt.Dimension(400, 200));
			consoleTab.add(new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
			
			new ReaderThread(piOut).start();
			new ReaderThread(piErr).start();
			
			root.addTab("Console", consoleTab);
		}
		setComponent(root);
	}
	
	class ReaderThread extends Thread {
		PipedInputStream pi;

        ReaderThread(PipedInputStream pi) {
            this.pi = pi;
        }

        public void run() {
            final byte[] buf = new byte[1024];
            try {
                while (true) {
                    final int len = pi.read(buf);
                    if (len == -1) {
                        break;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            console.append(new String(buf, 0, len));

                            // Make sure the last line is always visible
                            console.setCaretPosition(console.getDocument().getLength());

                            // Keep the text area down to a certain character size
                            int idealSize = 1000;
                            int maxExcess = 500;
                            int excess = console.getDocument().getLength() - idealSize;
                            if (excess >= maxExcess) {
                                console.replaceRange("", 0, excess);
                            }
                        }
                    });
                }
            } catch (IOException e) {
            }
        }
	}

}
