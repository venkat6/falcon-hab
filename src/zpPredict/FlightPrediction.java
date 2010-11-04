package zpPredict;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FlightPrediction {
	
	private static JTextField startLat;
	private static JTextField startLon;
	private static JTextField startAlt;
	private static JTextField startTime;
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("Zero Pressure Prediction");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		panel.add(new JLabel("Starting Latitude:"));
		startLat = new JTextField(10);
		panel.add(startLat);
		panel.add(new JLabel("Starting Longitude:"));
		startLon = new JTextField(10);
		panel.add(startLon);
		panel.add(new JLabel("Starting Altitude:"));
		startAlt = new JTextField(10);
		panel.add(startAlt);
		panel.add(new JLabel("Starting Time:"));
		startTime = new JTextField(10);
		startTime.setText(Long.toString(System.currentTimeMillis()/1000));
		panel.add(startTime);
		JButton runButton = new JButton("Run");
		panel.add(runButton);
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runPrediction();
			}
		});
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void runPrediction() {
		Scanner parser = new Scanner(startLat.getText());
		double startLatitude = parser.nextDouble();
		parser = new Scanner(startLon.getText());
		double startLongitude = parser.nextDouble();
		parser = new Scanner(startAlt.getText());
		double startAltitude = parser.nextDouble();
		parser = new Scanner(startTime.getText());
		double startSeconds = parser.nextDouble();
		
		Spacecraft payload = new Spacecraft(Spacecraft.HELIUM, 500, 1.2, 2.8, 1.0, 15.0);
		payload.launch(startSeconds, startLatitude, startLongitude, startAltitude);
		
		FileOutputStream out = null;
		PrintStream s = null;
		try {
			out = new FileOutputStream("results.txt");
			s = new PrintStream(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		write(s, payload.getProperties());
		for(int i = 0; i < 1000; i++) {
			payload.simulate(10.0);
			write(s, payload.getProperties());
		}
		System.out.println("Done");
	}
	
	private static void write(PrintStream s, double[] values) {
		for(int i = 0; i < values.length; i++) {
			s.print(values[i] + " ");
		}
		s.print("\n\r");
	}

}
