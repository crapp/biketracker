/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013  Christian Rapp
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * An Android background service to log GPS position and write it to DB 
 * @author Christian Rapp crapp
 *
 */
public class GPSLoggerBackgroundService extends Service {

	private LocationManager locManager;
	private long minTimeMs = 10000; //get update every x seconds
	private long minDistance = 10; //Minimum distance between two points 
	private float minAccuracy = 35; //Minimum accuracy to store the gps position
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					this.minTimeMs, 
					this.minDistance,
					this.listener);
		}
		catch (Exception ex) {
			Log.e(getPackageName(), ex.getStackTrace().toString());
		}
	}
	
	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Write to DB and notify UI
			//http://stackoverflow.com/questions/2463175/how-to-have-android-service-communicate-with-activity
			if (location.getAccuracy() <= minAccuracy) {
				//hoooray we can use this one 
				String locData = "Location Data -- ";
				locData += "Altitude: " + String.valueOf(location.getAltitude());
				locData += "; Bearing: " +String.valueOf(location.getBearing());
				locData += "; Lat: " +String.valueOf(location.getLatitude());
				locData += "; Lon: " +String.valueOf(location.getLongitude());
				Toast.makeText(getBaseContext(), locData,
	                    Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
                    Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
                    Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Toast.makeText(getBaseContext(), "onStatusChanged: " + provider +
					", status " + String.valueOf(status),
                    Toast.LENGTH_SHORT).show();
		}
		
    };

	@Override
	public void onDestroy() {
		super.onDestroy();
		locManager.removeUpdates(this.listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
