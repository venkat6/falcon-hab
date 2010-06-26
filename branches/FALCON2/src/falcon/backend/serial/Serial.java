package falcon.backend.serial;
//TODO Test serial implementation

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Stack;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
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
	private Stack<byte[]> data;
	private int messages = 0;
	
	public Serial(String name, int baudRate) {
		connected = connect(name, baudRate);
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
					continue;
				}
			}
		}
		return set;
	}
	
	private boolean connect(String portName, int baudRate) {
		try {
			CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(portName);
			if(id.getPortType() == CommPortIdentifier.PORT_SERIAL) return false;
			CommPort port = id.open(this.getClass().getName(), 2000);
			com = (SerialPort)port;
			com.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
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
		com.removeEventListener();
	}

	@Override
	public void serialEvent(SerialPortEvent e) {
		int c;
		byte[] buffer = new byte[1024];
		int length = 0;
		try {
			while((c = in.read()) > -1) {
				if(c == '\n') {
					break;
				}
				buffer[length++] = (byte)c;
			}
		} catch (IOException ex) {
			System.err.println("Bad serial data!");
		}
		data.push(buffer);
	}
	
	public boolean dataAvailable() {
		if(messages > 0) {
			return true;
		}
		return false;
	}
	
	public byte[] getData() {
		byte[] dat = data.pop();
		messages--;
		return dat;
	}
	
	public boolean sendData(byte[] dat) {
		if(!connected || dat.length < 1) return false;
		for(int i = 0; i < dat.length; i++) {
			try {
				out.write(dat[i]);
			} catch (IOException e) {
				System.err.println("Could not send serial data");
				return false;
			}
		}
		return true;
	}

}
