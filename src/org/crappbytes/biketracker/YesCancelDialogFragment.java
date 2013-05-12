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

package org.crappbytes.biketracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class YesCancelDialogFragment extends DialogFragment {
	
	
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface YesCancelDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int type);
        public void onDialogNegativeClick(DialogFragment dialog, int type);
    }
	
    public final static int DIALOG_GPS = 1;
    public final static int DIALOG_STOP_TRACKING = 2;
    
    // Instance of the interface to deliver action events
    private YesCancelDialogListener ycListener;
    
    //We override the onAttach Method where we instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	
    	try {
    		// Instantiate the TrackDialogListener that we use to notify the calling Activity
    		this.ycListener = (YesCancelDialogListener) activity;
    	}
    	catch(ClassCastException e) {
    		// The activity does not implement the interface
    		throw new ClassCastException(activity.toString() + 
    				 " must implement YesCancelDialogListener");
    	}
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
		//
		switch (getArguments().getInt("Type")) {
		case DIALOG_GPS:
			//set Title and message
		    builder.setTitle(R.string.noGPS);
		    builder.setMessage(R.string.noGPSmsg);
		    break;
		case DIALOG_STOP_TRACKING:
			//set Title and message
			builder.setTitle(R.string.stopTracking);
			builder.setMessage(R.string.stopTrackingMsg);
		}
		
	    //Add action buttons, you don't need to specify them in the layout file.
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    		   @Override
	    		   public void onClick(DialogInterface dialog, int id) {
	    			   ycListener.onDialogPositiveClick(YesCancelDialogFragment.this, getArguments().getInt("Type"));
	    		   }
	    	   })
	    	   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                	   ycListener.onDialogNegativeClick(YesCancelDialogFragment.this, getArguments().getInt("Type"));
                   }
               });
		return builder.create();
	}

}
