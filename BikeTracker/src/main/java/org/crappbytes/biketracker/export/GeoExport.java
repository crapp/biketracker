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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.crappbytes.biketracker.Node;

public abstract class GeoExport {
	private String outFile;
	private Context appContext;
    protected String trackName;
    protected ArrayList<Node> nodeList;
	//TODO: How do we get the data for the track to export? Own database query (is this possible with the passed context??), or pass in a cursor? 
	
	public abstract String buildXMLStructure();
	
	public GeoExport(Context context, String trackName, ArrayList<Node> nodeList) {
		this.appContext = context;
        this.trackName = trackName;
        this.nodeList = nodeList;
	}
	
	private Boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
	}
	
	public Boolean serializeDataToFile() {
		String xmlString = buildXMLStructure();
		if (isExternalStorageWriteable()) {
			File root = Environment.getExternalStorageDirectory();
			File bikeTrackerExport = new File(root.getAbsolutePath() + "/bikeTracker");
			if (!bikeTrackerExport.isDirectory() && !bikeTrackerExport.mkdirs()) {
				Toast.makeText(appContext, "Could not create BikeTracker directory", Toast.LENGTH_LONG).show();
			} else {
				try {
					File outFileObj = new File(bikeTrackerExport.getAbsolutePath() + "/" + outFile);
					FileOutputStream fos = new FileOutputStream(outFileObj);
					fos.write(xmlString.getBytes());
					fos.close();
				}
				catch(Exception ex) {
					Log.e(appContext.getPackageName(), ex.getStackTrace().toString());
				}
			}
		} else {
			Toast.makeText(appContext, "External Storage not mounted/writeable", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}
}
