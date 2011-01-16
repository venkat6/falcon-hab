/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author jcoleman
 */
public class Prediction {
    //Spacecraft information
    //Balloon mass in grams. Default: 1200
    double scBalloon_;
    //Weight of payload, parachute, and rigging in lb. Default: 5
    double scPayloadWt_;
    //Excess lift of payload in lb. Default: 4
    double scNetlift_;
    //Product of drag coefficient and area of parachute canopy in square meters. Default 1.3
    double scCdS_;
    
    //Launchsite information
    //Latitude of launch site in decimal degrees (DD.dddd)
    //Longitude of launch site in decimal degrees (DD.dddd), negative if West
    //Altitude of launch site in meters
    Position launchSitePos_;
    
    //string containing the name of the file containing wind data for subroutine WINDFILEPARSE
    String sWindfname_;
    
    //string containing the desired name of the kml file for plotting the trajectory in Google Earth
    String sKmlname_;
    
    //the desired altitude (in meters) for balloon cutwaway.  For flights to burst the value is 0.
    double cutdownalt_;

    boolean hasBurst_;
    Position startingPos_;
    Color lineColor_;
    //end inputs
    LinkedList<Double[]> winds;
    LinkedList<Position> pPos;
    int burstIndex_ = 0;
    //int landingIndex_ = 0;
    double burstalt = 0;
    double step_=5;					//integration step_ size (seconds)


    
    public Prediction(){
        //use defaults for inputs with blank constructor
        scBalloon_ = 1200;
        scPayloadWt_ = 5;
        scNetlift_ = 4;
        scCdS_ = 1.3;
        launchSitePos_ = new Position(42.0276,-93.6532,300.0);
        sWindfname_ = "winds.txt";
        sKmlname_ = "origPrediction.kml";
        cutdownalt_ = 0.0;
        hasBurst_ = false;
        startingPos_ = new Position(launchSitePos_);
        lineColor_ = Color.BLACK;

        winds = parseWindsFile(sWindfname_);
        updatePrediction();
    }


    public Prediction(
            Position launchSitePos,
            Position startPos,
            String windsFileName,
            String kmlFileName,
            Double balloonMass,
            Double payloadWt,
            Double netLift,
            Double dragCoef,
            Double cutdownAlt,
            boolean hasBurst,
            Color lineColor){
        scBalloon_ = balloonMass;
        scPayloadWt_ = payloadWt;
        scNetlift_ = netLift;
        scCdS_ = dragCoef;
        launchSitePos_ = launchSitePos;
        startingPos_ = startPos;
        sWindfname_ = windsFileName;
        sKmlname_ = kmlFileName;
        cutdownalt_ = cutdownAlt;   //use 0.0 for no cutdown
        hasBurst_ = hasBurst;
        lineColor_ = lineColor;

        winds = parseWindsFile(sWindfname_);
        updatePrediction();
    }

    public LinkedList<Position> getPrediction(){
        return pPos;
    }

    public Position getBurstPosition(){
        return pPos.get(burstIndex_);
    }

    public Position getLandingPosition(){
        return pPos.getLast();
    }

    public int getTimeToLandSec(){
        return (int)(pPos.size()*step_);
    }

    public int getTimeToBurstSec(){
        return (int)(burstIndex_*step_);
    }


    private void updatePrediction(){
        //calc burst altitude
        if(hasBurst_){
            //do nothing to burst alt
        }else if(cutdownalt_==0.0){
            burstalt = calcBurst()[0];
        }
        //calc & return burst (first in array is center value is pred burst)
        else{
            burstalt = cutdownalt_;
        }

        //run BFTP
        runBFTP();
        System.out.println("Flight Prediction Routine Complete");

        //create KML
        //KmlWriter.writeKml(sKmlname_, pPos, "HABET Prediction", lineColor_);
        //	kmlwrite(x,kmlname);

        //Display results
        //	screenoutput(burstalt,spacecraft,launchsite,x(length(x),:),flt_time,cutdownalt_)

    }
      
