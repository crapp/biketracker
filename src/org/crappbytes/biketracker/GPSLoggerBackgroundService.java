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

import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * An Android background service to log GPS position and write it to DB 
 * @author Christian Rapp crapp
 *
 */
public class GPSLoggerBackgroundService extends Service {

	private LocationManager locManager;
	private NotificationManager notiManager;
	private LowPassFilter lpf;
	//TODO: make this values configurable!!
	private long minTimeMs = 1000; //get update every x seconds
	private long minDistance = 5; //Minimum distance between two points in meters?! Or the min distance loc manager will get an update.
	private float minAccuracy = 20; //Minimum accuracy to store the gps position
	private String trackName;
	private int trackID;
	private Location prevLoc;
	private long unixTime;
	private long raceTime;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			
			this.prevLoc = null;
			this.unixTime = 0;
			this.raceTime = 0;
			Cursor cursor = getContentResolver().query(TracksContentProvider.CONTENT_URI_NODES,
					null,
					TrackNodesTable.COLUMN_TRACKID + "=?" ,
					new String[] {String.valueOf(trackID)},
					TrackNodesTable.COLUMN_ID + " ASC");
			if (cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToLast();
				this.raceTime = cursor.getInt(cursor.getColumnIndex(TrackNodesTable.COLUMN_RACETIME));
			}
			
			this.lpf = null;
			
			locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					this.minTimeMs, 
					this.minDistance,
					this.listener);
			
			//get notification manager
			notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Prepare intent which is triggered if the
		    // notification is selected
			Intent intent = new Intent(this, TrackActivity.class);
			//set some flags to avoid starting a new instance of TrackActivity
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("org.crappbytes.TrackName", trackName);
			intent.putExtra("org.crappbytes.TrackID", trackID);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			//Compat because we said from Api Level 14 on
			Notification no = new NotificationCompat.Builder(this)
			.setContentTitle("BikeTracker tracking...")
			.setContentText("BikeTracker is tracking your movement")
			.setSmallIcon(R.drawable.ic_biketracker_noti)
			.setContentIntent(pIntent).build();
			no.flags |= Notification.FLAG_NO_CLEAR;
			notiManager.notify(0, no);
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

				double[] altiAscDesc = {location.getAltitude(), 0, 0};
				double distance = 0;

				if (lpf == null) {
					lpf = new LowPassFilter(location.getAltitude());
				}

				//calculate distance to previous node now and store in db
				if (prevLoc != null) {
					//compute distance to previous location
					distance = Utility.haversineDistance(prevLoc.getLatitude(),
							prevLoc.getLongitude(),
							location.getLatitude(),
							location.getLongitude());

					altiAscDesc = lpf.applyFilter(location.getAltitude());
                    String altiAsDes = "AltiAsDes: ";
                    for (double d : altiAscDesc) {
                        altiAsDes += String.valueOf(d) + ", ";
                    }
//                    Toast.makeText(getBaseContext(),
//                            altiAsDes,
//                            Toast.LENGTH_SHORT).show();
				}

				//calculate race time now and store in db
				if (unixTime != 0) {
					//get seconds since epoche
					long newUnixTime = System.currentTimeMillis() / 1000L;
					long timeDelta = newUnixTime - unixTime;
					//only store if
					if (timeDelta <= 10) {
						raceTime += timeDelta;
					}
					unixTime = newUnixTime;
				} else {
					unixTime = System.currentTimeMillis() / 1000L;
				}

				//save location as previous location.
				prevLoc = location;
				ContentValues cv = new ContentValues();
				cv.put(TrackNodesTable.COLUMN_TRACKID, trackID);
				cv.put(TrackNodesTable.COLUMN_ACCURACY, location.getAccuracy());
				cv.put(TrackNodesTable.COLUMN_ALTITUDE, location.getAltitude());
				cv.put(TrackNodesTable.COLUMN_ALTITUDELPF, altiAscDesc[0]);
				cv.put(TrackNodesTable.COLUMN_ALTITUDEUP, altiAscDesc[1]);
				cv.put(TrackNodesTable.COLUMN_ALTITUDEDOWN, altiAscDesc[2]);
				cv.put(TrackNodesTable.COLUMN_BEARING, location.getBearing());
				cv.put(TrackNodesTable.COLUMN_LATITUDE, location.getLatitude());
				cv.put(TrackNodesTable.COLUMN_LONGITUDE, location.getLongitude());
				cv.put(TrackNodesTable.COLUMN_SPEED, location.getSpeed());
				cv.put(TrackNodesTable.COLUMN_DISTANCE, distance);
				cv.put(TrackNodesTable.COLUMN_RACETIME, raceTime);
				Uri url = getContentResolver().insert(TracksContentProvider.CONTENT_URI_NODES, cv);
				if (url == null) {
					Toast.makeText(getBaseContext(),
							"Could not insert location into Database",
							Toast.LENGTH_LONG).show();
				}
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
		notiManager.cancel(0);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		String tname = extras.getString("TrackName");
		String tid = extras.getString("TrackID");
		if (tid == null) {
			stopSelf();
		} else {
			this.trackName = tname;
			try {
				this.trackID = Integer.parseInt(tid);
			}
			catch (NumberFormatException ex) {
				Log.e(getPackageName(), "Can not parse trackID to int \n" + ex.getStackTrace().toString());
				stopSelf();
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
}
