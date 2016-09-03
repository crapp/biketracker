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

import org.crappbytes.biketracker.TrackListFragment.onTrackSelectedListener;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class TrackContainerActivity extends Activity implements YesCancelDialogFragment.YesCancelDialogListener, onTrackSelectedListener {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_container);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
		
        //instantiate the list fragment 
		TrackListFragment TLFrag = new TrackListFragment();
		//use the FragmentManager to add TrackListFragment to view container  
		getFragmentManager().beginTransaction().add(R.id.fragment_container, TLFrag).commit();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_container, menu);
		return true;
	}

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
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
		        return super.onOptionsItemSelected(item);
        }
	}

	@Override
	public void onTrackSelected(long id) {
		//TODO: Switch Fragments and tell the new fragment which position was selected
		TrackListDetailFragment tldFrag = new TrackListDetailFragment();
        Bundle args = new Bundle();
        args.putLong(TrackListDetailFragment.ARG_POSITION, id);
        tldFrag.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, tldFrag);
        transaction.addToBackStack(null);

        //TODO: Nice to have would be a card flip animation

        transaction.commit();
	}

	@Override
	public void onBackPressed() {

		int count = getFragmentManager().getBackStackEntryCount();

		if (count == 0) {
			super.onBackPressed();
		} else {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true); // disable the button
                actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
            }
			getFragmentManager().popBackStack();
		}

	}

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int type) {
        switch(type) {
            case YesCancelDialogFragment.DIALOG_DELETE_TRACK:

        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int type) {
        switch(type) {
            case YesCancelDialogFragment.DIALOG_DELETE_TRACK:
                // do nothing
        }
    }
}
