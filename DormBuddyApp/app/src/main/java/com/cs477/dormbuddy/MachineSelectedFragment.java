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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MachineSelectedFragment extends DialogFragment {
    private static final String MACHINE_NAME = "name";
    private static final String MACHINE_STATUS = "status";
    private static final String MACHINE_CONDITION = "condition";
    private static final String MACHINE_TIME_LEFT = "time-left";
    private static final String MACHINE_IS_WASHER = "is-washer";
    private static final String MACHINE_ID = "_machine-id";
    String name;
    int id;

    public MachineSelectedFragment() {
        // Required empty public constructor
    }

    public static MachineSelectedFragment newInstance(String name, int id, int status, int condition, int timeLeft, boolean isWasher) {
        Bundle args = new Bundle();
        args.putString(MACHINE_NAME, name);
        args.putInt(MACHINE_ID, id);
        args.putInt(MACHINE_STATUS, status);
        args.putInt(MACHINE_CONDITION, condition);
        args.putInt(MACHINE_TIME_LEFT, timeLeft);
        args.putBoolean(MACHINE_IS_WASHER, isWasher);
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
        id = getArguments().getInt(MACHINE_ID);
        int status = getArguments().getInt(MACHINE_STATUS);
        int condition = getArguments().getInt(MACHINE_CONDITION);
        int timeLeft = getArguments().getInt(MACHINE_TIME_LEFT);
        boolean isWasher = getArguments().getBoolean(MACHINE_IS_WASHER);

        if (! isWasher) {
            ((ImageView)v.findViewById(R.id.machineSelectedImage)).setImageResource(R.drawable.dryer);
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
        builder.setPositiveButton("Reserve now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

}
