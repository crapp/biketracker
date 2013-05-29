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
import org.crappbytes.biketracker.database.TrackTable;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TrackListActivity extends ListActivity implements LoaderCallbacks<Cursor> {
	
	//Members area
	// This is the Adapter being used to display the list's data
	private SimpleCursorAdapter scAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = {TrackTable.COLUMN_NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
        
		scAdapter = new SimpleCursorAdapter(this,
				android.R.layout.activity_list_item,
				null,
				fromColumns,
				toViews,
				0);
		setListAdapter(scAdapter);
		
		// Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
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
		getMenuInflater().inflate(R.menu.track_list, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = null;
		loader = new CursorLoader(this,
				TracksContentProvider.CONTENT_URI_TRACK,
				new String[]{TrackTable.COLUMN_ID, TrackTable.COLUMN_NAME},
				null,
				null,
				TrackTable.COLUMN_TIMESTAMP + " ASC");
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			// Swap the new cursor in.  (The framework will take care of closing the
	        // old cursor once we return.)
			this.scAdapter.swapCursor(cursor);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        this.scAdapter.swapCursor(null);
	}

	/**
	 * Called when an item in the list is clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO: Use this method to start the track detail fragment!
		super.onListItemClick(l, v, position, id);
	}
}
