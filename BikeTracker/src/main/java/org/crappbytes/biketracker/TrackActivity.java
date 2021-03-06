/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013 - 2016 Christian Rapp <0x2a at posteo dot org>
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

import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.crappbytes.biketracker.YesCancelDialogFragment.YesCancelDialogListener;
import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.database.TrackTable;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TrackActivity extends FragmentActivity implements YesCancelDialogListener, LoaderCallbacks<Cursor> {

    //Member area
    private TextView tvSpeed;
    private TextView tvAltitude;
    private TextView tvDistance;
    private TextView tvTime;

    private String trackName;
    private String trackID;
    private static final int LOADER_GENERAL = 1;
    private static final int LOADER_FUNC = 2;
    private Timer zeroSpeedTimer;

    private SharedPreferences sharedPrefs;

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

    private final OnClickListener stopListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    //Override methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        this.trackID = "0";

        //get action bar and set ancestral navigation
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Intent is a connection between Activities - naja
        Intent intent = getIntent();
        // get the track name defined in the TrackNameDialogFragment
        this.trackName = intent.getStringExtra("org.crappbytes.TrackName");

        this.tvAltitude = (TextView) findViewById(R.id.tvAlt);
        this.tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        this.tvDistance = (TextView) findViewById(R.id.tvDistance);
        this.tvTime = (TextView) findViewById(R.id.tvTime);

        RelativeLayout checkGPS = (RelativeLayout) findViewById(R.id.relAltitude);
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
                DialogFragment vdf = new ValueDialogFragment();

//                vdf.setExitTransition(new Slide(Gravity.LEFT));
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.content, f, getKey())
//                        .commit();
                Bundle args = new Bundle();
                args.putInt(ValueDialogFragment.ARGS_VALUE_TYPE, ValueDialogFragment.ALTITUDE);
                args.putLong(ValueDialogFragment.ARGS_TID, Long.parseLong(trackID));
                vdf.setArguments(args);
                vdf.show(getFragmentManager(), "altitudefragment");
            }
        });

        RelativeLayout speedArea = (RelativeLayout) findViewById(R.id.relSpeed);
        speedArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment vdf = new ValueDialogFragment();
                Bundle args = new Bundle();
                args.putInt(ValueDialogFragment.ARGS_VALUE_TYPE, ValueDialogFragment.SPEED);
                args.putLong(ValueDialogFragment.ARGS_TID, Long.parseLong(trackID));
                vdf.setArguments(args);
                vdf.show(getFragmentManager(), "speedfragment");
            }
        });

        ImageButton pauResButton = (ImageButton) findViewById(R.id.butPauseTracking);
        pauResButton.setOnClickListener(pauseRecListener);
        ImageButton stopButton = (ImageButton) findViewById(R.id.butStopTracking);
        stopButton.setOnClickListener(stopListener);

        //this.zeroSpeedTimer = new Timer();

        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //insert track in db
        insertTrack();
        //init async loaders
        getLoaderManager().initLoader(LOADER_GENERAL, null, this);
        getLoaderManager().initLoader(LOADER_FUNC, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }


    /**
     * Will be triggered whenever an Item in the Action Bar will be clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            DialogFragment trackCancelDialog = new YesCancelDialogFragment();
            Bundle args = new Bundle();
            args.putInt("Type", YesCancelDialogFragment.DIALOG_STOP_TRACKING);
            trackCancelDialog.setArguments(args);
            trackCancelDialog.show(getFragmentManager(), "trackCancelDialog");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
            DialogFragment gpsDialog = new YesCancelDialogFragment();
            Bundle args = new Bundle();
            args.putInt("Type", YesCancelDialogFragment.DIALOG_GPS);
            gpsDialog.setArguments(args);
            gpsDialog.show(getFragmentManager(), "gpsOffDialog");
        } else {
            Intent serviceIntent = new Intent(this, GPSLoggerBackgroundService.class);
            //Pass the name and the ID of the track to our gps log service.
            serviceIntent.putExtra("TrackName", this.trackName);
            serviceIntent.putExtra("TrackID", this.trackID);
            startService(serviceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DialogFragment trackCancelDialog = new YesCancelDialogFragment();
        Bundle args = new Bundle();
        args.putInt("Type", YesCancelDialogFragment.DIALOG_STOP_TRACKING);
        trackCancelDialog.setArguments(args);
        trackCancelDialog.show(getFragmentManager(), "trackCancelDialog");
    }

    /**
     * Stop track recording, needs to be public as we access this onClick Handler via XML
     */
    public void stopRecording(View v) {
        if (stopService(new Intent(this, GPSLoggerBackgroundService.class))) {
            Toast.makeText(getApplicationContext(), "Service stopped successfully",
                    Toast.LENGTH_SHORT).show();
        }
        NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
        //TODO: Open a Track Overview Activity. Reuse this activity for detail view in track list. Or use a fragment for this
    }

    /**
     * Pause recording of GPS Waypoints
     */
    private void pauseRecording(View v) {
        if (stopService(new Intent(this, GPSLoggerBackgroundService.class))) {
            Toast.makeText(getApplicationContext(), "Service stopped successfully",
                    Toast.LENGTH_SHORT).show();
            Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp);
            ((ImageButton) v).setImageDrawable(icon);
            v.setOnClickListener(resumeRecListener);
        }
    }

    /**
     * Resume waypoint recording
     * @param v
     */
    private void resumeRecording(View v) {
        Intent serviceIntent = new Intent(this, GPSLoggerBackgroundService.class);
        serviceIntent.putExtra("TrackName", this.trackName);
        serviceIntent.putExtra("TrackID", this.trackID);
        if (startService(serviceIntent) != null) {
            Toast.makeText(getApplicationContext(), "Service started successfully",
                    Toast.LENGTH_SHORT).show();
            Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp);
            ((ImageButton) v).setImageDrawable(icon);
            v.setOnClickListener(pauseRecListener);
        }
    }

    /**
     * Insert new Track into Database
     */
    private void insertTrack() {
        try {
            if (Integer.parseInt(this.trackID) > 0) {
                Cursor cursor = getContentResolver().query(TracksContentProvider.CONTENT_URI_TRACK,
                        new String[]{TrackTable.COLUMN_ID},
                        TrackTable.COLUMN_ID + "=?",
                        new String[] {this.trackID},
                        null);
                if (cursor != null && cursor.getCount() > 0) {
                    //ups the track is already in the database -> fetch data
                    return;
                }
            }
        }
        catch (NumberFormatException ex) {
            Log.e(getPackageName(), ex.getStackTrace().toString());
        }
        ContentValues cv = new ContentValues();
        //all we need to provide is the trackname and the lpf
        cv.put(TrackTable.COLUMN_NAME, this.trackName);
        cv.put(TrackTable.COLLUMN_LPF, this.sharedPrefs.getString(SettingsFragment.PREF_LPF, "25"));
        Uri url = getContentResolver().insert(TracksContentProvider.CONTENT_URI_TRACK, cv);
        //the last element of the Uri returned by insert is the ID. Store it as we need to pass it to the background service
        if (url != null) {
            this.trackID = url.getLastPathSegment();
        }
    }

    private boolean gpsEnabled() {
         LocationManager locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void enableLocationSettings() {
        //Start the location settings
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    //Here follow the methods we need to implement because of the GPSDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int type) {
        switch (type) {
        case YesCancelDialogFragment.DIALOG_GPS:
            this.enableLocationSettings();
            break;
        case YesCancelDialogFragment.DIALOG_STOP_TRACKING:
            this.stopRecording(null);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int type) {
        switch (type) {
        case YesCancelDialogFragment.DIALOG_GPS:
            //TODO: Is it ok to go back to the mainActivity if user does not want to
            //activate GPS?!
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            break;
        case YesCancelDialogFragment.DIALOG_STOP_TRACKING:
            //Cancel so nothing to do
            break;
        }
    }

    //Loader callback methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        switch (id) {
        case LOADER_GENERAL:
            //Our standard query to get all waypoints for the current track
            loader = new CursorLoader(this,
                    TracksContentProvider.CONTENT_URI_NODES,
                    null,
                    TrackNodesTable.COLUMN_TRACKID + "=?",
                    new String[]{this.trackID},
                    TrackNodesTable.COLUMN_TIMESTAMP + " ASC");
            break;
        case LOADER_FUNC:
            String[] projection = new String[] {TrackNodesTable.COLUMN_ID,
                            "SUM(" + TrackNodesTable.COLUMN_DISTANCE + ") AS sum_" + TrackNodesTable.COLUMN_DISTANCE};
            loader = new CursorLoader(this,
                    TracksContentProvider.CONTENT_URI_NODES,
                    projection,
                    TrackNodesTable.COLUMN_TRACKID + "=?",
                    new String[]{this.trackID},
                    null);
            break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
        case LOADER_GENERAL:
            if (cursor != null && cursor.getCount() > 0) {
                //this.zeroSpeedTimer.cancel();
                //the last element is important for us
                cursor.moveToLast();
                Double height = cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ALTITUDE));
                double speed = cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_SPEED));

                //Update race time
                long rTime = cursor.getLong(cursor.getColumnIndex(TrackNodesTable.COLUMN_RACETIME));
                long hours = TimeUnit.SECONDS.toHours(rTime);
                long minutes = TimeUnit.SECONDS.toMinutes(rTime - (hours * 3600));
                long seconds = TimeUnit.SECONDS.toSeconds(rTime - ((hours * 3600) + (minutes * 60)));
                //Toast.makeText(getBaseContext(),
                //        "Race time: " + String.valueOf(rTime),
                //        Toast.LENGTH_SHORT).show();
                String shours = "";
                //string must have at least two digits --> substring
                shours = String.format("%02d", hours);
                String sminutes = "";
                sminutes = String.format("%02d", minutes);
                String sseconds = "";
                sseconds = String.format("%02d", seconds);
                this.tvTime.setText(shours + ":" + sminutes + ":" + sseconds);

                //update height
                height = Utility.round(height, 0);
                this.tvAltitude.setText(String.valueOf(height.intValue()) + " m");

                //update speed
                speed = Utility.convertSpeed(speed);
                speed = Utility.round(speed, 0);
                this.tvSpeed.setText(String.valueOf(speed) + " km/h");

                //Use a timer to set displayed Speed to 0 after some time
//				zeroSpeedTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 10000);
            }
            break;
        case LOADER_FUNC:
            if (cursor != null && cursor.getCount() > 0) {
                //update distance
                cursor.moveToFirst();
                double distInKM = Utility.round(cursor.getDouble(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_DISTANCE)), 3);
                this.tvDistance.setText(String.valueOf(distInKM) + " km");
            }
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }

}
