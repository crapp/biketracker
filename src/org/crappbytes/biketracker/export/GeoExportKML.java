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

package org.crappbytes.biketracker.export;

import java.io.StringWriter;
import java.util.ArrayList;

import org.crappbytes.biketracker.Utility;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.crappbytes.biketracker.Node;

public class GeoExportKML extends GeoExport {

	public GeoExportKML(Context context, String outFile, String trackName, ArrayList<Node> nodeList) {
		//pass context to constructor of GeoExport abstract class
		super(context, trackName, nodeList);
		this.setOutFile(outFile);
	}

	@Override
	public String buildXMLStructure() {
		XmlSerializer serializer = Xml.newSerializer();
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        //set the default prefix "" to namespace
	        serializer.setPrefix("", "http://www.opengis.net/kml/2.2");
	        serializer.startTag("http://www.opengis.net/kml/2.2", "kml");
	        serializer.startTag("", "Document");
	        serializer.startTag("", "name");
	        serializer.text(this.trackName);
	        serializer.endTag("", "name");
            serializer.startTag("", "description");
            serializer.text("KML track created by BikeTracker");
            serializer.endTag("", "description");
            //style informations
            serializer.startTag("", "Style");
            serializer.attribute("", "id", "yellowLineGreenPoly");
            serializer.startTag("", "LineStyle");
            serializer.startTag("", "color");
            serializer.text("7f00ffff");
            serializer.endTag("", "color");
            serializer.startTag("", "width");
            serializer.text("4");
            serializer.endTag("", "width");
            serializer.endTag("", "LineStyle");
            serializer.startTag("", "PolyStyle");
            serializer.text("7f00ff00");
            serializer.endTag("", "PolyStyle");
            serializer.endTag("", "Style");
//            <Placemark>
//            <name>Absolute Extruded</name>
//            <description>Transparent green wall with yellow outlines</description>
//            <styleUrl>#yellowLineGreenPoly</styleUrl>
//            <LineString>
//            <extrude>1</extrude>
//            <tessellate>1</tessellate>
//            <altitudeMode>absolute</altitudeMode>
            serializer.startTag("", "Placemark");
            serializer.startTag("", "name");
            serializer.text(this.trackName);
            serializer.endTag("", "name");
            serializer.startTag("", "description");
            serializer.text("what a nice tour :)");
            serializer.endTag("", "description");
            serializer.startTag("", "styleUrl");
            serializer.text("#yellowLineGreenPoly");
            serializer.endTag("", "styleUrl");
            serializer.startTag("", "LineString");
            serializer.startTag("", "extrude");
            serializer.text("0");
            serializer.endTag("", "extrude");
            serializer.startTag("", "tessellate");
            serializer.text("0");
            serializer.endTag("", "tessellate");
            serializer.startTag("", "altitudeMode");
            serializer.text("absolute");
            serializer.endTag("", "altitudeMode");
            serializer.startTag("", "coordinates");
            //get all nodes from list and write coordinates + altitude
            for (Node n : this.nodeList) {
                serializer.text(String.valueOf(n.getLongitude()) + "," +
                        String.valueOf(n.getLatitude()) + "," +
                        String.valueOf((int)Utility.round(n.getAltitudelpf(), 1)) + "\n");
            }
            serializer.endTag("", "coordinates");
            serializer.endTag("", "LineString");
            serializer.endTag("", "Placemark");
	        serializer.endTag("", "Document");
	        serializer.endTag("http://www.opengis.net/kml/2.2", "kml");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception ex) {
	        Log.e(getAppContext().getPackageName(), ex.getStackTrace().toString());
	    }  
		return null;
	}
}
