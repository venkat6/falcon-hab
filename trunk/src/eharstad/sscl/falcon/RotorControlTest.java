package eharstad.sscl.falcon;

import java.util.Scanner;

public class RotorControlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			boolean go = true;
			Scanner input = new Scanner(System.in);
			while(go) {
				System.out.println("Enter desired azimuth:");
				String in = input.nextLine();
				Scanner parser = new Scanner(in);
				double az = parser.nextDouble();
				System.out.println("Enter desired elevation:");
				in = input.nextLine();
				parser = new Scanner(in);
				double el = parser.nextDouble();
				go = RotorControl.setAzEl(az, el);
			}
			System.exit(0);	
	}
}
