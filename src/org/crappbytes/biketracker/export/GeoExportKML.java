package org.crappbytes.biketracker.export;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class GeoExportKML extends GeoExport {

	public GeoExportKML(Context context, String outFile) {
		//pass context to constructor of GeoExport abstract class
		super(context);
		this.setOutFile(outFile);
	}

	@Override
	public String buildXMLStructure() {
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        //set the default prefix "" to namespace
	        serializer.setPrefix("", "http://www.opengis.net/kml/2.2");
	        serializer.startTag("http://www.opengis.net/kml/2.2", "kml");
	        serializer.startTag("", "document");
	        serializer.startTag("", "name");
	        serializer.text("");
	        serializer.endTag("", "name");
	        serializer.endTag("", "document");
	        serializer.endTag("http://www.opengis.net/kml/2.2", "kml");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception ex) {
	        Log.e(getAppContext().getPackageName(), ex.getStackTrace().toString());
	    }  
		return null;
	}
}
