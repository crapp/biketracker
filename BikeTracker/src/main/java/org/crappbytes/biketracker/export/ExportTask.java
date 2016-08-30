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

package org.crappbytes.biketracker.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.crappbytes.biketracker.R;
import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saedelaere on 03.06.13.
 */
public class ExportTask extends AsyncTask<Long, Void, Boolean> {

    private ProgressDialog pgDialog;
    private Context ctxt; //warning, this is the activity context. might be dangerous because of android lifecycle!
    private String trackName;

    public ExportTask(Context ctxt, String trackName) {
        this.ctxt = ctxt;
        this.trackName = trackName;
    }

    @Override
    protected void onPreExecute() {
        //show the progress dialog
        pgDialog = new ProgressDialog(this.ctxt);
        pgDialog.setIndeterminate(true);
        pgDialog.setCancelable(false);
        pgDialog.setMessage(ctxt.getResources().getString(R.string.exportKMLTitle));
        pgDialog.setTitle(ctxt.getResources().getString(R.string.exportKMLMessage, trackName));
        pgDialog.show();
    }

    @Override
    protected Boolean doInBackground(Long... trackid) {
        boolean ret = false;
        //query all nodes via the ContentProvider
        Cursor cursor = ctxt.getContentResolver().query(TracksContentProvider.CONTENT_URI_NODES,
                null,
                TrackNodesTable.COLUMN_TRACKID + "=?",
                new String[] {String.valueOf(trackid[0])},
                TrackNodesTable.COLUMN_TIMESTAMP + " ASC");
        //check if we got a usable cursor.
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Node> nodeList = new ArrayList<Node>();
            while (cursor.moveToNext()) {
                Node n = new Node();
                n.setId(cursor.getInt(cursor.getColumnIndex(TrackNodesTable.COLUMN_ID)));
                n.setTrackid(cursor.getInt(cursor.getColumnIndex(TrackNodesTable.COLUMN_TRACKID)));
                n.setAccuracy(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ACCURACY)));
                n.setAltitude(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ALTITUDE)));
                n.setAltitudelpf(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ALTITUDELPF)));
                n.setAltitudeup(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ALTITUDEUP)));
                n.setAltitudedown(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_ALTITUDEDOWN)));
                n.setBearing(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_BEARING)));
                n.setLatitude(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_LATITUDE)));
                n.setLongitude(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_LONGITUDE)));
                n.setSpeed(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_SPEED)));
                n.setDistance(cursor.getDouble(cursor.getColumnIndex(TrackNodesTable.COLUMN_DISTANCE)));
                n.setRaceTime(cursor.getInt(cursor.getColumnIndex(TrackNodesTable.COLUMN_RACETIME)));
                String datestring = cursor.getString(cursor.getColumnIndex(TrackNodesTable.COLUMN_TIMESTAMP));
                try {
                    n.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datestring));
                }
                catch (ParseException ex) {
                    Log.e(ctxt.getPackageName(), ex.getStackTrace().toString());
                }
                nodeList.add(n);
            }
            //FIXME: There is not only KML export
            String fileName = trackName.trim().toLowerCase().replaceAll("[^\\w\\s]","") + ".kml";
            GeoExportKML geoxKML = new GeoExportKML(this.ctxt, fileName, trackName, nodeList);
            geoxKML.serializeDataToFile();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //dismiss the progressbar dialog
        this.pgDialog.dismiss();
    }
}
