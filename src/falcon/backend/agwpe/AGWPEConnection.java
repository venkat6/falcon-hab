package falcon.backend.agwpe;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Stack;

import javax.swing.SwingUtilities;

//TODO

public class AGWPEConnection implements PacketUser {

	private String address;
	private int port;
	private boolean configured = false;
	public Stack<PacketData> pkts;
	
	private boolean monitorState = false;
	Controller controller;
	PacketTransport remote;
	int state = Packet.CLOSED;
	boolean running = false;
	Charset charset;
	CharsetEncoder encoder;
	CharsetDecoder decoder;
	StringBuffer recvBuffer;
	
	public AGWPEConnection() {
		this("127.0.0.1", 8000);
	}
	
	public AGWPEConnection(String addr, int p) {
		address = addr;
		port = p;
		controller = new Controller();
		charset = Charset.forName("ISO-8859-1");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
		recvBuffer = new StringBuffer();
		pkts = new Stack<PacketData>();
		configured = true;
	}
	
	public void run() {
		if(!configured) return;
		controller.start(true);
		running = true;
	}
	
	public void start() {
		if(!running) return;
		switch(state) {
		case Packet.CLOSED:
			remote = new PacketTransport(controller);
			if(remote.connect(this,address,port)) {
				setSockState(Packet.OPENING);
			} else {
				remote = null;
			}
			break;
		case Packet.OPENED:
		case Packet.OPENING:
		case Packet.CLOSING:
			if(remote != null) {
				remote.disconnect();
				break;
			}
		}
	}
	
	public void disconnect() {
		if(remote != null) {
			remote.disconnect();
			running = false;
		}
	}
	
	private void setSockState(int s) {
		if(state != s) {
			state = s;
			switch(state) {
				// Handle stuff here....
			}
		}
	}
	
	public boolean setMonitor(boolean state) {
		if(!running) return false;
		if(state != monitorState) {
			Packet pkt = new Packet(this, Packet.SEND, null, null);	// get empty send packet
			pkt.setDataKind((int)'m');
			pkt.setPort(0);
			pkt.setCallTo(null);
			pkt.setCallFrom(null);
			try{
				remote.send(pkt);
				monitorState = !monitorState;
			} catch(Exception e) {
				System.err.println("Setting monitor state unsuccesful");
				return false;
			}
		}
		return true;
	}

	@Override
	public void postPacket(Packet pkt) {
		SwingUtilities.invokeLater(pkt);
	}

	@Override
	public void runPacket(Packet pkt) {
		int type = pkt.getType();
		switch(type) {
		case Packet.STATE:
			int s = ((Integer)pkt.getArg()).intValue();
			setSockState(s);
			break;
		case Packet.RECEIVE:
			receive(pkt);
			break;
		case Packet.FLOW:
			boolean b = ((Boolean)pkt.getArg()).booleanValue();
			break;
		default:
			System.err.println("Unexpected packet type=" + type);
			break;
		}	
	}
	
	private void receive(Packet pkt) {
		ByteBuffer hbuffer = pkt.getHeader();
		int len = hbuffer.limit();
		byte[] head = new byte[len];
		hbuffer.get(head,0,len);
		ByteBuffer buffer = pkt.getData();
		len = buffer.limit();
		byte[] dat = new byte[len];
		buffer.get(dat,0,len);
		pkts.push(new PacketData(head, dat));
	}
	
	private void sendText(String txt) {
		ByteBuffer buf = encode(txt);
		Packet pkt = new Packet(this,Packet.SEND,null,buf);
		remote.send(pkt);
	}
	
	private ByteBuffer encode(String str) {
		ByteBuffer buffer = null;
		CharBuffer cb = CharBuffer.wrap(str);
		try{
			buffer = encoder.encode(cb);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	public void requestVersion() {
		Packet pkt = new Packet(this, Packet.SEND, null, null);
		pkt.setDataKind((int)'R');
		pkt.setPort(0);
		pkt.setCallTo(null);
		pkt.setCallFrom(null);
		try {
			remote.send(pkt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasData() {
		return !pkts.isEmpty();
	}
	
	public PacketData getData() {
		if(pkts.isEmpty()) return null;
		return pkts.pop();
	}
	
}
