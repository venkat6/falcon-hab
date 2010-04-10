/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

import java.util.LinkedList;

/**
 *
 * @author Joe
 */
public class PacketExchange {

    private static PacketExchange pe_;
    LinkedList<PacketListener> packetListenerList_;
    LinkedList<CallsignGPSListener> callsignListenerList_;
    LinkedList<String> callsignPairedList_; //paired with callsignListenerList_;


    private PacketExchange() {
        packetListenerList_ = new LinkedList<PacketListener>();
        callsignListenerList_ = new LinkedList<CallsignGPSListener>();
        callsignPairedList_ = new LinkedList<String>();
    }

    /**
     * method used to get instance of the master data handler
     * @return instance of the Master Data Handler
     */
    public static PacketExchange getInstance() {
        if (pe_ == null) {
            pe_ = new PacketExchange();
        }
        return pe_;
    }

    public void addPacketListener(PacketListener pl) {
        packetListenerList_.add(pl);
    }

    public void addCallsignListener(CallsignGPSListener cl, String callsign){
        callsignListenerList_.add(cl);
        callsignPairedList_.add(callsign);
    }

    public void removeCallsignListener(CallsignGPSListener cl){
        if(callsignListenerList_.contains(cl)){
            int i = callsignListenerList_.indexOf(cl);
            callsignListenerList_.remove(i);
            callsignPairedList_.remove(i);
        }
    }

    public void packetReceived(String hdrStr, String dataStr) {
        //see http://www.sv2agw.com/downloads/develop.zip, AGWPEAPI.HTM#_Toc500723812
        //for more information on parsing the AGWPE field for APRS info

        String callsign = Parser.getCallsign(hdrStr);
        String[] sLines = dataStr.split("\r");
        //second line is the one with the APRS info, use the first if only one exists
        String aprsStr = "";
        if(sLines.length>1){
            aprsStr = sLines[1];
        }else if(sLines.length == 1){
            aprsStr = sLines[0];
        }
        System.out.println("Picked APRS String:" + aprsStr);

        boolean validAprsId = false;
        for(String s:Parser.getValidAprsIdentifiers()){
            if(aprsStr.startsWith(s)) validAprsId = true;
        }

        int i = 0;
        for(String s:callsignPairedList_){
            if(s.equals(callsign) && validAprsId){
                //match found, notify associated listener
                callsignListenerList_.get(i).callsignGPSEvent(Parser.getGPSFromAPRS(aprsStr), aprsStr);
            }
            i++;
        }

//        char cType = Parser.getHdrType(hdrStr);

//        if (callsign.equals("KB0MGQ") && cType == '.') {
//            String b91str = Parser.getBase91(dataStr);
//            String cLat = Parser.base91toLat(b91str);
//            String cLong = Parser.base91toLong(b91str);
//            Parser.getInstance().setPos(new Position(cLat, cLong, "0"));
//        }
//        if (callsign.equals("KB0MGQ") && cType == 'f') {
//            //telemetry info
//        }
        for (PacketListener pl : packetListenerList_) {
            pl.packetReceivedEvent(hdrStr, dataStr);
        }

    }

    public void spoofIncomingPacket(String sHdr, String sData){
        packetReceived(sHdr,sData);
    }
}
