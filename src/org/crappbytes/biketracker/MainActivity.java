package org.crappbytes.biketracker;

import org.crappbytes.biketracker.TrackDialogFragment.TrackDialogListener;

import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements TrackDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(false);
        }
        
        Button butNewTrack = (Button) findViewById(R.id.butStartTracking);
        butNewTrack.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				DialogFragment trackDialog = new TrackDialogFragment();
				trackDialog.show(getFragmentManager(), "trackdialog");
				//Intent intNewTrack = new Intent(MainActivity.this, TrackActivity.class);
				//startActivity(intNewTrack);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
    
}
