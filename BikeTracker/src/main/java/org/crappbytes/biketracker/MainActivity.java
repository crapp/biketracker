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

import org.crappbytes.biketracker.TrackNameDialogFragment.TrackDialogListener;

import android.content.SharedPreferences;
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

//import com.dropbox.sync.android.DbxAccountManager;

public class MainActivity extends Activity implements TrackDialogListener{

    //private DbxAccountManager dbxAccManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(false);
        }

        //get our buttons and assign onClickListener to them
        Button butNewTrack = (Button) findViewById(R.id.butStartTracking);
        butNewTrack.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				DialogFragment trackDialog = new TrackNameDialogFragment();
				trackDialog.show(getFragmentManager(), "trackdialog");
			}
		});
        Button butTrackList = (Button) findViewById(R.id.butShowTracks);
        butTrackList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent trackListIntent = new Intent(MainActivity.this, TrackListActivity.class);
				Intent trackListIntent = new Intent(MainActivity.this, TrackContainerActivity.class);
		        startActivity(trackListIntent);
			}
		});

        //set default values for our application
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //read from preferences if we should activate dropbox sync
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dropbox = sharedPrefs.getBoolean("pref_syncDropbox", false);
        if (dropbox) {
            //this.dropboxSync(dropbox);
        }
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

    private void dropboxSync(boolean enable) {
        if (enable) {
//            if (this.dbxAccManager == null) {
//                //FIXME: How to handle the app key/secret in oss???
//                //FIXME: What for do we need dropbox support? Export tracks to dropbox? Syncing app db directly is highly discouraged!
//            }
        }
    }
}
