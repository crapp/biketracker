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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.crappbytes.biketracker.contentprovider.TracksContentProvider;
import org.crappbytes.biketracker.database.TrackTable;


public class TrackEditFragment extends DialogFragment {

    public static final String ARG_TRACK_ID = "arg_track_id";
    public static final String ARG_TRACK_NAME = "arg_track_name";

    private EditText trackNameEdit;

    public TrackEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        // super.onStart() is where dialog.show() is actually called on the underlying dialog,
        // so we have to do it after this point
        super.onStart();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (trackNameEdit.getText().toString().isEmpty()) {
                        // FIXME: This toast is not visible. Probably because we use an alertdialog?
                        // Toast.makeText(getActivity(), R.string.trackNameEmpty, Toast.LENGTH_SHORT);
                        return;
                    }
                    // Create ContentValues with new track name
                    ContentValues cv = new ContentValues();
                    cv.put(TrackTable.COLUMN_NAME, trackNameEdit.getText().toString());
                    // get our content resolver
                    ContentResolver res = getActivity().getContentResolver();
                    Bundle args = getArguments();
                    // update the track identified by id with the new name
                    Long track_id = args.getLong(TrackEditFragment.ARG_TRACK_ID);
                    res.update(Uri.withAppendedPath(
                            TracksContentProvider.CONTENT_URI_TRACK, track_id.toString()),
                            cv, null, null);
                    Boolean wantToCloseDialog = false;
                    d.dismiss();
                    // else dialog stays open
                }
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_track_edit, null);
        trackNameEdit = (EditText) view.findViewById(R.id.track_name_edit);
        Bundle args = getArguments();
        String trackName = args.getString(TrackEditFragment.ARG_TRACK_NAME);
        trackNameEdit.setText(trackName);

        builder.setTitle(R.string.trackEditTitle);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // http://stackoverflow.com/a/15619098/1127601
                        // Do nothing here because we override this button later to change the close
                        // behaviour. However, we still need this because on older versions of
                        // Android unless we pass a handler the button doesn't get instantiated
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TrackEditFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
