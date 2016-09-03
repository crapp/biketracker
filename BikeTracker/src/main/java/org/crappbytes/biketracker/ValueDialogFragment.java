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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackNodesTable;

/**
 * Created by Christian Rapp on 08.06.13.
 */

public class ValueDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARGS_TID = "argsTrackID";
    public static final String ARGS_VALUE_TYPE = "track_value_type";
    public static final int ALTITUDE = 10;
    public static final int SPEED = 20;
    private int trackDialogValType;

    private View builderView = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        this.trackDialogValType = args.getInt(ValueDialogFragment.ARGS_VALUE_TYPE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        switch (this.trackDialogValType) {
            case ValueDialogFragment.ALTITUDE:
                this.builderView = inflater.inflate(R.layout.fragment_values_dialog_altitude, null);
                getLoaderManager().initLoader(ALTITUDE, null, this);
                break;
            case ValueDialogFragment.SPEED:
                this.builderView = inflater.inflate(R.layout.fragment_values_dialog_speed, null);
                getLoaderManager().initLoader(SPEED, null, this);
                break;
        }

        builder.setView(this.builderView);

        builder.setPositiveButton("Ok", null);

        return builder.create();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        Bundle gargs = getArguments();
        long trackID = gargs.getLong(ARGS_TID);

        String projection[] = null;

        switch (id) {
            case ALTITUDE:
                projection = new String[]{
                        "MAX(" + TrackNodesTable.COLUMN_ALTITUDELPF +
                                ") AS max_" + TrackNodesTable.COLUMN_ALTITUDELPF,
                        "MIN(" + TrackNodesTable.COLUMN_ALTITUDELPF +
                                ") AS min_" + TrackNodesTable.COLUMN_ALTITUDELPF,
                        "SUM(" + TrackNodesTable.COLUMN_ALTITUDEUP +
                                ") AS sum_" + TrackNodesTable.COLUMN_ALTITUDEUP,
                        "SUM(" + TrackNodesTable.COLUMN_ALTITUDEDOWN +
                                ") AS sum_" + TrackNodesTable.COLUMN_ALTITUDEDOWN
                };
                break;
            case SPEED:
                projection = new String[]{
                        "MIN(" + TrackNodesTable.COLUMN_SPEED +
                                ") AS min_" + TrackNodesTable.COLUMN_SPEED,
                        "MAX(" + TrackNodesTable.COLUMN_SPEED +
                                ") AS max_" + TrackNodesTable.COLUMN_SPEED,
                        "AVG(" + TrackNodesTable.COLUMN_SPEED +
                                ") AS avg_" + TrackNodesTable.COLUMN_SPEED
                };
                break;
        }
        loader = new CursorLoader(
                getActivity(),
                TracksContentProvider.CONTENT_URI_NODES,
                projection,
                TrackNodesTable.COLUMN_TRACKID + "=?",
                new String[]{String.valueOf(trackID)},
                null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            switch (loader.getId()) {
                case ALTITUDE:
                    data.moveToFirst();
                    Double max_altitudelpf;
                    Double min_altitudelpf;
                    Double ascend;
                    Double descend;

                    ascend = Utility.round(data.getDouble(data.getColumnIndex(
                            "sum_" + TrackNodesTable.COLUMN_ALTITUDEUP)), 0);
                    descend = Utility.round(data.getDouble(data.getColumnIndex(
                            "sum_" + TrackNodesTable.COLUMN_ALTITUDEDOWN)), 0);
                    max_altitudelpf = Utility.round(data.getDouble(data.getColumnIndex(
                            "max_" + TrackNodesTable.COLUMN_ALTITUDELPF)), 0);
                    min_altitudelpf = Utility.round(data.getDouble(data.getColumnIndex(
                            "min_" + TrackNodesTable.COLUMN_ALTITUDELPF)), 0);

                    if (this.builderView != null) {
                        ((TextView) this.builderView.findViewById(R.id.value_altitude_ascendvalue)).setText(
                                String.valueOf(ascend.intValue()));
                        ((TextView) this.builderView.findViewById(R.id.value_altitude_descendvalue)).setText(
                                String.valueOf(descend.intValue()));
                        ((TextView) this.builderView.findViewById(R.id.value_altitude_maxvalue)).setText(
                                String.valueOf(max_altitudelpf.intValue()));
                        ((TextView) this.builderView.findViewById(R.id.value_altitude_minvalue)).setText(
                                String.valueOf(min_altitudelpf.intValue()));
                    }
                    break;
                case SPEED:
                    data.moveToFirst();
                    Double speedMin;
                    Double speedMax;
                    Double speedAvg;

                    speedMin = Utility.round(Utility.convertSpeed(
                            data.getDouble(data.getColumnIndex(
                                    "min_" + TrackNodesTable.COLUMN_SPEED))), 1);
                    speedMax = Utility.round(Utility.convertSpeed(
                            data.getDouble(data.getColumnIndex(
                                    "max_" + TrackNodesTable.COLUMN_SPEED))), 1);
                    speedAvg = Utility.round(Utility.convertSpeed(
                            data.getDouble(data.getColumnIndex(
                                    "avg_" + TrackNodesTable.COLUMN_SPEED))), 1);

                    if (this.builderView != null) {
                        ((TextView) this.builderView.findViewById(R.id.value_speed_minvalue)).setText(
                                String.valueOf(speedMin));
                        ((TextView) this.builderView.findViewById(R.id.value_speed_maxvalue)).setText(
                                String.valueOf(speedMax));
                        ((TextView) this.builderView.findViewById(R.id.value_speed_average)).setText(
                                String.valueOf(speedAvg));
                    }
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // we are'nt using an adapter so nothing to do here
    }
}