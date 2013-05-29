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

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TrackListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	public interface onTrackSelectedListener {
		public void onTrackSelected(int position);
	}
	
	private onTrackSelectedListener activityCallback;
	private CustomListAdapter adapter;
	private ProgressBar pBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create a progress bar to display while the list loads
        pBar = new ProgressBar(getActivity());
        pBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        pBar.setIndeterminate(true);
        
		
		adapter = new CustomListAdapter(getActivity(), null, 0);
		setListAdapter(adapter);
		
		// Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setEmptyView(pBar);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            this.activityCallback = (onTrackSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}
	
	/**
	 * Called when an item in the list is clicked
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Notify the container activity of selected item
        this.activityCallback.onTrackSelected(position);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cloader = null;
		String[] projection = new String[]{TrackTable.TABLE_NAME + "." + TrackTable.COLUMN_ID,
				TrackTable.COLUMN_NAME,
				"SUM(" + TrackNodesTable.COLUMN_DISTANCE + ") AS sum_" + TrackNodesTable.COLUMN_DISTANCE,
				"SUM(" + TrackNodesTable.COLUMN_ALTITUDEUP + ") AS sum_" + TrackNodesTable.COLUMN_ALTITUDEUP,
				TrackTable.TABLE_NAME + "." + TrackTable.COLUMN_TIMESTAMP};
		cloader = new CursorLoader(getActivity(),
				TracksContentProvider.CONTENT_URI_TRACKNODES,
				projection,
				null,
				null,
				TrackTable.TABLE_NAME + "." + TrackTable.COLUMN_TIMESTAMP + " ASC");
		return cloader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			// Swap the new cursor in.  (The framework will take care of closing the
	        // old cursor once we return.)
			this.adapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        this.adapter.swapCursor(null);
	}
}
