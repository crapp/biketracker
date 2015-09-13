/*
 *  BikeTracker is an Android Application.
 *  Copyright (C) 2013, 2014, 2015 Christian Rapp <0x2a at posteo dot org>
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

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.database.TrackTable;
import org.crappbytes.biketracker.export.ExportTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackListDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    public final static String ARG_POSITION = "argPosition";
    private final static int LOADER_TRACK = 10;
    private final static int LOADER_NODES = 20;
    private final static int LOADER_NODESFUNC = 30;

    private TextView trackName;
    private TextView trackBegin;
    private TextView trackEnd;
    private TextView trackDistance;
    private TextView trackAscend;
    private TextView trackDescend;
    private TextView trackAltMax;
    private TextView trackAltMin;
    private TextView trackSpeedMax;
    private TextView trackSpeedMin;
    private TextView trackSpeedAvg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //yes we have a menu :)
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(LOADER_TRACK, null, this);
        getLoaderManager().initLoader(LOADER_NODES, null, this);
        getLoaderManager().initLoader(LOADER_NODESFUNC, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //TODO:
        //inflate the layout for this fragment
		View view = inflater.inflate(R.layout.tracklist_detail_layout, container, false);

        this.trackName = (TextView) view.findViewById(R.id.detailTrackName);
        this.trackBegin = (TextView) view.findViewById(R.id.detailTrackStart);
        this.trackEnd = (TextView) view.findViewById(R.id.detailTrackEnd);
        this.trackDistance = (TextView) view.findViewById(R.id.detailTrackDist);
        this.trackAscend = (TextView) view.findViewById(R.id.detail_heightUp);
        this.trackDescend = (TextView) view.findViewById(R.id.detail_heightDown);
        this.trackAltMax = (TextView) view.findViewById(R.id.detail_heightMax);
        this.trackAltMin = (TextView) view.findViewById(R.id.detail_heightMin);
        this.trackSpeedMax = (TextView) view.findViewById(R.id.detail_speedMax);
        this.trackSpeedMin = (TextView) view.findViewById(R.id.detail_speedMin);
        this.trackSpeedAvg = (TextView) view.findViewById(R.id.detail_speedAvg);

        return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tracklist_detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = getArguments().getLong(TrackListDetailFragment.ARG_POSITION);
        switch (item.getItemId()) {
            case R.id.menutrackdelete:
                //TODO: Ask if sure to delete!
                int delete = getActivity().getContentResolver().delete(TracksContentProvider.CONTENT_URI_TRACK,
                                TrackTable.COLUMN_ID + "=?",
                                new String[]{String.valueOf(id)});
                return true;
            case R.id.menuexportkml:
                ExportTask expTask = new ExportTask(getActivity(), this.trackName.getText().toString());
                Long ids[] = new Long[] {id};
                expTask.execute(ids);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cloader = null;
        Bundle args = getArguments();
        long id = args.getLong(TrackListDetailFragment.ARG_POSITION);
        switch (i) {
            case LOADER_TRACK:
                cloader = new CursorLoader(getActivity(),
                        TracksContentProvider.CONTENT_URI_TRACK,
                        null,
                        TrackTable.COLUMN_ID + "=?",
                        new String[] {String.valueOf(id)},
                        null);
                break;
            case LOADER_NODES:
                cloader = new CursorLoader(getActivity(),
                        TracksContentProvider.CONTENT_URI_NODES,
                        null,
                        TrackNodesTable.COLUMN_TRACKID + "=?",
                        new String[] {String.valueOf(id)},
                        TrackNodesTable.COLUMN_TIMESTAMP + " ASC");
                break;
            case LOADER_NODESFUNC:
                String projection[] = new String[] {"MAX(" + TrackNodesTable.COLUMN_ALTITUDELPF + ") AS max_" + TrackNodesTable.COLUMN_ALTITUDELPF,
                        "MIN(" + TrackNodesTable.COLUMN_ALTITUDELPF + ") AS min_" + TrackNodesTable.COLUMN_ALTITUDELPF,
                        "SUM(" + TrackNodesTable.COLUMN_ALTITUDEUP + ") AS sum_" + TrackNodesTable.COLUMN_ALTITUDEUP,
                        "SUM(" + TrackNodesTable.COLUMN_ALTITUDEDOWN + ") AS sum_" + TrackNodesTable.COLUMN_ALTITUDEDOWN,
                        "SUM(" + TrackNodesTable.COLUMN_DISTANCE + ") AS sum_" + TrackNodesTable.COLUMN_DISTANCE,
                        "MIN(" + TrackNodesTable.COLUMN_SPEED + ") AS min_" + TrackNodesTable.COLUMN_SPEED,
                        "MAX(" + TrackNodesTable.COLUMN_SPEED + ") AS max_" + TrackNodesTable.COLUMN_SPEED,
                        "AVG(" + TrackNodesTable.COLUMN_SPEED + ") AS avg_" + TrackNodesTable.COLUMN_SPEED};
                cloader = new CursorLoader(getActivity(),
                        TracksContentProvider.CONTENT_URI_NODES,
                        projection,
                        TrackNodesTable.COLUMN_TRACKID + "=?",
                        new String[] {String.valueOf(id)},
                        null);
        }
        return cloader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            switch (cursorLoader.getId()) {
                case LOADER_TRACK:
                    //move to first entry in cursor.
                    cursor.moveToFirst();
                    this.trackName.setText(cursor.getString(cursor.getColumnIndex(TrackTable.COLUMN_NAME)));
                    break;
                case LOADER_NODES:
                    //move to first entry in cursor.
                    cursor.moveToFirst();
                    String dateStringFrom = cursor.getString(cursor.getColumnIndex(TrackNodesTable.COLUMN_TIMESTAMP));
                    //move to last entry in cursor.
                    cursor.moveToLast();
                    String dateStringTo = cursor.getString(cursor.getColumnIndex(TrackNodesTable.COLUMN_TIMESTAMP));

                    this.trackBegin.setText(dateStringFrom);

                    this.trackEnd.setText(dateStringTo);

//                    //get system date format
//                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
//                    //parse date string from db
//                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                    Date d = null;
//                    try {
//                        d = dFormat.parse(dateStringFrom);
//                        trackBegin.setText(dateFormat.format(d));
//                        d= dFormat.parse(dateStringTo);
//                        trackEnd.setText(dateFormat.format(d));
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                    break;
                case LOADER_NODESFUNC:
                    //move to first entry in cursor.
                    cursor.moveToFirst();
                    Double dist;
                    Double max_altitudelpf;
                    Double min_altitudelpf;
                    Double ascend;
                    Double descend;
                    Double speedMin;
                    Double speedMax;
                    Double speedAvg;
                    dist = Utility.round(cursor.getDouble(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_DISTANCE)), 2);
                    ascend = cursor.getDouble(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_ALTITUDEUP));
                    descend = cursor.getDouble(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_ALTITUDEDOWN));
                    max_altitudelpf = cursor.getDouble(cursor.getColumnIndex("max_" + TrackNodesTable.COLUMN_ALTITUDELPF));
                    min_altitudelpf = cursor.getDouble(cursor.getColumnIndex("min_" + TrackNodesTable.COLUMN_ALTITUDELPF));
                    speedMin = Utility.round(Utility.convertSpeed(cursor.getDouble(cursor.getColumnIndex("min_" + TrackNodesTable.COLUMN_SPEED))), 1);
                    speedMax = Utility.round(Utility.convertSpeed(cursor.getDouble(cursor.getColumnIndex("max_" + TrackNodesTable.COLUMN_SPEED))), 1);
                    speedAvg = Utility.round(Utility.convertSpeed(cursor.getDouble(cursor.getColumnIndex("avg_" + TrackNodesTable.COLUMN_SPEED))), 1);
                    String sDist = getResources().getString(R.string.distance) + ": " + String.valueOf(dist);
                    String trackAsc = getResources().getString(R.string.altitudeAscend) + " " + String.valueOf(ascend.intValue()) + "m";
                    String trackDesc = getResources().getString(R.string.altitudeDescend) + " " + String.valueOf(descend.intValue()) + "m";
                    String maxAlti = getResources().getString(R.string.altitudeMax) + " " + String.valueOf(max_altitudelpf.intValue()) + "m";
                    String minAlti = getResources().getString(R.string.altitudeMin) + " " + String.valueOf(min_altitudelpf.intValue()) + "m";
                    String minSpeed = getResources().getString(R.string.altitudeMin) + " " + String.valueOf(speedMin) + "km/h";
                    String maxSpeed = getResources().getString(R.string.altitudeMax) + " " + String.valueOf(speedMax) + "km/h";
                    String avgSpeed = getResources().getString(R.string.speedAverage) + " " + String.valueOf(speedAvg) + "km/h";
                    this.trackDistance.setText(sDist);
                    this.trackAscend.setText(trackAsc);
                    this.trackDescend.setText(trackDesc);
                    this.trackAltMax.setText(maxAlti);
                    this.trackAltMin.setText(minAlti);
                    this.trackSpeedMin.setText(minSpeed);
                    this.trackSpeedMax.setText(maxSpeed);
                    this.trackSpeedAvg.setText(avgSpeed);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //nothing to do here
    }
}
