/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

import java.util.LinkedList;

/**
 *
 * @author jcoleman
 *
 * For static functions only
 */
public class Functions {

    private Functions(){

    }

    public static LinkedList<Double[]> generateWindsFromGPS(LinkedList<Position> posList, boolean forAscent){
        LinkedList<Double[]> dblList = new LinkedList<Double[]>();
        double maxAlt = 0;
        int index = 0;
        int maxAltIndex = 0;
        for (Position p : posList) {
            if (p.getAlt() > maxAlt) {
                maxAlt = p.getAlt();
                maxAltIndex = index;
                break;
            }
            index++;
        }
        
        if(forAscent){
            for(int i = 0; i < maxAltIndex; i++) {
                if (i < posList.size() - 1) {
                    //we have at least one more Position in the list
                    Double[] dArr = new Double[3];
                    dArr[0] = posList.get(i).getAlt(); //use altitude of cur pos
                    dArr[1] = calcVelocity(posList.get(i), posList.get(i+1));
                    dArr[2] = AzelCalc.calc(posList.get(i), posList.get(i + 1))[0]; //azimuth
                    dblList.add(dArr);
                }
            }
        }else{
            for(int i = maxAltIndex; i < posList.size(); i++) {
                if (i < posList.size() - 1) {
                    //we have at least one more Position in the list
                    Double[] dArr = new Double[3];
                    dArr[0] = posList.get(i).getAlt();
                    dArr[1] = calcVelocity(posList.get(i), posList.get(i + 1));
                    dArr[2] = AzelCalc.calc(posList.get(i), posList.get(i + 1))[0]; //azimuth
                    dblList.add(dArr);
                }
            }
        }



        Double[] dArr = new Double[3];
        dArr[0] = 0.0;
        dArr[1] = dblList.get(0)[1]; //get 2nd column of first row - the first velocity
        dArr[2] = dblList.get(0)[2]; //get 3rd column of the first row - the first heading
        dblList.addFirst(dArr);
        dArr = new Double[3];
        dArr[0] = 50000.0;
        dArr[1] = dblList.getLast()[1]; //get 2nd column of first row - the first velocity
        dArr[2] = dblList.getLast()[2]; //get 3rd column of the first row - the first heading
        dblList.addLast(dArr);

        for(Double[] d : dblList){
            d[2] = d[2] + 180.0;
            if(d[2]>360) d[2] = d[2] - 360;
            //System.out.println(d[0] + "," + d[1] + "," + d[2]);
        }

        return dblList;
    }

    public static double calcVelocity(Position pStart, Position pStop){
        Double[] enuDbl = geodToENU(
                new Double[]{pStart.getLat(), pStart.getLong(), pStart.getAlt()},
                new Double[]{pStop.getLat(), pStop.getLong(), pStop.getAlt()}
        );
        Double E = enuDbl[0];
        Double N = enuDbl[1];
        Double U = enuDbl[2];
        //distances in enuDbl are in meters

        //calculate E,N (2D) distance traveled between the two points
        Double dist = Math.sqrt(E*E + N*N);
        Double heading = Math.atan2(E, N)*180/Math.PI;
        //shifting so 0 degrees is N, and the angles increase in the clockwise direction
        System.out.println("calcVelocity Heading Info: " + heading);
        System.out.println("E: " + E + ",N: " + N + ",U: " + U);

        //calculate the time difference in seconds
        Double timeDiff = (pStop.getTimestamp().getTime() - pStart.getTimestamp().getTime())/1000.0;
        
        //calculate the 2D velocity in (m/s)
        Double vel = dist/timeDiff;

        return vel;
    }


    public static Double[] geodToENU(Double[] baseP, Double[] targetP){
        Double[] ecefBaseDbl = geodToECEF(baseP);
        Double[] ecefTargetDbl = geodToECEF(targetP);
        return ecefToENU(ecefBaseDbl, ecefTargetDbl);
    }

    public static Double[] geodToECEF(Double[] geodPos) {
        double pi = Math.PI;
        double lat = geodPos[0] * pi / 180;	//Position latitude (rad)
        double lon = geodPos[1] * pi / 180;	//Position longitude (rad)
        double h = geodPos[2];		//Position height above geoid (m)
        
        //Define local variables
        double a = 6378137;		//Earth geoid semi-major axis (m)
        double f = 1/298.257223563;     //reciprocal flattening
        double e2 = 2*f-(f*f);          //eccentricity squared

        double chi = Math.sqrt(1-e2*Math.sin(lat)*Math.sin(lat));

        double X = (a/chi + h) * Math.cos(lat) * Math.cos(lon);
        double Y = (a/chi + h) * Math.cos(lat) * Math.sin(lon);
        double Z = (a*(1 - e2)/chi) * Math.sin(lat);

        return new Double[]{X, Y, Z};
    }

    public static Double[] ecefToENU(Double[] ecefBaseP, Double[] ecefTargetP) {
        //convert from ECEF coordinates to ENU
        //see http://en.wikipedia.org/wiki/Geodetic_system#From_geodetic_to_ECEF_coordinates
        //for reference
        Double Xr = ecefBaseP[0];
        Double Yr = ecefBaseP[1];
        Double Zr = ecefBaseP[2];
        Double X = ecefTargetP[0];
        Double Y = ecefTargetP[1];
        Double Z = ecefTargetP[2];

        Double phiP = Math.atan2(Zr, Math.sqrt(Xr * Xr + Yr * Yr));
        Double lambda = Math.atan2(Yr, Xr);

        Double e = -Math.sin(lambda) * (X - Xr) + Math.cos(lambda) * (Y - Yr);
        Double n = -Math.sin(phiP) * Math.cos(lambda) * (X - Xr) - Math.sin(phiP) * Math.sin(lambda) * (Y - Yr) + Math.cos(phiP) * (Z - Zr);
        Double u = Math.cos(phiP) * Math.cos(lambda) * (X - Xr) + Math.cos(phiP) * Math.sin(lambda) * (Y - Yr) + Math.sin(phiP) * (Z - Zr);

        return new Double[]{e, n, u};
    }

    public static String dblToPrettyStr(Double d, int numPrecision){
        return ""+(Math.floor(d*10.0*(numPrecision+1))/(10.0*(numPrecision+1)));
    }

}
