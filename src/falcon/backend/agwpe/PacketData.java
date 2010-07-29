package falcon.backend.agwpe;

import java.util.Arrays;

public class PacketData {
	public byte[] header;
	public byte[] data;
	
	public PacketData(byte[] h, byte[] d) {
		header = h;
		data = d;
	}
	
	public static String toString(byte[] dat) {
		int len = dat.length;
		int dlen;
		String str = null;
		
		while(len>0) {
			if(len < 16) {
				dlen = len;
				char[] c48 = new char[48];
				Arrays.fill(c48, ' ');
				String fill48 = String.valueOf(c48);
				str = fill48.substring(0,3*(16-dlen));
			} else {
				dlen = 16;
				str = "";
			}
			len-=dlen;
		}
		
		return str;
	}
}
