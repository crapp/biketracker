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

package org.crappbytes.biketracker.contentprovider;

import org.crappbytes.biketracker.database.TrackDBHelper;
import org.crappbytes.biketracker.database.TrackNodesTable;
import org.crappbytes.biketracker.database.TrackTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TracksContentProvider extends ContentProvider {

    // database
    private TrackDBHelper db;

    // Used for the UriMatcher
    // Track table
    private static final int TRACK = 10;
    private static final int TRACK_ID = 20;
    // Nodes table
    private static final int NODES = 30;
    private static final int NODES_ID = 40;
    //Special code for join and groupBy
    private static final int TRACKNODES = 50;

    private static final String AUTHORITY = "org.crappbytes.biketracker.contentprovider";

    // Define the Uris
    private static final String PATH_TRACK = "track";
    private static final String PATH_NODES = "nodes";
    private static final String PATH_TRACKNODES = "tracknodes";

    // Content uri
    public static final Uri CONTENT_URI_TRACK = Uri.parse("content://"
            + AUTHORITY + "/" + PATH_TRACK);
    public static final Uri CONTENT_URI_NODES = Uri.parse("content://"
            + AUTHORITY + "/" + PATH_NODES);
    public static final Uri CONTENT_URI_TRACKNODES = Uri.parse("content://"
            + AUTHORITY + "/" + PATH_TRACKNODES);

    // FIXME: Do we need to define CONTENT_TYPE --> RTFM

    // Setup UriMatcher, one Uri pair for each table and one for the table join
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, PATH_TRACK, TRACK);
        sURIMatcher.addURI(AUTHORITY, PATH_TRACK + "/#", TRACK_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_NODES, NODES);
        sURIMatcher.addURI(AUTHORITY, PATH_NODES + "/#", NODES_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_TRACKNODES, TRACKNODES);
    }

    @Override
    public boolean onCreate() {
        this.db = new TrackDBHelper(getContext(), null, null, 0);
        return false; // FIXME: return false??!!
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = null;
        try {
            sqlDB = db.getWritableDatabase();
        }
        catch(SQLiteException ex) {
            //not possible to get the package name here :/
            Log.e("", ex.getStackTrace().toString());
            return returnUri;
        }

        // activate foreign keys support for sqlite
        foreignKeys(sqlDB);
        long id = 0;
        switch (uriType) {
        case TRACK:
            id = sqlDB.insert(TrackTable.TABLE_NAME, null, values);
            returnUri = Uri.parse(PATH_TRACK + "/" + id);
            break;
        case NODES:
            id = sqlDB.insert(TrackNodesTable.TABLE_NAME, null, values);
            returnUri = Uri.parse(PATH_NODES + "/" + id);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //Notify all Observers (Loaders) about the database change
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public synchronized int delete(Uri uri, String selection,
            String[] selectionArgs) {
        int noOfRowsDeleted = 0;
        //string that holds the ID if uri = *_ID
        String id = "";
        SQLiteDatabase sqlDB = null;
        try {
            sqlDB = db.getWritableDatabase();
        }
        catch(SQLiteException ex) {
            //not possible to get the package name here :/
            Log.e("", ex.getStackTrace().toString());
            return 0;
        }

        // activate foreign keys support for sqlite
        foreignKeys(sqlDB);
        switch (sURIMatcher.match(uri)) {
        case TRACK:
            //ex. selection => NAME = ? AND FOO =? => selectionArgs: Baz, Boo (replace the questions marks)
            noOfRowsDeleted = sqlDB.delete(TrackTable.TABLE_NAME, selection, selectionArgs);
            break;
        case TRACK_ID:
            id = uri.getLastPathSegment();
            noOfRowsDeleted = sqlDB.delete(TrackTable.TABLE_NAME, TrackTable.COLUMN_ID + "=" + id, null);
            break;
        case NODES:
            noOfRowsDeleted = sqlDB.delete(TrackNodesTable.TABLE_NAME, selection, null);
            break;
        case NODES_ID:
            id = uri.getLastPathSegment();
            noOfRowsDeleted = sqlDB.delete(TrackNodesTable.TABLE_NAME, TrackNodesTable.COLUMN_ID + "=" + id, null);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //Notify all Observers (Loaders) about the database change
        getContext().getContentResolver().notifyChange(uri, null);
        //TODO: Nasty hack to inform all TRACKNODES listeners as we only delete from TRACKS because of the database on delete cascade constraint
        getContext().getContentResolver().notifyChange(TracksContentProvider.CONTENT_URI_TRACKNODES, null);
        return noOfRowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: In the tut, vogella checked whether all the requested columns really exist.
        // Not needed?!!? I mean this is a non public provider
        SQLiteDatabase sqlDB = null;
        try {
            //we only need a readable database here
            sqlDB = db.getReadableDatabase();
        }
        catch(SQLiteException ex) {
            //not possible to get the package name here :/
            Log.e("", ex.getStackTrace().toString());
            return null;
        }

        //Using an SQLiteQueryBuilder rather than query()
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        String id = "";

        String groupBy = null;
        String having = null;
        switch (sURIMatcher.match(uri)) {
        case TRACK:
            qBuilder.setTables(TrackTable.TABLE_NAME);
            break;
        case TRACK_ID:
            id = uri.getLastPathSegment();
            qBuilder.setTables(TrackTable.TABLE_NAME);
            selection = TrackTable.COLUMN_ID + "=" + id;
            break;
        case NODES:
            qBuilder.setTables(TrackNodesTable.TABLE_NAME);
            break;
        case NODES_ID:
            id = uri.getLastPathSegment();
            qBuilder.setTables(TrackNodesTable.TABLE_NAME);
            selection = TrackNodesTable.COLUMN_ID + "=" + id;
            break;
        case TRACKNODES:
            //qBuilder with cross table -- problem no tracks that have no nodes --> bad
            //qBuilder.setTables(TrackTable.TABLE_NAME + ", " + TrackNodesTable.TABLE_NAME);
            //qBuilder with LEFT OUTER JOIN gets also tracks with no nodes
            qBuilder.setTables(TrackTable.TABLE_NAME
                    + " LEFT OUTER JOIN "
                    + TrackNodesTable.TABLE_NAME
                    + " ON ("
                    + TrackTable.TABLE_NAME + "." + TrackTable.COLUMN_ID
                    + " = "
                    + TrackNodesTable.COLUMN_TRACKID
                    + ")");
            groupBy = TrackTable.TABLE_NAME + "." + TrackTable.COLUMN_ID;
        }
        cursor = qBuilder.query(sqlDB, projection, selection, selectionArgs, groupBy, having, sortOrder);
        //Set the notification URI for the cursor we return. This is needed for all loaders and observers
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        int affectedRows = 0;
        String id = "";
        SQLiteDatabase sqlDB = null;
        try {
            sqlDB = db.getWritableDatabase();
        }
        catch(SQLiteException ex) {
            //not possible to get the package name here :/
            Log.e("", ex.getStackTrace().toString());
            return affectedRows;
        }
        // activate foreign keys support for sqlite
        foreignKeys(sqlDB);
        switch(sURIMatcher.match(uri)) {
        case TRACK:
            affectedRows = sqlDB.update(TrackTable.TABLE_NAME, values, selection, selectionArgs);
            break;
        case TRACK_ID:
            //get id from uri path
            id = uri.getLastPathSegment();
            affectedRows = sqlDB.update(TrackTable.TABLE_NAME, values, TrackTable.COLUMN_ID + "=" + id, null);
            break;
        case NODES:
            affectedRows = sqlDB.update(TrackNodesTable.TABLE_NAME, values, selection, selectionArgs);
            break;
        case NODES_ID:
            id = uri.getLastPathSegment();
            affectedRows = sqlDB.update(TrackNodesTable.TABLE_NAME, values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //Notify all Observers (Loaders) about the database change
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }

    private void foreignKeys(SQLiteDatabase sqlDB) {

        // FIXME: Seems like we should use the DBHelper class to activate foreign key support.
        // --> http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#onConfigure%28android.database.sqlite.SQLiteDatabase%29

        try {
            sqlDB.execSQL("PRAGMA foreign_keys = ON;");
        }
        catch(SQLException ex) {
            //not possible to get the package name here :/
            Log.e("", ex.getStackTrace().toString());
        }
    }
}
