/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

/**
 *
 * @author Joe
 */
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Lat and Long are Decimal Degrees
 * @author jcoleman
 *
 */
public class Position {
	Double lat_;	//dec degrees
	Double long_;	//dec degrees
					//E is +, W is -
	Double alt_;	//in meters above sealevel
	Timestamp ts_;	//Timestamp of this position measurement (optional)
	boolean initialized_ = false;

	public Position(){
		lat_ = 0.0;
		long_ = 0.0;
		alt_ = 0.0;
	}

        public Position(Position p){
            lat_ = p.lat_;
            long_ = p.long_;
            alt_ = p.alt_;
            ts_ = p.ts_;
            boolean initialized_ = p.initialized_;
        }
	public Position(Double pLat, Double pLong, Double pAlt, Timestamp ts){
		lat_ = pLat;
		long_ = pLong;
		alt_ = pAlt;
		ts_ = ts;
		initialized_ = true;
	}


	//decimal degrees and meters for altitude
	public Position(Double pLat, Double pLong, Double pAlt){
		lat_ = pLat;
		long_ = pLong;
		alt_ = pAlt;
                ts_ = new Timestamp(System.currentTimeMillis());
                initialized_ = true;
	}

	public Position(Double pLat, Double pLong){
		lat_ = pLat;
		long_ = pLong;
		alt_ = 0.0;
                ts_ = new Timestamp(System.currentTimeMillis());
		initialized_ = true;
	}

//	public Position(String sLat, String sLong, String sAlt){
//		//lat and long in decimal minutes
//		setPosition(sLat, sLong, sAlt);
//	}
//
//	public Position(String sLat, String sLong){
//		setPosition(sLat, sLong, "0");
//	}
//
//	public Position(String[] pos){
//		if(pos.length == 3){
//			setPosition(pos[0], pos[1], pos[2]);
//			initialized_ = true;
//		}else{
//			lat_ = 0.0;
//			long_ = 0.0;
//			alt_ = 0.0;
//		}
//	}


	/**
	 * parseLatLong(String sLat)
	 * @param sLat
	 * @return Integer parsed latitude
	 */
	Double parseLatLong(String s, boolean isLongitude) throws NumberFormatException{
		//DDMM.MMN
		//scratch above... the LAST char is an S or N or a number (assume N in that case)
		int longOffset = isLongitude ? 1 : 0;
		boolean negative = false;
		int len = s.length();
		if(len < 1){
			System.out.println("error parsing empty string");
			return 0.0;
		}

		if( Character.isDigit(s.charAt(len-1)) ){
			//there is no letter at the end
		}else if(s.charAt(len-1)=='S' || s.charAt(len-1)=='W'){
			//there is an S letter at the end
			s = s.substring(0,len-1);
			negative = true;
		}else{
			//there is an E or N or an invalid letter at the end... trim it
			s = s.substring(0,len-1);
		}

		Double degrees = 0.0;
		Double minutes = 0.0;
		int pt = s.indexOf('.');
		len = s.length();

		if(pt == 0 && len == 1){
			System.out.println("only .");
			return 0.0;	//only a period
		}

		if(pt == -1 && len>2+longOffset){
			degrees = Double.parseDouble(s.substring(0, len-2));
			minutes = Double.parseDouble(s.substring(len-2, len));
		}else if(pt <= 2+longOffset && pt >= 0){
			//longitude can handle one more digit at the beginning for decimal degrees
			//for -1 there is not period... for decimal pts at 0, 1, 2 or 3(for long) it means it's already in decimal degrees
			degrees = Double.parseDouble(s);
			minutes = 0.0;

			//temporary radian conversion:
			if(degrees < 3 && degrees > -3){
				degrees = degrees *180/Math.PI;
			}
		}else {
			//it's not in decimal degrees it's in decimal minutes,
			degrees = Double.parseDouble(s.substring(0, pt-2));
			minutes = Double.parseDouble(s.substring(pt-2, len));
		}

		Double ret = degrees + minutes/60.0;
		if(negative) ret = -ret;
		return ret;
	}




	//return decimal degrees
	/**
	 * getReadableLat()
	 * returns the readable latitude
	 * TODO finish commeting when done with this method
	 * @return the latatude string
	 */
	public String getReadableLat(){
		Double dec_deg = getLat();
		int len = dec_deg.toString().length();
		int desiredLength = 8;
		if(len > desiredLength){
			return dec_deg.toString().substring(0,desiredLength);
		}else{
			String padding = "";
			for(int i=0; i<desiredLength-len; i++){
				padding = padding + "0";
			}
			return dec_deg.toString()+padding;
		}

	}

	/**
	 * getReadableLong()
	 * returns the readable longitude in string format
	 * @return the longitutde
	 */
	public String getReadableLong(){
		Double dec_deg = getLong();
		int len = dec_deg.toString().length();
		int desiredLength = 9;
		if(len > desiredLength){
			return dec_deg.toString().substring(0,desiredLength);
		}else{
			String padding = "";
			for(int i=0; i<desiredLength-len; i++){
				padding = padding + "0";
			}
			return dec_deg.toString()+padding;
		}
	}

	/**
	 * getReadableAlt()
	 * @return
	 */
	public String getReadableAlt(){
		Double alt = getAlt();
		int len = alt.toString().length();
		int desiredLength = 9;
		if(len > desiredLength){
			return alt.toString().substring(0,desiredLength);
		}else{
			String padding = "";
			for(int i=0; i<desiredLength-len; i++){
				padding = padding + "0";
			}
			return alt.toString()+padding;
                }
	}

	/**
	 * getLat()
	 * @return
	 */
	public Double getLat(){
		return lat_;
	}

	/**
	 * getLong()
	 * @return
	 */
	public Double getLong(){
		return long_;
	}

	/**
	 * getAlt()
	 * @return
	 */
	public Double getAlt(){
		return alt_;
	}

        public String getTimstampString(){
            return ts_.toString();
        }

        public Timestamp getTimestamp(){
            return ts_;
        }

	/**
	 * setPosition(String sLat, String sLong, String sAlt)
	 * @param sLat
	 * @param sLong
	 * @param sAlt
	 */
	private void setPosition(String sLat, String sLong, String sAlt){
		try{
			lat_ = parseLatLong(sLat, false);
			long_ = parseLatLong(sLong, true);
			alt_ = Double.parseDouble(sAlt);	//safer then parsing as an int
			if(lat_>0 && long_ > 0) initialized_ = true;
		}catch(NumberFormatException e){
			System.out.println("Position parsing error while parsing: " + sLat + " / " + sLong + " / " + sAlt + ". Using Zeroes." );
			lat_ = 0.0;
			long_ = 0.0;
			alt_ = 0.0;
		}
	}

	/**
	 * toString()
	 * returns string in format: lat,long,alt
	 * @return lat,long,alt
	 */
	public String toString(){
		return "(" + getReadableLat() + "," + getReadableLong() + "," + getReadableAlt() + ")";
	}

	public boolean isInitialized(){
		return initialized_;
	}


    public void setLat(double latP){
        lat_ = latP;
    }
    public void setLong(double longP){
        long_ = longP;
    }
    public void setAlt(Double altP){
        alt_ = altP;
    }
    public void setAlt(int altP){
        alt_ = (double)altP;
    }
}
