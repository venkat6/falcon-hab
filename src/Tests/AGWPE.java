package Tests;

import static org.junit.Assert.*;
import org.junit.Test;

import falcon.backend.agwpe.AGWPEConnection;
import falcon.backend.agwpe.PacketData;


public class AGWPE {

	@Test
	public void test_getVersion() {
		System.out.println("testing.");
		AGWPEConnection agw = new AGWPEConnection("129.186.192.124", 8000);
		agw.run();
		agw.start();
		agw.requestVersion();
		long time = System.currentTimeMillis();
		while(!agw.hasData()) {
			long diff = System.currentTimeMillis() - time;
			if(diff > 2000) break;
		}
		System.out.println("Out of loop!");
		PacketData pkt = agw.getData();
		System.out.println("Header: " + PacketData.toString(pkt.header));
		System.out.println("Body: " + PacketData.toString(pkt.data));
		assertNotNull(pkt);
		agw.setMonitor(true);
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while(agw.hasData()) {
			pkt = agw.getData();
			System.out.println("Header: " + PacketData.toString(pkt.header));
			System.out.println("Body: " + PacketData.toString(pkt.data));
		}
		agw.disconnect();
	}
	
}
