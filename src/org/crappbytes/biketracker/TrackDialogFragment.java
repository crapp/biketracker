package org.crappbytes.biketracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class TrackDialogFragment extends DialogFragment {
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface TrackDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
    // Instance of the interface to deliver action events
    private TrackDialogListener tdListener;
    
    public EditText newTrackName;
    
    //We override the onAttach Method where we instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	
    	try {
    		// Instantiate the TrackDialogListener that we use to notify the calling Activity
    		this.tdListener = (TrackDialogListener) activity;
    	}
    	catch(ClassCastException e) {
    		// The activity does not implement the interface
    		throw new ClassCastException(activity.toString() + 
    				 " must implement TrackDialogListener");
    	}
    }
    
	@SuppressLint("SimpleDateFormat")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View builderView = inflater.inflate(R.layout.dialog_trackname, null);
	    builder.setView(builderView);
	    
	    //Add action buttons, you don't need to specify them in the layout file.
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    		   @Override
	    		   public void onClick(DialogInterface dialog, int id) {
	    			   tdListener.onDialogPositiveClick(TrackDialogFragment.this);
	    		   }
	    	   })
	    	   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                	   tdListener.onDialogNegativeClick(TrackDialogFragment.this);
                   }
               });
	    newTrackName = (EditText)builderView.findViewById(R.id.nameNewTrack);
	    //Use a GregorianCalendar to get Date and Time
	    Calendar cal = new GregorianCalendar();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:MM"); 
	    newTrackName.setText("biketrack_" + dateFormat.format(cal.getTime()));
		return builder.create();
	}
}
