/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author Joe
 */
public class Parser {
//    private static Parser parser_;
//    private static Position pos_;
//
//    private static FalconMainView fv_;

    private Parser(FalconMainView fv){ //not meant to be instantiated
//        fv_ = fv;
    }

//    public static Parser getInstance(){
//        return parser_;
//    }
//
//    public static boolean startParser(FalconView fv){
//        parser_ = new Parser(fv);
//        if(parser_!=null) return true;
//        return false;
//    }

//    public static void processString(String s){
//        //fv_.addToLog(s);
//    }
//
//    private Parser(){
//		pos_ = new Position();
//	}
//	/**
//	 * method used to get instance of the master data handler
//	 * @return instance of the Master Data Handler
//	 */
//	public static Parser getInstance(){
//		if(parser_ == null) parser_ = new Parser();
//		return parser_;
//	}
//
//    public Position getPos(){
//        return pos_;
//    }
//    public void setPos(Position p){
//        pos_ = p;
//    }

    public static char getHdrType(String s){
        System.out.println("HdrType:" + s.charAt(28));
        return s.charAt(28);
    }

    public static String base91toLatStr(String b91Lat){
        Double dLat = base91toLatDbl(b91Lat);
        String strLat = (Double.toString(Math.round(dLat*1000000)/1000000.0));
        return strLat;
    }

    public static Double base91toLatDbl(String b91Lat){
        if(b91Lat.length()==4){
            String s = b91Lat.substring(0,4);
            int[] iArr = new int[4];
            for(int i=0; i<4;i++){
                iArr[i] = s.charAt(i);
                //System.out.println(iArr[i]);
            }
            Double dLat = 90.0-((iArr[0]-33)*91*91*91+(iArr[1]-33)*91*91+(iArr[2]-33)*91+iArr[3]-33)/380926.0;
            return dLat;
        }
        return 0.0;
    }

    public static String base91toLonStr(String b91Lon){
        Double dLong = base91toLonDbl(b91Lon);
        String strLong = (Double.toString(Math.round(dLong*1000000)/1000000.0));
        System.out.println("Lon:" + strLong);
        return strLong;
    }
    public static Double base91toLonDbl(String b91Lon){
        if(b91Lon.length()==4){
            String s = b91Lon.substring(0,4);
            int[] iArr = new int[4];
            for(int i=0; i<4;i++){
                iArr[i] = s.charAt(i);
                //System.out.println(iArr[i]);
            }
            Double dLong = -180.0+((iArr[0]-33)*91*91*91+(iArr[1]-33)*91*91+(iArr[2]-33)*91+iArr[3]-33)/190463.0;
            return dLong;
        }
        return 0.0;
    }

    public static String getCallsign(String pktHdr){
        //char 9 until period of header
        String s = pktHdr.substring(8); //kill first 8 characters
        int i = s.indexOf('.');
        if(i>-1 && i<s.length()){
            s = s.substring(0,i);
        }
        System.out.println("Callsign identified:\"" + s + "\"");
        return s;

    }

    public static Double feetToMeters(Double dFeet){
        //Nation Institute of Standards conversion
        return dFeet*1200.0/3937.0;
    }

    public static Double decMinToDecDeg(Double decMin){
        //get decimal portion
        Double degreesPart = Math.floor(decMin/100);
        //get minute portion
        Double minutesPart = decMin-degreesPart*100;
        //convert minutes to degrees
        Double moreDegrees = minutesPart/60;
        //combine
        Double ret = degreesPart + moreDegrees;
//        System.out.println("decMin Conversion: " + decMin + " converted to: " + ret +
//                " steps: (" + degreesPart + "," + minutesPart + "," + moreDegrees + ")");
        return ret;
    }

    public static String[] getValidAprsIdentifiers(){
        return new String[]{"!","/","=","@"};
    }

