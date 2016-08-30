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

package org.crappbytes.biketracker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by saedelaere on 05.06.13.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String PREF_ACCURACY  = "pref_accuracy";
    public static final String PREF_POLLTIME = "pref_polltime";
    public static final String PREF_LPF = "pref_lpf";
    public static final String PREF_MINDISTANCE = "pref_mindistance";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
