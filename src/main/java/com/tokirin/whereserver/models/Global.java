package com.tokirin.whereserver.models;

public class Global {
	
	public static double calDistance(double lat1,double lng1, double lat2,double lng2){
		
		double EarthR=6371000.0;
		double R = Math.PI/180;
		double radLat1 = R*lat1;
		double radLat2 = R*lat2;
		double radDist = R*(lng1-lng2);
		
		double distance = Math.sin(radLat1) * Math.sin(radLat2);
		
		distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
		double ret = EarthR * Math.acos(distance);
		System.out.println(ret);
		return ret;
		
	}

}
