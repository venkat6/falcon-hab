package AGW_Sockets;

/*******************************************************************************************
    JavaAGW.java
	A Swing based tester for the AGW Packet Transport
	authors: PKWooster, Oct 2003 GPL license
	         Michael J Pawlowsky, March 2004
	        
	Part of the R/C Pilot Project
	http://rcpilot.sourceforge.net/
	
	
*/

import Falcon.FalconMainView;
import Falcon.PacketExchange;
import javax.swing.*;
import java.awt.event.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;


// client class that acts as a simple terminal
public class JavaAGW extends JFrame implements PacketUser
{

	private static int agw_monitor_status = 0;
	public static int debugLevel = 1;
        private boolean logFullDetail = false;

	// teminal stuff
	Controller controller;
	PacketTransport remote;			// NIO support
	String address="hw2362-14.aere.iastate.edu";			// default host new thor
	int port=8000;
	int sends = 1;
	public boolean readOn = true;
	int state = Packet.CLOSED;
	boolean running = false;
	Charset charset;
	CharsetEncoder encoder;
	CharsetDecoder decoder;
	StringBuffer recvBuffer;

        //components to register
        JTextArea logTextArea_;
        JLabel statusText_;
        JMenuItem agwpeMonitorMenuItem_;
        JMenuItem agwpeConnectMenuItem_;

	public JavaAGW(FalconMainView parent)
	{

    	controller = new Controller();	// runs the NIO select
		controller.start(true);			// start NIO select as a separate thread
		running = true;
		charset = Charset.forName("ISO-8859-1");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
		recvBuffer = new StringBuffer();
		
		// enter on userText causes transmit
//		userText.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent evt){userTyped(evt);}
//		});

		//setStatus("Closed");
		

		// put some documentaion in log window
		//toLog("!! use start menu to start");
		//toLog("## host is "+address+":"+port);
		
	}



    // user pressed enter in the user text field, we try to send the text
	void userTyped(ActionEvent evt)
	{
//		String txt = evt.getActionCommand();
//		userText.setText("");	// don't send it twice
//		toLog(txt, true);
//		System.out.println("From user:"+txt);
//
//		// put the text on the remote transmit queue
//		if(state == Packet.OPENED)
//		for(int i = 0; i<sends; i++)sendText(txt);
	}

	// methods to put text in logging window, toLog(text,true) if it came from user
	void toLog(String txt){toLog (txt,false);}
	void toLog(String txt, boolean user)
	{
		//log.append((user?"> ":"< ")+txt+"\n");
//		log.append(txt+"\n");
//		log.setCaretPosition(log.getDocument().getLength() ); // force last line visible
            if(logFullDetail){
                logTextArea_.append(txt + "\n");
                logTextArea_.setCaretPosition(logTextArea_.getDocument().getLength());
            }
	}

        public void setLogFullDetail(boolean b){
            logFullDetail = b;
        }

        public boolean isLogFullDetail(){
            return logFullDetail;
        }


        public void registerLogWindow(JTextArea logText){
            logTextArea_ = logText;
        }

        public void registerStatusField(JLabel statusText){
            statusText_ = statusText;
        }

        public void registerAGWPEMonitorMenuItem(JMenuItem item){
            agwpeMonitorMenuItem_ = item;
        }

        public void registerAGWPEConnectMenuItem(JMenuItem item){
            agwpeConnectMenuItem_ = item;
        }

	// start and stop menu
	public void mnuStart()
	{
		switch(state)
		{
			case Packet.CLOSED:
				remote = new PacketTransport(controller);
				if(remote.connect(this,address,port))setSockState (Packet.OPENING);
				else remote = null;
				break;
			case Packet.OPENED:
			case Packet.OPENING:
			case Packet.CLOSING:
				if(remote != null)remote.disconnect();	// shut it down
				break;
		}
	}

	// prompt user for host in form address:port
	// default is 127.0.0.1:8000
	public void mnuHost()
	{
		String txt = JOptionPane.showInputDialog("Enter host address and port");
		if (txt == null)return;

		int n = txt.indexOf(':');
		if(n == 0)
		{
			address = "127.0.0.1";
			port = Functions.toInt(txt.substring(1),8000);
		}
		else if(n < 0)
		{
			address = txt;
			port = 8000;
		}
		else
		{
			address = txt.substring(0,n);
			port = Functions.toInt(txt.substring(n+1),8000);
		}
			toLog("!! host set to "+address+":"+port);
	}
	
	
	
