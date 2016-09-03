package org.crappbytes.biketracker;

import android.os.Bundle;
import android.app.Activity;

public class StatsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
