package falcon.backend.serial;
//TODO

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * A class to implement a serial connection for data sources such as
 * ZigBee's, Aerocomm's and other wireless serial modems. Utilizes the
 * RxTx library which is cross platform compatible.
 * 
 * @author Ethan Harstad
 *
 */
public class Serial implements SerialPortEventListener {
	
	private boolean connected = false;
	private SerialPort com;
	private InputStream in;
	private OutputStream out;
	
	public Serial(String name) {
		connected = connect(name);
	}
	
	public static HashSet<CommPortIdentifier> getAvailablePorts() {
		HashSet<CommPortIdentifier> set = new HashSet<CommPortIdentifier>();
		//TODO Type safety
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if(port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				try {
					CommPort temp = port.open("CommUtil", 50);
					temp.close();
					set.add(port);
				} catch(Exception e) {
					// Cannot access the port for some reason, do not use it
				}
			}
		}
		return set;
	}
	
	private boolean connect(String portName) {
		try {
			CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(portName);
			if(id.getPortType() == CommPortIdentifier.PORT_SERIAL) return false;
			CommPort port = id.open(this.getClass().getName(), 2000);
			com = (SerialPort)port;
			com.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			com.addEventListener(this);
			com.notifyOnDataAvailable(true);
			in = com.getInputStream();
			out = com.getOutputStream();
		} catch (Exception e) {
			// Could not use the port for some reason
			System.err.println("The port cannot be used");
			return false;
		}
		
		return true;
	}
	
	public void close() {
		//TODO
		//Remove the event listener to prevent hang on exit
	}

	@Override
	public void serialEvent(SerialPortEvent e) {
		// TODO Auto-generated method stub
		
	}

}