	public void getAGWVersion(){
		toLog("send version request");
		Packet pkt = new Packet(this, Packet.SEND, null, null); // get an empty send packet
		pkt.setDataKind((int)'R');	// set the type
		pkt.setPort(0);
		pkt.setCallTo(null);
		pkt.setCallFrom(null);
		try {
			remote.send(pkt);
		} catch(Exception e){
			toLog(e.toString());
			toLog("Did you connect?");
		}

	}
	
	
	public void monitorAGW(){
		toLog("send monitor request");
		Packet pkt = new Packet(this, Packet.SEND, null, null); // get an empty send packet
		pkt.setDataKind((int)'m');	// set the type
		pkt.setPort(0);
		pkt.setCallTo(null);
		pkt.setCallFrom(null);
		try {
			remote.send(pkt);
			if (agw_monitor_status == 0){
				agw_monitor_status = 1;
				agwpeMonitorMenuItem_.setText("Off AGWPE Monitor");
			} else {
				agwpeMonitorMenuItem_.setText("On AGWPE Monitor");
				agw_monitor_status = 0;
			}
		}catch(Exception e){
			toLog(e.toString());
			toLog("Did you connect?");
		}
	}
	
	
	public void mnuDisc()
	{
		if(state == Packet.OPENED)
		{
			remote.disconnect();    // request disconnect
			setSockState (Packet.CLOSING);
		}
	}


	public void mnuDebug()
	{
		String txt = JOptionPane.showInputDialog("Enter debug level");
		if (txt == null)return;
		Functions.setDebugLevel(Functions.toInt(txt,0));
	}

//	void mnuAbout(){new AboutDialog(this).setVisible(true);}



	private void sendText(String txt)
	{
		toLog("send requested");
		txt += "\n...";
		ByteBuffer buf = encode(txt);	// encode chars as bytes
		System.out.println("sendText buffer position="+buf.position()+" limit="+buf.limit());	
		Packet pkt = new  Packet(this,Packet.SEND,null,buf);
		remote.send(pkt);
	}

	// encode a string using our character encoder
	private ByteBuffer encode(String str)
	{
		ByteBuffer buffer=null;
		CharBuffer cb = CharBuffer.wrap(str);
		System.out.println("encode str len="+str.length()+" cb position="+cb.position()+" limit="+cb.limit());
		try{buffer = encoder.encode(cb);}
		catch(Exception e){e.printStackTrace();}
		System.out.println("encoded buffer position="+buffer.position()+" limit="+buffer.limit());
		return buffer;
	}
	
	
	private void receive(Packet pkt)
	{
		
            toLog("received packet data length=" + pkt.getDataLength());

            ByteBuffer hbuffer = pkt.getHeader();
            //Functions.println("header position=" + hbuffer.position() + " limit=" + hbuffer.limit() + " remaining=" + hbuffer.remaining());

            ByteBuffer buffer = pkt.getData();
            //Functions.println("received position=" + buffer.position() + " limit=" + buffer.limit() + " remaining=" + buffer.remaining());

            toLog("Header", true);
            int hlen = hbuffer.limit();
            byte[] head = new byte[hlen];
            hbuffer.get(head, 0, hlen);
            //toLogHex(head,0,hlen);
            String hdrStr = Functions.atochar(head, 0, hlen);
            toLog(hdrStr, true);

            toLog("Data", true);
            int dlen = buffer.limit();
            byte[] dat = new byte[dlen];
            buffer.get(dat, 0, dlen);
            toLogHex(dat,0,dlen);
            String dataStr = Functions.atochar(dat, 0, dlen);
            toLog(dataStr, true);

            PacketExchange.getInstance().packetReceived(hdrStr,dataStr);


    }
	
///**
// * print the hexadecimal and character representation of a byte array
// * @param ba the byte array
// * @param ofs the offset to start at
// * @param len the length to print
// */
//	public void toLogHex(byte[]ba, int ofs, int len)
//	{
//		int dlen;
//		String dstr;
//
//		while(len>0)
//		{
//			if(len < 80)
//			{
////				dlen = len;
////				char[] c48 = new char[48];
////				Arrays.fill(c48, ' ');
////				String fill48 = String.valueOf(c48);
////				dstr = fill48.substring(0,3*(16-dlen));
//				dlen = len;
//				char[] c240 = new char[240];
//				Arrays.fill(c240, ' ');
//				String fill240 = String.valueOf(c240);
//				dstr = fill240.substring(0,3*(80-dlen));
//			}
//			else
//			{
//				dlen = 80;
//				dstr = "";
//			}
//			//dstr = Functions.tohex4(ofs)+": "+Functions.atohex(ba,ofs,dlen)+dstr+": "+Functions.atochar(ba,ofs,dlen);
//            dstr = Functions.atochar(ba,ofs,dlen);
//			toLog(dstr, true);
//			ofs+=dlen;
//			len-=dlen;
//		}
//
//	}

