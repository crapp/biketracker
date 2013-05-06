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

import org.crappbytes.biketracker.GPSDialogFragment.GPSDialogListener;
import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackTable;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TrackActivity extends FragmentActivity implements GPSDialogListener {
	
	//Member area
	private String trackName;
	private Button stopButton;
	private Button pauResButton;
	
	//Interface Member 
	private final OnClickListener pauseRecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			pauseRecording(v);
		}
	};
	
	private final OnClickListener resumeRecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			resumeRecording(v);
		}
	};
	
	private final BroadcastReceiver bCastRecv = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	//Override methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);
		
		//Intent is a connection between Activities
		Intent intent = getIntent();
		this.trackName = intent.getStringExtra("org.crappbytes.TrackName");
		
		Button checkGPS = (Button) findViewById(R.id.butTrackDist);
		checkGPS.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String toastMsg = "GPS ist ";
				if (gpsEnabled()) {
					toastMsg += "an!";
				} else {
					toastMsg += "aus!";
				}
				//Show a toast 
				Toast.makeText(getApplicationContext(), toastMsg,
	                    Toast.LENGTH_SHORT).show();
			}
		});
		
		this.stopButton = (Button) findViewById(R.id.butStopTracking);
		this.pauResButton = (Button) findViewById(R.id.butPauseTracking);
		this.pauResButton.setOnClickListener(pauseRecListener);
		
		insertTrack();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// This verification should be done during onStart() because the system calls
	    // this method when the user returns to the activity, which ensures the desired
	    // location provider is enabled each time the activity resumes from the stopped state.
	    
	    if (!this.gpsEnabled()) {
	        // Build an alert dialog here that requests that the user enable
	        // the location services, then when the user clicks the "OK" button,
	        // call enableLocationSettings()
	    	DialogFragment gpsDialog = new GPSDialogFragment();
	    	gpsDialog.show(getFragmentManager(), "gpsOffDialog");
	    } else {
	    	Intent serviceIntent = new Intent(this, GPSLoggerBackgroundService.class);
			serviceIntent.putExtra("TrackName", this.trackName);
	    	//startService(new Intent(this, GPSLoggerBackgroundService.class));
	    }
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	/**
	 * Stop track recording, needs to be public as we access this onClick Handler via XML
	 */
	public void stopRecording(View v) {
		stopService(new Intent(this, GPSLoggerBackgroundService.class));
		//TODO: Open a Track Overview Activity. Reuse this activity for detail view in track list. Or use a fragment for this  
	}
	
	/**
	 * Pause recording of GPS Waypoints
	 */
	private void pauseRecording(View v) {
		if (stopService(new Intent(this, GPSLoggerBackgroundService.class))) {
			Toast.makeText(getApplicationContext(), "Service stopped successfully",
                    Toast.LENGTH_SHORT).show();
			Drawable icon= getBaseContext().getResources().getDrawable(R.drawable.ic_button_play);
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
			((Button) v).setOnClickListener(resumeRecListener);
		}
	}
	
	/**
	 * Resume waypoint recording
	 * @param v
	 */
	private void resumeRecording(View v) {
		Intent serviceIntent = new Intent(this, GPSLoggerBackgroundService.class);
		serviceIntent.putExtra("TrackName", this.trackName);
		if (startService(serviceIntent) != null) {
			Drawable icon= getBaseContext().getResources().getDrawable(R.drawable.ic_button_pause);
			((Button) v).setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
			((Button) v).setOnClickListener(pauseRecListener);
		}
	}
	
	private void insertTrack() {
		ContentValues cv = new ContentValues();
		cv.put(TrackTable.COLUMN_NAME, this.trackName);
		getContentResolver().insert(TracksContentProvider.CONTENT_URI_TRACK, cv);
	}
	
	private boolean gpsEnabled() {
		 LocationManager locationManager =
		            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
	//Here follow the methods we need to implement because of the GPSDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		this.enableLocationSettings();
		
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

}
