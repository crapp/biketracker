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

import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.database.TrackTable;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class TrackListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	public interface onTrackSelectedListener {
		void onTrackSelected(long id);
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
        
		
		adapter = new CustomListAdapter(getActivity().getApplicationContext(), null, 0);
		setListAdapter(adapter);

		// Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setEmptyView(pBar);

        //we registering our listview for longpress context menu
        registerForContextMenu(getListView());
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.tracklist_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.tracklst_context_del:
                int delete = getActivity().getContentResolver().delete(TracksContentProvider.CONTENT_URI_TRACK,
                        TrackTable.COLUMN_ID + "=?",
                        new String[]{String.valueOf(info.id)});
                if (delete > 0) {
                    Toast.makeText(getActivity(),
                            "Deleted one Track",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.tracklst_context_show:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
	public void onAttach(Context context) {
		super.onAttach(context);
		
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Activity a = getActivity();
            if (a != null) {
                this.activityCallback = (onTrackSelectedListener) a;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnHeadlineSelectedListener " + e.getMessage());
        }
	}
	
	/**
	 * Called when an item in the list is clicked
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Notify the container activity of selected item
        this.activityCallback.onTrackSelected(id);
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
