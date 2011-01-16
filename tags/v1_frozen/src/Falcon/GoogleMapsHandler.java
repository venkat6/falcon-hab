/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

import java.util.LinkedList;
import org.mozilla.browser.MozillaPanel;
import java.awt.Color;

/**
 *
 * @author Joe
 */
public class GoogleMapsHandler {

//    MozillaPanel moz_;
//
//    public GoogleMapsHandler(MozillaPanel moz){
//        moz_ = moz;
//    }

    public GoogleMapsHandler(){

    }

    public static void writeBalloonPath(MozillaPanel moz_, LinkedList<Position> posList, Color lineColor) {
        String jsCmd = "buildballoonline(new Array(";
        jsCmd = jsCmd + generatePathFromList(posList);
//        for(Position p:bPos){
//            jsCmd = jsCmd + "[" + p.getLat() + ", " + p.getLong() + "]";
//            if(!p.equals(bPos.getLast())) jsCmd = jsCmd + ",";
//        }
        jsCmd = jsCmd + "),\"#" + Integer.toHexString(lineColor.getRGB()).substring(2) + "\");";
        System.out.println(jsCmd);
        moz_.jsexec(jsCmd);

////        if (pList == null || pList.size() < 1 || jTagName.length() == 0) {
////            return;
////        }
//        String jTagName = "balloonline";
//        String color = "#151B8D";
//        moz_.jsexec("balloonline.show();");
//        //moz_.jsexec("map.removeOverlay(" + jTagName + ");"); //remove the old one if it's there
//        String s = "var " + jTagName + " = new GPolyline([" +
//                generatePathFromList(bPos) +
//                "], \"" + color + "\", 3, .75);\n";
//        moz_.jsexec(s);
//        moz_.jsexec("map.addOverlay(" + jTagName + ");");
//        moz_.jsexec("balloonline.show();");

    }

        public static void writePredictionPath(MozillaPanel moz_, LinkedList<Position> posList, Color lineColor) {
        String jsCmd = "buildpredline(new Array(";
        jsCmd = jsCmd + generatePathFromList(posList);
        jsCmd = jsCmd + "),\"#" + Integer.toHexString(lineColor.getRGB()).substring(2) + "\");";
        System.out.println(jsCmd);
        moz_.jsexec(jsCmd);
        }

        public static void writeRecoveryPath(MozillaPanel moz_, LinkedList<Position> posList, Color lineColor) {
        String jsCmd = "buildrecline(new Array(";
        jsCmd = jsCmd + generatePathFromList(posList);
        //discard first two hex values from string b'c they are for the alpha channel
        jsCmd = jsCmd + "),\"#" + Integer.toHexString(lineColor.getRGB()).substring(2) + "\");";
        System.out.println(jsCmd);
        moz_.jsexec(jsCmd);
        }

    private static String generatePathFromList(LinkedList<Position> posList) {
        //Limit points to 100
        String ret = "";
        if(posList.size() > 100){
            System.out.println("Number of path points " + posList.size() + " resized to less than 100.");
        }
        int step = (int)Math.ceil(posList.size()/100.0);
        System.out.println("Step size:" + step);
        for(int i = 0; i<posList.size(); i+=step){
            ret = ret + "[" + posList.get(i).getLat() + ", " + posList.get(i).getLong() + "]";
            if(i+step < posList.size()) ret = ret + ",";
        }
        return ret;


//        //trim the last line of the coordinate points (it doesn't have a comma)
//        if (sPos.length() > 2) {	//check to see if there was at least one val
//            sPos = sPos.substring(0, sPos.length() - 2) + "\n";
//        }
//        System.out.println(sPos);
//        return sPos;


    }

    public synchronized static void updateMarker(MozillaPanel browser_, Position p, String jTagName){
	//valid tags are: bCurr, burst, launch, recCurr, pland
        if(p==null || jTagName.length()==0) return;
		//with MozillaPanel, functions need to be used to set the javascript variables, don't use the following line
                //browser_.jsexec("var "+jTagName+"_lat = \""+p.getReadableLat()+"\";");
                browser_.jsexec("set" + jTagName + "("+p.getReadableLat()+","+p.getReadableLong()+","+p.getReadableAlt()+");");
		browser_.jsexec(jTagName + ".show();");
		
	}

    public static void testLine(MozillaPanel moz_){
        //String s = "predline = new GPolyline([new GLatLng(42.026908, -93.65324), new GLatLng(42.126908, -93.55324)], \"#151B8D\", 3, .6);\n map.addOverlay(predline);";
        //moz_.jsexec(s);
        //moz_.jsexec("buildpredline(new Array([42.026908, -93.65324],[42.126908, -93.55324]));");
        //moz_.jsexec("buildpredline();");
        //moz_.jsexec("showballoonlineval();");
    }
}
