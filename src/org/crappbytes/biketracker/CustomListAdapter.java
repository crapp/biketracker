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

	private Context ctxt;
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
			String sName = cursor.getString(cursor.getColumnIndex(TrackTable.COLUMN_NAME));
			String sDist = cursor.getString(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_DISTANCE));
			String sAltUp = cursor.getString(cursor.getColumnIndex("sum_" + TrackNodesTable.COLUMN_ALTITUDEUP));
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
