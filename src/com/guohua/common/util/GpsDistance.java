package com.guohua.common.util;

public class GpsDistance {
	
	private static final double EARTH_RADIUS = 6378.137; 
    
	public static double geo_distance(double lat1, double lng1, double lat2,  
            double lng2) {  
        double r = EARTH_RADIUS;  
        lat1 = Math.toRadians(lat1);  
        lng1 = Math.toRadians(lng1);  
        lat2 = Math.toRadians(lat2);  
        lng2 = Math.toRadians(lng2);  
        double d1 = Math.abs(lat1 - lat2);  
        double d2 = Math.abs(lng1 - lng2);  
        double p = Math.pow(Math.sin(d1 / 2), 2) + Math.cos(lat1)  
                * Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2);  
        double dis = r * 2 * Math.asin(Math.sqrt(p));  
        return dis;  
    }  
    public static  double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
    	 
        double d = 0;
        double longi=lng_b-lng_a;
        d=Math.atan2(Math.sin(longi)*Math.cos(lat_b),Math.cos(lat_a)*Math.sin(lat_b)-Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(longi));
        return d;
  
     }
}
