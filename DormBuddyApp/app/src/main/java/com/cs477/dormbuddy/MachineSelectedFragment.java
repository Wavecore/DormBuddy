package com.cs477.dormbuddy;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MachineSelectedFragment extends DialogFragment {
    private static final String MACHINE_NAME = "name";
    private static final String MACHINE_STATUS = "status";
    private static final String MACHINE_CONDITION = "condition";
    private static final String MACHINE_TIME_LEFT = "time-left";
    private static final String MACHINE_IS_WASHER = "is-washer";
    private static final String MY_MACHINE = "my-machine";
    private static final String MACHINE_ID = "_machine-id";
    String name;
    String id;

    public MachineSelectedFragment() {
        // Required empty public constructor
    }

    public static MachineSelectedFragment newInstance(String name, String id, int status, int condition, int timeLeft, boolean isWasher, boolean myMachine) {
        Bundle args = new Bundle();
        args.putString(MACHINE_NAME, name);
        args.putString(MACHINE_ID, id);
        args.putInt(MACHINE_STATUS, status);
        args.putInt(MACHINE_CONDITION, condition);
        args.putLong(MACHINE_TIME_LEFT, timeLeft);
        args.putBoolean(MACHINE_IS_WASHER, isWasher);
        args.putBoolean(MY_MACHINE, myMachine);
        MachineSelectedFragment fragment = new MachineSelectedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_machine_selected,null);

        name = getArguments().getString(MACHINE_NAME);
        id = getArguments().getString(MACHINE_ID);
        int status = getArguments().getInt(MACHINE_STATUS);
        int condition = getArguments().getInt(MACHINE_CONDITION);
        long timeLeft = getArguments().getLong(MACHINE_TIME_LEFT);
        boolean isWasher = getArguments().getBoolean(MACHINE_IS_WASHER);
        boolean myMachine = getArguments().getBoolean(MY_MACHINE);

        if (! isWasher) {
            ((ImageView)v.findViewById(R.id.machineSelectedImage)).setImageResource(R.drawable.dryer);
        }

        if (timeLeft > 0) {
            String timeLeftText = "" + timeLeft;
            v.findViewById(R.id.optionalTimerLayout).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.machineTimeLeftText)).setText(timeLeftText);
        }

        if (myMachine) {
            v.findViewById(R.id.myMachine).setVisibility(View.VISIBLE);
        }

        String statText = (status == 0 ) ? "Free" : (status == 1 ) ? "Busy" : "Reserved";
        String condText = (condition == 0 ) ? "Good" : (condition == 1 ) ? "Caution" : "Broken";

        if (isWasher) {
            ((TextView) v.findViewById(R.id.machineNumberText)).setText("WASHER " + name);
        } else {
            ((TextView) v.findViewById(R.id.machineNumberText)).setText("DRYER " + name);
        }
        ((TextView)v.findViewById(R.id.machineStatusText)).setText(statText);
        ((TextView)v.findViewById(R.id.machineConditionText)).setText(condText);

        builder.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        if (status == 0) { //only show reserve if free
            builder.setPositiveButton("Reserve now", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id2) {
                    /*
                     * Server code to reserve the machine by its ID
                     * final check if the reservation was successful
                     */
                    //updates the selected template
                    ReservationDoneListener activity = (ReservationDoneListener) getActivity();
                    //every machine gets reserved for 10 minutes so the student can come
                    activity.onReservationMade(id, 2);
                    dialog.cancel();
                }
            });
        } else if (status == 2 && myMachine) { //cancel reservation/start laundry button if reserved by current user
            builder.setNeutralButton("Cancel Reservation", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id2) {
                    ReservationDoneListener activity = (ReservationDoneListener) getActivity();
                    activity.onReservationMade(id, 0);
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Start Laundry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id2) {
                    /*
                     * Server code to reserve the machine by its ID
                     * final check if the reservation was successful
                     */
                    //updates the selected template
                    ReservationDoneListener activity = (ReservationDoneListener) getActivity();
                    activity.onReservationMade(id, 1);
                    dialog.cancel();
                }
            });
        }


        return builder.create();
    }

    public interface ReservationDoneListener {
        void onReservationMade(String id, int reservationType);
    }

}
