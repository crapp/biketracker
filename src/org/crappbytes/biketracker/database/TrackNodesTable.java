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

package org.crappbytes.biketracker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrackNodesTable {
	public static final String TABLE_NAME = "nodes";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TRACKID = "trackid";
	public static final String COLUMN_ACCURACY = "accuracy";
	public static final String COLUMN_ALTITUDE = "altitude";
	public static final String COLUMN_ALTITUDELPF = "altitudelpf"; //smoothed altitude using a lpf
	public static final String COLUMN_ALTITUDEUP = "altitudeup"; 
	public static final String COLUMN_ALTITUDEDOWN = "altitudedown";
	public static final String COLUMN_BEARING = "bearing";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_SPEED = "speed";
	public static final String COLUMN_DISTANCE = "distance"; //Distance to previous node in km
	public static final String COLUMN_RACETIME = "racetime";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	//Table create string
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME 
			+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ COLUMN_TRACKID + " INTEGER NOT NULL REFERENCES " + TrackTable.TABLE_NAME + "(" + TrackTable.COLUMN_ID + ") ON DELETE CASCADE, "
			+ COLUMN_ACCURACY + " REAL NOT NULL, "
			+ COLUMN_ALTITUDE + " REAL, "
			+ COLUMN_ALTITUDELPF + " REAL, "
			+ COLUMN_ALTITUDEUP + " REAL, "
			+ COLUMN_ALTITUDEDOWN + " REAL, "
			+ COLUMN_BEARING + " REAL, " 
			+ COLUMN_LATITUDE + " REAL NOT NULL, "
			+ COLUMN_LONGITUDE + " REAL NOT NULL, "
			+ COLUMN_SPEED + " REAL, "
			+ COLUMN_DISTANCE + " REAL, "
			+ COLUMN_RACETIME + " INTEGER, " 
			+ COLUMN_TIMESTAMP  + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP )";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(TrackNodesTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
}
