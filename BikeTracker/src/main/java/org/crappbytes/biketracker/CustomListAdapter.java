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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.database.TrackTable;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomListAdapter extends CursorAdapter {

	private Context ctxt; //application context
	private Cursor cursor;
	private int passedFlags;
	
	public CustomListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.ctxt = context;
		this.cursor = c;
		this.passedFlags = flags;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			TextView name = (TextView) view.findViewById(R.id.rowTrackName);
			TextView dist = (TextView) view.findViewById(R.id.rowTrackDistance);
			TextView altUp = (TextView) view.findViewById(R.id.rowTrackTotalHeight);
			TextView date = (TextView) view.findViewById(R.id.rowTrackDate);
            //TODO: I really don't need hungarian notation :(
			String sName = cursor.getString(cursor.getColumnIndex(TrackTable.COLUMN_NAME));
            String sDist = "0";
            if (!cursor.isNull(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_DISTANCE))) {
                double dDist = Utility.round(cursor.getDouble(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_DISTANCE)), 2);
			    sDist = String.valueOf(dDist);
            }
            String sAltUp = "0";
            if (!cursor.isNull(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_ALTITUDEUP))) {
                int iAltUp = cursor.getInt(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_ALTITUDEUP));
			    sAltUp = String.valueOf(iAltUp);
            }
			String sDate = cursor.getString(cursor.getColumnIndex(TrackTable.COLUMN_TIMESTAMP));
			
			
			name.setText(sName);
			dist.setText(sDist);
			altUp.setText(sAltUp);
			//get system date format
			DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
			//parse date string from db
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = null;
			try {
				d = dFormat.parse(sDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			date.setText(dateFormat.format(d));
			
			
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		//this is the layout of the columns
		View view = LayoutInflater.from(context).inflate(R.layout.tracklistrow_layout, parent, false);
		return view;
	}

}