    public static Position getGPSFromAPRS(String dataStr){
        //TODO: validate string lengths

        //APRS Identifiers:
        //'!' No APRS Messaging, w/o timestamp
        //'/' No APRS Messaging, w/ timestamp
        //'=' APRS Messaging, w/o timestamp
        //'@' APRS Messaging, w/ timestamp

        //'!' or '=' for Position Report with no Timestamp
        //'/' or '@' for Position Report with Timestamp

        //APRS String # characters in order (pg.32)
        //1: APRS Identifier Flag
        //7: (optional) Time DHM/HMS
        //8: Lat
        //1: SymTableID
        //9: Long
        //1: Symbol Code
        //0-43 Comment

        //APRS Mic-E pg.41
        //1: APRS Identifier Flag
        //7: (optional) Time DHM/HMS
        //1: SymTableID ('/')
        //4: Compressed Lat
        //4: Compressed Long
        //1: Symbol Code
        //2: Compressed (Course/Speed,Radio Range, or Altitude)
        //1: Comp Type
        //0-40: Comment

        //APRS lat and lon are in decimal minutes
        //Example lat: "4903.50N"
        //Example lon: "07201.75W"

        //APRS altitude in one of two ways
        //'/A=aaaaaa' in the comment field
        //Mic-E compressed in the comment field (not supported atm)
        //TODO: add Mic-E Altitude support

        Double dAlt = 0.0;
        Double dLat = 0.0;
        Double dLon = 0.0;

        char c = dataStr.charAt(0);
        if(c == '!' || c == '='){
            //with no timestamp
            //check for Mic-E SymTableID
            if(dataStr.charAt(1) == '/'){
                //Mic-E compressed version (w/o timestamp)
                dLat = base91toLatDbl(dataStr.substring(2,6));
                dLon = base91toLonDbl(dataStr.substring(6,10));
                dAlt = getAltFromComment(dataStr.substring(14));
            }else{
                //Uncompressed version (w/o timestamp)
                dLat = decMinToDecDeg(Double.parseDouble(dataStr.substring(1,8)));
                if(dataStr.charAt(8)=='S') dLat = -dLat;
                dLon = decMinToDecDeg(Double.parseDouble(dataStr.substring(10,18)));
                if(dataStr.charAt(18)=='W') dLon = -dLon;
                dAlt = getAltFromComment(dataStr.substring(20));
            }
        }else if(c == '/' || c == '@'){
            //with timestamp
            //check for Mic-E SymTableID
            //TODO: get timestamp
            if(dataStr.charAt(8) == '/'){
                //Mic-E compressed version (w/ timestamp)
                dLat = base91toLatDbl(dataStr.substring(9,13));
                dLon = base91toLonDbl(dataStr.substring(13,17));
                dAlt = getAltFromComment(dataStr.substring(21));
            }else{
                //Uncompressed version (w/ timestamp)
                dLat = decMinToDecDeg(Double.parseDouble(dataStr.substring(8,15)));
                if(dataStr.charAt(15)=='S') dLat = -dLat;
                dLon = decMinToDecDeg(Double.parseDouble(dataStr.substring(17,25)));
                if(dataStr.charAt(25)=='W') dLon = -dLon;
                dAlt = getAltFromComment(dataStr.substring(27));
            }
        }
        return new Position(dLat,dLon,dAlt, new Timestamp(Calendar.getInstance().getTime().getTime()));
    }

    private static Double getAltFromComment(String s){
        Double dAlt = 0.0;
        int i = s.indexOf("/A=");
        System.out.println("" + i);
        if (i > -1) {
            if (s.length() == i + 9) {
                //the altitude is at the end of the string, special case
                dAlt = feetToMeters(Double.parseDouble(s.substring(i + 3)));
            } else if (s.length() > i + 9) {
                dAlt = feetToMeters(Double.parseDouble(s.substring(i + 3, i + 9)));
            }
        }
        System.out.println(dAlt);
        return dAlt;
    }

    public static TelemetryData getTelemtryFromAPRS(String dataStr){
        //APRS Telemetry Data (pg.68) (data formats in appendix 1, pg. 94)
        //1: 'T'
        //5: Sequence No (starts with '#', ends with ',')
        //4: Analog Value 1 (ends with ',')
        //4: Analog Value 2 (ends with ',')
        //4: Analog Value 3 (ends with ',')
        //4: Analog Value 4 (ends with ',')
        //4: Analog Value 5 (ends with ',')
        //8: Digital Value (bbbbbbbb)

        if(dataStr.length()==34 && dataStr.charAt(0)=='T'){
            Integer iSeq = Integer.parseInt(dataStr.substring(2,5));
            Integer i1 = Integer.parseInt(dataStr.substring(6,9));
            Integer i2 = Integer.parseInt(dataStr.substring(10,13));
            Integer i3 = Integer.parseInt(dataStr.substring(14,17));
            Integer i4 = Integer.parseInt(dataStr.substring(18,21));
            Integer i5 = Integer.parseInt(dataStr.substring(22,25));
            boolean b1 = (dataStr.charAt(26)=='1');
            boolean b2 = (dataStr.charAt(27)=='1');
            boolean b3 = (dataStr.charAt(28)=='1');
            boolean b4 = (dataStr.charAt(29)=='1');
            boolean b5 = (dataStr.charAt(30)=='1');
            boolean b6 = (dataStr.charAt(31)=='1');
            boolean b7 = (dataStr.charAt(32)=='1');
            boolean b8 = (dataStr.charAt(33)=='1');

            return new TelemetryData(iSeq,new int[]{i1,i2,i3,i4,i5},new boolean[]{b1,b2,b3,b4,b5,b6,b7,b8});
        }
        return new TelemetryData(0,new int[5],new boolean[8]);
    }

    public static String getBase91(String pktData){
        String s = pktData;
        //int i = s.indexOf("./");
        int i = 9; //start of base91
        if(i>-1 && i<s.length()){
            s = s.substring(i+2,i+10);
        }
//        String[] sArr = s.split("/");
//        if(sArr.length > 2) s=sArr[2];
        System.out.println(s);
        return s;
      
    }

}