        /**
 * print the hexadecimal and character representation of a byte array
 * @param ba the byte array
 * @param ofs the offset to start at
 * @param len the length to print
 */
	public void toLogHex(byte[]ba, int ofs, int len)
	{
		int dlen;
		String dstr;

		while(len>0)
		{
			if(len < 16)
			{
				dlen = len;
				char[] c48 = new char[48];
				Arrays.fill(c48, ' ');
				String fill48 = String.valueOf(c48);
				dstr = fill48.substring(0,3*(16-dlen));
			}
			else
			{
				dlen = 16;
				dstr = "";
			}
			dstr = Functions.tohex4(ofs)+": "+Functions.atohex(ba,ofs,dlen)+dstr+": "+Functions.atochar(ba,ofs,dlen);
			toLog(dstr, true);
			ofs+=dlen;
			len-=dlen;
		}

	}	
	
	
	private void setSockState (int s)
	{
		if(state != s)
		{
			state = s;
			switch(state)
			{
				case Packet.OPENED:
					agwpeConnectMenuItem_.setText("Disconnect");
					setStatus("Connected to "+address);
					break;
				case Packet.CLOSED:
					agwpeConnectMenuItem_.setText("Connect");
					agwpeMonitorMenuItem_.setText("On AGWPE Monitor");
					agw_monitor_status = 0;
					setStatus("Disconnected");
					remote = null;
					if(!running)System.exit(0);
					break;
				case Packet.OPENING:
					setStatus("Connecting to "+address);
					agwpeConnectMenuItem_.setText("Abort");
					break;

				case Packet.CLOSING:
					setStatus("Disconnecting from "+address);
					agwpeConnectMenuItem_.setText("Abort");
					break;
			}
		}
	}

	void setStatus(String st)
	{
		statusText_.setText(st);
	}

	// called when the run method in Packet is executed in the AWT event dispatch thread
	// looks a bit like an action event
	public void runPacket(Packet pkt)
	{
		int type = pkt.getType();
		
		switch(type)
		{
			case Packet.STATE:
				int s = ((Integer)pkt.getArg()).intValue();
				setSockState(s);
				break;

			case Packet.RECEIVE:
				receive(pkt);  
				break;
				
			case Packet.FLOW:
				boolean b = ((Boolean)pkt.getArg()).booleanValue();
				toLog("flow="+b);
				break;
			default:
				toLog("unexpected packet type="+type);
				break;	
		}
	}

	// uses invokeLater to put this packet on the system event queue so Swing will run it
	public void postPacket(Packet pkt)
	{
		SwingUtilities.invokeLater(pkt);	// the packet implements Runnable, so we can use invokeLater
	}

	// start up
//	static public void main(String[] args)
//	{
//		new JavaAGW().setVisible(true);
//	}
	

	

	//============================================================================================
	// inner classes

	//----------------------------------------------------------------------------
	// about dialog
//	class AboutDialog extends JDialog
//	{
//		Container contentPane;
//		JTextField text = new JTextField("Part of the R/C Pilot Project. http://rcpilot.sourceforge.net/");
//
//		AboutDialog(Frame f)
//		{
//			super(f,"About Client",true);
//			contentPane = getContentPane();
//			contentPane.add(text);
//			pack();
//		}
//	}
}