package org.crappbytes.biketracker.contentprovider;

import org.crappbytes.biketracker.database.TrackDBHelper;
import org.crappbytes.biketracker.database.TrackTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TracksContentProvider extends ContentProvider {
	
	// database
	private TrackDBHelper db;
	
	// Used for the UriMatcher
	//Track table
	private static final int TRACK = 10;
	private static final int TRACK_ID = 20;
	//Nodes table
	private static final int NODES = 30;
	private static final int NODES_ID = 40;
	
	private static final String AUTHORITY = "org.crappbytes.biketracker.contentprovider";
	
	//Define the Uris
	private static final String PATH_TRACK = "tracks";
	private static final String PATH_NODES = "nodes";
	
	//Content uri
	public static final Uri CONTENT_URI_TRACK = Uri.parse("content://" + AUTHORITY
		    + "/" + PATH_TRACK);
	public static final Uri CONTENT_URI_NODES = Uri.parse("content://" + AUTHORITY
	        + "/" + PATH_NODES);
	
	//FIXME: Do we need to define CONTENT_TYPE --> RTFM
	
	//Setup UriMatcher, one Uri pair for each table
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
	  sURIMatcher.addURI(AUTHORITY, PATH_TRACK, TRACK);
	  sURIMatcher.addURI(AUTHORITY, PATH_TRACK + "/#", TRACK_ID);
	  sURIMatcher.addURI(AUTHORITY, PATH_NODES, NODES);
	  sURIMatcher.addURI(AUTHORITY, PATH_NODES + "/#", NODES_ID);
	}
	
	@Override
	public boolean onCreate() {
		this.db = new TrackDBHelper(getContext(), null, null, 0);
		return false; //FIXME: return false??!!
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
		SQLiteDatabase sqlDB = this.db.getWritableDatabase();
		sqlDB.execSQL("PRAGMA foreign_keys = ON;");
		long id = 0;
		switch (uriType) {
		case TRACK:
			id = sqlDB.insert(TrackTable.TABLE_NAME, null, values);
			returnUri = Uri.parse(PATH_TRACK + "/" + id);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}
	
	@Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
