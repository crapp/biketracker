/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013, 2014 Christian Rapp <0x2a@posteo.org>
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.crappbytes.biketracker;

/**
 * Utility class
 * 
 * @author Christian Rapp aka crapp 
 *
 */

public class Utility {
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public static double convertSpeed(double speed) {
		speed = speed * 3600 / 1000;
		return speed;
	}
	
	public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
		//earth radius in km
		int earthRadius = 6371;
		
		//the haversine formula, very good for short distances. Problems with antipodal points
		double a = Math.pow((Math.toRadians(lat2) - Math.toRadians(lat1)) / 2, 2); //haversin
		double b = Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = Math.pow((Math.toRadians(lon2) - Math.toRadians(lon1)) / 2, 2); //haversin
		double d = 2 * earthRadius * Math.asin(Math.sqrt(a + b * c)); //bring it all together

		return d ; //distance in km
	}
	
	public static double toRad(double degree) {
		//TODO: Java is providing a toRadions function!
		double rad = 0;
		//conversation to radians = degree * PI/180
		rad = degree * (Math.PI/180);
		return rad;
	}
}
