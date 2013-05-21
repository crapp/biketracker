package org.crappbytes.biketracker.export;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public abstract class GeoExport {
	private String outFile;
	private Context appContext;
	//TODO: How do we get the data for the track to export? Own database query (is this possible with the passed context??), or pass in a cursor? 
	
	public abstract String buildXMLStructure();
	
	public GeoExport(Context context) {
		this.appContext = context;
	}
	
	private Boolean isExternalStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
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
