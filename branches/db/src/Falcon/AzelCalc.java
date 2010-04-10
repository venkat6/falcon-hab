
package Falcon;

/**
 *
 * @author Joe
 */
public class AzelCalc {
    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static String[] calc(String baseP, String targetP){
        String[] baseStr = baseP.split(",", 3);
        String[] targetStr = targetP.split(",", 3);
        Double[] base = new Double[]{0.0, 0.0, 0.0};
        Double[] target = new Double[]{0.0, 0.0, 0.0};
        for (int i = 0; i < baseStr.length; i++) {
            if (isNumeric(baseStr[i])) {
                base[i] = Double.parseDouble(baseStr[i]);
            } else {
                System.out.println("!" + baseStr[i]);
            }
        }
        for (int i = 0; i < targetStr.length; i++) {
            if (isNumeric(targetStr[i])) {
                target[i] = Double.parseDouble(targetStr[i]);
            } else {
                System.out.println("!" + targetStr[i]);
            }
        }
        Double[] azel = calc(base,target);
        String[] ret = {Double.toString(Math.round(azel[0] * 100) / 100.0), Double.toString(Math.round(azel[1] * 100) / 100.0)};
        return ret;
    }

    /**
     *
     * @param baseP
     * @param targetP
     * @return Double[]{Azimuth,Elevation}
     */
    public static Double[] calc(Position baseP, Position targetP){
        return calc(
                new Double[]{baseP.getLat(),baseP.getLong(),baseP.getAlt()},
                new Double[]{targetP.getLat(),targetP.getLong(),targetP.getAlt()}
        );
    }


    public static Double[] calc(Double[] baseP, Double[] targetP){
        double pi = Math.PI;
        double latb = baseP[0] * pi / 180;					//Base latitude (rad)
//		System.out.println(latb);
        double lonb = baseP[1] * pi / 180;					//Base longitude (rad)
//		System.out.println(lonb);
        double hb = baseP[2];							//Base height above geoid (m)
//		System.out.println(hb);
        double latt = targetP[0] * pi / 180;					//Target latitude (rad)
//		System.out.println(latt);
        double lont = targetP[1] * pi / 180;					//Target lontitude (rad)
//		System.out.println(lont);
        double ht = targetP[2];						//Target height above geoid (m)
//        System.out.println(ht);

        //Define local variables
        double a = 6378137;							//Earth geoid semi-major axis (m)
        double e = 0.08181919;						//Earth geoid eccentricity

        //Calculate ECEF position vectors of tracking station and target
        double Nb = a / Math.sqrt(1 - e * e * Math.sin(latb) * Math.sin(latb));		//Prime vertical radius of curvature of the earth spheroid model at tracking station location
//		System.out.println(Nb);
        double Nt = a / Math.sqrt(1 - e * e * Math.sin(latt) * Math.sin(latt));		//Prime vertical radius of curvature of the earth spheroid model at target location
//        System.out.println(Nt);
        double Pb[] = {(Nb + hb) * Math.cos(latb) * Math.cos(lonb), //ECEF position vector of tracking station
            (Nb + hb) * Math.cos(latb) * Math.sin(lonb),
            (Nb * (1 - e * e) + hb) * Math.sin(latb)};
//        System.out.println(Double.toString(Pb[0]) + " " + Double.toString(Pb[1]) + " " + Double.toString(Pb[2]));

        double Pt[] = {(Nt + ht) * Math.cos(latt) * Math.cos(lont), //ECEF position vector of target
            (Nt + ht) * Math.cos(latt) * Math.sin(lont),
            (Nt * (1 - e * e) + ht) * Math.sin(latt)};
//        System.out.println(Double.toString(Pt[0]) + " " + Double.toString(Pt[1]) + " " + Double.toString(Pt[2]));

        //Calculate relative vector from tracking station to target expressed in ECEF frame
        double R[] = {0, 0, 0};
        for (int i = 0; i < 3; i++) {
            R[i] = Pt[i] - Pb[i];
        }
//        System.out.println(Double.toString(R[0]) + " " + Double.toString(R[1]) + " " + Double.toString(R[2]));

        //Transform relative position vector from ECEF to NED coordinates
        double sp = Math.sin(latb);
        double cp = Math.cos(latb);
        double sl = Math.sin(lonb);
        double cl = Math.cos(lonb);

        double Cne[] = {-sp * cl, -sp * sl, cp, //Rotation matrix from ECEF to NED frame
            -sl, cl, 0,
            -cp * cl, -cp * sl, -sp};
//        System.out.println(Double.toString(Cne[0]) + " " + Double.toString(Cne[1]) + " " + Double.toString(Cne[2]));
//        System.out.println(Double.toString(Cne[3]) + " " + Double.toString(Cne[4]) + " " + Double.toString(Cne[5]));
//        System.out.println(Double.toString(Cne[6]) + " " + Double.toString(Cne[7]) + " " + Double.toString(Cne[8]));

        double Rned[] = {0, 0, 0};
        //matrix multiplication
        for (int i = 0; i < 3; i++) {
            Rned[i] = Cne[3 * i] * R[0] + Cne[3 * i + 1] * R[1] + Cne[3 * i + 2] * R[2];			//Relative position vector expressed in NED frame
        }
//        System.out.println(Double.toString(Rned[0]) + " " + Double.toString(Rned[1]) + " " + Double.toString(Rned[2]));

        //Calculate output variables
        double LOS = 0;
        //find Euclidean length (norm) of Rned
        LOS = norm(Rned);

//        System.out.println(LOS);
        for (int i = 0; i < 3; i++) {
            Rned[i] = Rned[i] / LOS;							//Unit-vector in the direction of the relative position vector expressed in NED frame
        }


        Double El = -Math.asin(Rned[2]) * 180 / pi;				//Elevation angle
        Double Az = Math.atan2(Rned[1], Rned[0]) * 180 / pi;					//Azimuth angle
        
        return new Double[]{Az,El};
    }

    public static double norm(double[] d) {
        //find Euclidean length (norm)
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            sum += d[i] * d[i];
        }
        return Math.sqrt(sum);
    }

}
