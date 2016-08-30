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

import org.crappbytes.biketracker.TrackNameDialogFragment.TrackDialogListener;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements TrackDialogListener{

    // some constants for permissions we need
    private static final int PERMISSION_LOCATION = 1;
    private static final int PERMISSION_STORAGE = 2;
//    private static final int PERMISSION_CAMERA = 3;
//    private static final int PERMISSION_NETWORK = 4;
    private boolean showTracks = false;
    private boolean canTrack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false);
            }
        }

        //get our buttons and assign onClickListener to them
        Button butNewTrack = (Button) findViewById(R.id.butStartTracking);
        butNewTrack.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
                if (canTrack) {
                    DialogFragment trackDialog = new TrackNameDialogFragment();
                    trackDialog.show(getFragmentManager(), "trackdialog");
                }
			}
		});
        Button butTrackList = (Button) findViewById(R.id.butShowTracks);
        butTrackList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if (showTracks) {
                    //Intent trackListIntent = new Intent(MainActivity.this, TrackListActivity.class);
                    Intent trackListIntent = new Intent(MainActivity.this, TrackContainerActivity.class);
                    startActivity(trackListIntent);
                }
			}
		});

        requestPermission(MainActivity.PERMISSION_LOCATION);
        requestPermission(MainActivity.PERMISSION_STORAGE);

        //set default values for our application
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
	public void onDialogPositiveClick(DialogFragment dialog, String trackName) {
		Intent intNewTrack = new Intent(MainActivity.this, TrackActivity.class);
		intNewTrack.putExtra("org.crappbytes.TrackName", trackName);
		startActivity(intNewTrack);
	}


	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MainActivity.PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.canTrack = true;
                } else {
                    this.canTrack = false;
                }
                break;
            }
            case MainActivity.PERMISSION_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.showTracks = true;
                } else {
                    this.showTracks = false;
                }
                break;
            }
        }
    }

    private void requestPermission(final int whichPermission) {

        String permission = "";
        String alertBoxTitle = "";
        String alertBoxText = "";

        switch (whichPermission) {
            case MainActivity.PERMISSION_LOCATION:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case MainActivity.PERMISSION_STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            default:
                break;
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {
                // TODO: Either remove this code or activate it
//                new AlertDialog.Builder(this)
//                        .setTitle("Inform and request")
//                        .setMessage("You need to enable permissions, bla bla bla")
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(
//                                        MainActivity.this,
//                                        new String[]{permission},
//                                        whichPermission);
//                            }
//                        })
//                        .show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission}, whichPermission);
            }
        } else {
            if (whichPermission == MainActivity.PERMISSION_LOCATION) {
                this.canTrack = true;
                return;
            }
            if (whichPermission == MainActivity.PERMISSION_STORAGE) {
                this.showTracks = true;
            }

        }
    }
}
