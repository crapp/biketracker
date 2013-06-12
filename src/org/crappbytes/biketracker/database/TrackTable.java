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

public class TrackTable {
	public static final String TABLE_NAME = "track";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_MODIFIED = "modified";
    public static final String COLLUMN_LPF = "lowpassfilter";
 	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	//Table create String
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ COLUMN_NAME + " TEXT NOT NULL, "
			+ COLUMN_MODIFIED + " DATETIME DEFAULT NULL, "
            + COLLUMN_LPF + " TEXT NOT NULL, "
			+ COLUMN_TIMESTAMP + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)";
	
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