    /**
     * This subroutine parses a wind file from the Near Space Ventures website
     * at http://nearspaceventures.com/w3Baltrak/readyget.pl  To be read by this
     * routine, the file must be altered so that it contains only the columns of
     * data, no column headers.  The columns from the file are (for your
     * reference) pressure (hPa), altitude in MSL (m), temp (C), dew point (C),
     * wind direction (deg), wind speed (m/s).  
     * @param fname string containing the name of the file to be read
     * @return winds array with the following columns: altitude (m), wind speed (m/s), and wind direction (deg).
     */
    public static LinkedList<Double[]> parseWindsFile(String sWindfname){
        //the linkedlist is a table with columns: altitude, velocity, heading
        LinkedList<Double[]> dblList = new LinkedList<Double[]>();
        String sLine = "";
        FileReader fr;
        try{
            fr = new FileReader(sWindfname);
            BufferedReader br = new BufferedReader(fr);
            while(sLine!=null){
                sLine = br.readLine();
                //System.out.println(sLine);
                if(sLine!=null){
                    dblList.add(parseLine(sLine));
                }
            }
            fr.close();
        }catch(FileNotFoundException fnfe){
            System.out.println(fnfe + "/nWinds file not found. Creating new file.");
          
        }catch(IOException ioe){
            System.out.println(ioe);
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

    private static Double[] parseLine(String sLine){
        Double[] dArr = new Double[6];
        String[] sArr = sLine.split(" ");
        int i = 0;
        for(String s:sArr){
            //System.out.print(s);
            if(!s.isEmpty()){
                dArr[i] = Double.parseDouble(s);
                i++;
            }
        }
        Double[] dRet = new Double[3];
        dRet[0] = dArr[1];
        dRet[1] = dArr[5];
        dRet[2] = dArr[4];
        return dRet;
    }
    
    public double[] calcBurst(){
        double burstrad = 0;
        if(scBalloon_==3000){
            burstrad=13/2.0;
        }else if(scBalloon_==2000){
            burstrad=10.54/2;
        }else if(scBalloon_==1500){
            burstrad=9.44/2;
        }else if(scBalloon_==1200){
            burstrad=8.63/2;
        }else if(scBalloon_==600){
            burstrad=6.02/2;
        }else if(scBalloon_==350){
            burstrad=4.12/2;
        }else if(scBalloon_==300){
            burstrad=3.78/2;
        }else{
            burstrad=8.63/2;
            System.out.println("Data for selected balloon size not available.  1200gr data used.");
        }

        double[] P = new double[151];
        double[] T = new double[151];
        double[] rho = new double[151];
        double[] mu = new double[151];
        double[] h = new double[151];
        for(int i=0; i<=150; i++){
            double alt=1000*(i)*.3048;
            double[] stdat = stdat(alt);
            P[i] = stdat[0];
            T[i] = stdat[1];
            rho[i] = stdat[2];
            mu[i] = stdat[3];
            h[i] = i*1000.0;
        }

        double g=9.81;
        double Rair=287;
        double Rhe=2077;

        double lift=scPayloadWt_+scNetlift_+scBalloon_/454;
        double L0=lift/2.2*g;   //(lift in newtons)
        double L1=(lift-1)/2.2*g;      //high end of burst range
        double L2=(lift+1)/2.2*g;      //low end of burst range

        double V0=L0/(g*rho[0]*(1-Rair/Rhe));
        double mhe=rho[0]*Rair/Rhe*V0;
        double[] V = new double[151];
        double[] r = new double[151];
        for(int i=0;i<151;i++){
            V[i] = 1.0/(rho[i]*Rair/(mhe*Rhe));
            r[i]=Math.pow(3/(4*Math.PI)*V[i],1/3.0);
        }

        double V01=L1/(g*rho[0]*(1-Rair/Rhe));
        mhe=rho[0]*Rair/Rhe*V01;
        double[] V1 = new double[151];
        double[] r1 = new double[151];
        for(int i=0;i<151;i++){
            V1[i] = 1.0/(rho[i]*Rair/(mhe*Rhe));
            r1[i]=Math.pow(3/(4*Math.PI)*V1[i],1/3.0);
        }


        double V02=L2/(g*rho[0]*(1-Rair/Rhe));
        mhe=rho[0]*Rair/Rhe*V02;
        double[] V2 = new double[151];
        double[] r2 = new double[151];
        for(int i=0;i<151;i++){
            V2[i] = 1.0/(rho[i]*Rair/(mhe*Rhe));
            r2[i]=Math.pow(3/(4*Math.PI)*V2[i],1/3.0);
        }

        double burst =interp1(r, h,burstrad);
        double burst1=interp1(r1,h,burstrad);
        double burst2=interp1(r2,h,burstrad);
        //System.out.println("Bursts:" + burst + "," + burst1 + "," + burst2);

        double[] retArr = {0,0,0};
        retArr[0] = burst*0.3048;
        retArr[1] = burst1*0.3048;
        retArr[2] = burst2*0.3048;

        return retArr;
    }


    /**
     * This subroutine calculates the flight path of the balloon.  The key
     * assumptions of the calculation are that the balloon lift is constant
     * throughout the ascent portion of the flight, and that the horizontal
     * velocity of the balloon is exactly the same as the wind velocity.  The
     * wind speed and direction are interpolated linearly between data points in
     * the wind array.  Euler integration is used to numerically integrate the
     * equations of motion.
     *
     * Citations:
     *      1. Stevens, Brian L. and Lewis, Frank L., "Aircraft Control and Simulation,
     *         2nd Edition," John Wiley & Sons, Inc., Hoboken, New Jersy, 2003.
     *
     * @return Two element array with the following values:
     * array with the following columns: latitude (D.d), longitude (D.d), and altitude (m)
     * flt_time	duration of the flight in seconds
     */
    public void runBFTP(){
        //dependant on derivs and stdat
        //Joe: Runs from current REAL balloon position
        

        pPos = new LinkedList<Position>();
        Position pLast = new Position(startingPos_);
        double[] pStep = {0,0,0};
        pPos.add(new Position(pLast));
        //	pPos=[lat,lon,alt];

        int i=0;

        //Calculate ascent portion
        while (pLast.getAlt()<burstalt && !hasBurst_){
            //x(i,:)=x(i-1,:)+step_*derivs(x(i-1,:),winds,spacecraft,0);
            pStep = derivs(pLast,0);
            pStep[0] = pStep[0]*step_;
            pStep[1] = pStep[1]*step_;
            pStep[2] = pStep[2]*step_;
           //System.out.println("step_:" + pStep[0] + "," + pStep[1] + "," + pStep[2]);
            pLast.setLat(pLast.getLat() + pStep[0]);
            pLast.setLong(pLast.getLong() + pStep[1]);
            pLast.setAlt(pLast.getAlt() + pStep[2]);
            pPos.add(new Position(pLast));
            i=i+1;
        }

        burstIndex_=i;

        //Calculate descent portion
        //Descent from burst to launch site altitude
        while(pLast.getAlt()>launchSitePos_.getAlt()){
            //x(i,:)=x(i-1,:)+step_*derivs(x(i-1,:),winds,spacecraft,1);
            pStep = derivs(pLast,1);
            pStep[0] = pStep[0]*step_;
            pStep[1] = pStep[1]*step_;
            pStep[2] = pStep[2]*step_;
            pLast.setLat(pLast.getLat() + pStep[0]);
            pLast.setLong(pLast.getLong() + pStep[1]);
            pLast.setAlt(pLast.getAlt() + pStep[2]);
            pPos.add(new Position(pLast));
            i=i+1;
        }

        //landingIndex_=i;
//
//        double flt_time=i*step_;
//        return flt_time;
    }
    
    public void notes(){
        //	prompt  = {'Payload Weight (lb) (payload, parachute, rigging, etc)',...
        //				'Net Lift (lb)',...
        //				'Baloon Weight (gr)',...
        //				'Reference Drag Area Cd*S (m^2) (orange chute .8 white chute 1.3)',...
        //				'Launch Site Latitude (DD.dddd)',...
        //				'Launch Site Longitude (DD.dddd, negative if W)',...
        //				'Launch Site Altitude MSL (m)',...
        //				'Cutdown Altitude (m) (0 if fly to burst)',...
        //				'Wind File Name',...
        //				'KML File Name'};
    }
    
    /**
     * This subroutine calculates the 1976 U.S. Standard Atmosphere.  The
     * quantites calculated include the pressure, temperature, density, and
     * absolute dynamic viscosity.  The atmosphere model is valid to over 
     * 150,000 feet in altitude.  
     * @param h altitude in meters
     * @return Four element array with the following values: Pressure in Pa,
     * Temperature in K, Density in kg/m^3, absolute dynamic viscosity in N s 
     * m^-2 or Pa s
     */
    private double[] stdat(double h){
        double[] retArr = {0,0,0,0};
        double P = 0;
        double T = 0;
        double rho = 0;
        double mu = 0;
        
        //setup constants
        double T0=288.15;			//Temperature at base of each layer in deg K
        double T1=216.66;
        double T2=216.66;
        double T3=228.65;
        
        double p0=101325;			//Pressure at base of each layer in Pa
        double p1=22632.06;
        double p2=5474.89;
        double p3=868.02;
        
        double L0=-0.0065;			//Temerature Lapse Rate in deg K per meter
        double L1=0;
        double L2=.001;
        double L3=.0028;
        
        double R=287.05307;         //Gas constant
        double g=9.80665;			//Gravitational Acceleration
        
        if(h<=11000){
            T=T0+L0*h;
            P=p0*Math.pow(T/T0,-g/(R*L0));
            rho=P/(R*T);
            mu=1.458e-6*Math.sqrt(T)/(1+110.4/T);
        }
        else if(h<=20000){
            T=T1+L1*(h-11000);
            P=p1*Math.exp(g*(11000-h)/(R*T));
            rho=P/(R*T);
            mu=1.458e-6*Math.sqrt(T)/(1+110.4/T);
        }
        else if(h<32000){
            T=T2+L2*(h-20000);
            P=p2*Math.pow(T/T2,-g/(R*L2));
            rho=P/(R*T);
            mu=1.458e-6*Math.sqrt(T)/(1+110.4/T);
        }
        else if(h<47000){
            T=T3+L3*(h-32000);
            P=p3*Math.pow(T/T3,-g/(R*L3));
            rho=P/(R*T);
            mu=1.458e-6*Math.sqrt(T)/(1+110.4/T);
        }

        retArr[0] = P;
        retArr[1] = T;
        retArr[2] = rho;
        retArr[3] = mu;
        return retArr;
    }
    


    public double[] derivs(Position pLast, int mode){
        double[] retArr = {0,0,0};

        //Constants
        double a	= 6378137;  //Semi-major axis of Earth ellipsoid in meters
        double e	= 0.08181919;   //Eccentricity of Earth ellipsoid
        double g	= 9.81; //Gravitational acceleration (m/s^2)

        //Required Quantities
        double latr     = pLast.getLat()*Math.PI/180;
        double h        = pLast.getAlt();
        double M	= (a*(1-e*e)) / Math.pow(1-e*e*Math.sin(latr)*Math.sin(latr),3/2.0);  //Meridian radius of curvature
        double N	= (a) / Math.pow(1-e*e*Math.sin(latr)*Math.sin(latr),1/2.0);  //Prime vertical radius of curvature
        double W	= scPayloadWt_/2.2*g;   //weight of payload and parachute in newtons
        //[P,T,rho,mu]= stdat(h)
        double[] stdat = stdat(h);  //Standard Atmosphere
        double P = stdat[0];
        double T = stdat[1];
        double rho = stdat[2];
        double mu = stdat[3];
        //System.out.println("Stdat:" + P + "," + T + "," + rho + "," + mu);

        //Local Wind Speed (linearly interpolated)
        //double Vw	= interp1( winds(:,1), winds(:,2), h);
        //find index of alt just under currAlt
        //all three sub-arrays must be the same length
        //double Vw = interp1(winds[0],winds[1],h);
        double Vw = llInterp(winds,0,1,h);
        //System.out.println("Vw=" + Vw);

        //Psiw=(interp1( winds(:,1), winds(:,3), h));
        //double Psiw = interp1(winds[0],winds[2],h);
        double Psiw = llInterp(winds,0,2,h);
        //System.out.println("Psiw=" + Psiw);

        double VE	= Vw*Math.sin(Psiw*Math.PI/180);    //x component of wind (m/s)
        double VN	= Vw*Math.cos(Psiw*Math.PI/180);    //y component of wind (m/s)

        //EOM calculation

        if(mode==0){
            //Ascent Rate
            double AR	= 650*Math.sqrt(scNetlift_+scPayloadWt_)/Math.pow(scPayloadWt_+scBalloon_/454.0,1/3.0); //ft/min, all weights in lb
            AR	= AR*0.3048/60; //convert AR to m/s
            retArr[2] = AR;
        }
        else if(mode==1){
            //Descent Rate
            retArr[2] = -Math.pow( 2*W/(rho*scCdS_),1/2.0);
        }

        retArr[0]	= VN/(M+h)*180/Math.PI;
        retArr[1]	= VE/((N+h)*Math.cos(latr))*180/Math.PI;

        //System.out.println("Derivs Result: " + retArr[0] + "," + retArr[1] + "," + retArr[2]);
        return retArr;
    }

    /**
     * Needs input that is two ordered arrays of same length
     * @param pArrX
     * @param pArrY
     * @param pXi
     * @return
     */
    public double interp1(double[] pArrX, double[] pArrY, double pXi){
        if(pArrX.length<2) System.out.println("Error. Interpolation tables must have at least 2 rows");
        double ret = 0;
        if(pArrX.length!=pArrY.length){
            System.out.println("Error. Arrays not the same length");
            return ret;
        }
        int i0 = 0;
        for(double x : pArrX){
            if(x>pXi) break;
            i0++;
        }
        //check if the lookup value is not with 2 values (past last value of table)
        if(i0==pArrX.length){
            i0 = i0-2;
            System.out.println("above top of interp");
        }else if(i0==0){
            //do nothing;
        }else{
            i0 = i0-1;
        }
        double xLow = pArrX[i0];
        double xHigh = pArrX[i0+1];
        double yLow = pArrY[i0];
        double yHigh = pArrY[i0+1];
        ret = yLow + (pXi-xLow)*(yHigh-yLow)/(xHigh-xLow);
        //System.out.println("Index" + i0 + " Interp: " + xLow + "," + xHigh + "," + yLow + "," + yHigh);
        return ret;
    }

    private double llInterp(LinkedList<Double[]> dblList, int col1i, int col2i, double lookupVal){
        double ret = 0;
        int i0 = 0;
        for(Double[] dArr : dblList){
            //find the low value for interpolation
            if(dArr[col1i]>lookupVal) break;
            i0++;
        }
        //check if the lookup value is not with 2 values (past last value of table)
        if(i0==dblList.size()){
            i0 = i0-2;
            System.out.println("above top of interp");
        }else if(i0==0){
            //do nothing;
        }else{
            i0 = i0-1;
        }
        
        //(interpolation is done with last 2 points if lookupVal is above the highest value)
        
        double xLow = dblList.get(i0)[col1i];
        double xHigh = dblList.get(i0+1)[col1i];
        double yLow = dblList.get(i0)[col2i];
        double yHigh = dblList.get(i0+1)[col2i];
        ret = yLow + (lookupVal-xLow)*(yHigh-yLow)/(xHigh-xLow);
        //System.out.println("Index" + i0 + " Interp: " + xLow + "," + xHigh + "," + yLow + "," + yHigh);
        return ret;
    }


    public void screenoutput(){
        //function screenoutput(burstalt,spacecraft,launchsite,x,flt_time,cutdownalt_)
//	AR	= 650*sqrt(scNetlift_+scPayloadWt_)/(scPayloadWt_+scBalloon_/454)^(1/3);
//	flight_time=flt_time/60;
//	[P,T,rho50k,mu]=stdat(50000*.3048);
//	[P,T,rho30k,mu]=stdat(30000*.3048);
//	[P,T,rho10k,mu]=stdat(10000*.3048);
//	W	= scPayloadWt_/2.2*9.81;				%weight of payload and parachute in newtons
//	DR50k	= ( 2*W/(rho50k*spacecraft.CdS))^(1/2)/.3048*60;
//	DR30k	= ( 2*W/(rho30k*spacecraft.CdS))^(1/2)/.3048*60;
//	DR10k	= ( 2*W/(rho10k*spacecraft.CdS))^(1/2)/.3048*60;
//	burstalt=burstcalc(spacecraft);
//	burstalt=burstalt/.3048;
//	cutdownalt_=cutdownalt_/.3048;
//	flt_dist=distance(launchsite.lat,launchsite.lon,x(1),x(2));
//	flt_dist=deg2sm(flt_dist);
//
//	disp(' ');disp(' ');disp(' ');disp('           FLIGHT DATA SUMMARY')
//	h1=sprintf('Ascent Rate:  %7.2f ft/min',AR);disp(h1)
//	h2=sprintf('Descent Rate: %7.2f ft/min at 50,000 ft',DR50k);disp(h2);
//	h3=sprintf('              %7.2f ft/min at 30,000 ft',DR30k);disp(h3);
//	h4=sprintf('              %7.2f ft/min at 10,000 ft',DR10k);disp(h4);disp(' ')
//	h9=sprintf('Flight Time:  %5.2f minutes',flight_time);disp(h9)
//
//	if cutdownalt_==0
//		h5=sprintf('Nominal Burst Altitude %6.0f ft',burstalt(1));disp(h5);
//		h6=sprintf('Burst likely from %6.0f to %6.0f ft',burstalt(3),burstalt(2));disp(h6);
//	else
//		h5=sprintf('Cutdown Altitude %6.0f ft',cutdownalt_);disp(h5)
//		h6=sprintf('Burst Altitude would be from %6.0f to %6.0f ft',burstalt(3),burstalt(2));disp(h6);
//	end
//	disp(' ');
//	h7=sprintf('Landing Site: %7.4f N, %7.4f W',x(1),-x(2));disp(h7)
//	h8=sprintf('Landing site is %5.1f miles from Launch Site',flt_dist);disp(h8)
//return
    }

    public static void main(String args[]){
        Prediction p = new Prediction();
        
    }




}
