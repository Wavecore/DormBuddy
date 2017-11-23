package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

/* using enums didnt work out as implicit int casting is not possible, much more work than needed
import com.cs477.dormbuddy.CycleSettingsActivity.WASHER_SOIL;
import com.cs477.dormbuddy.CycleSettingsActivity.WASHER_CYCLE;
import com.cs477.dormbuddy.CycleSettingsActivity.WASHER_TEMPERATURE;
import com.cs477.dormbuddy.CycleSettingsActivity.WASHER_SMALL_LOAD;
import com.cs477.dormbuddy.CycleSettingsActivity.DRYER_TEMPERATURE;
import com.cs477.dormbuddy.CycleSettingsActivity.DRYER_TEMPERATURE;
*/

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AddPreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPreferenceFragment extends DialogFragment {
    // private OnFragmentInteractionListener mListener;
    //all views with text + button group
    LinearLayout dryerTemp, delicates, soil, cycle, washerTemp, smallLoad;
    //the edit text
    EditText cycleNameEditText;
    //all radio buttons
    //starter ones
    RadioButton washer, dryer;
    //dryer
    RadioButton dryerHigh, dryerMedium, dryerLow, dryerNone, dryerYes, dryerNo;
    //washer
    RadioButton soilLight, soilMedium, soilHeavy, cycleNormal, cyclePermPress, cycleDelicates,
            washerHot, washerWarm, washerCool, washerYes, washerNo;
    RadioButton[] radioGaga;
    //THE ADD BUTTON
    Button addPreferenceButton;
    LinearLayout starters;
    //if washer is selected, make these visible and the others gone
    LinearLayout[] washerVisible;
    //if dryer is selected, make these visible and the others gone
    LinearLayout[] dryerVisible;
    View view; //shared view
    //what the user selects
    int dryerTemperature = 0, dryerDelicates = 0,
            washerSoil = 0, washerCycle = 0, washerTemperature = 0, washerSmallLoad = 0;
    boolean washerSelected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_add_preference,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //////////////////////////INITIALIZE EVERY SINGLE THING//////////////////////
        //edit text
        cycleNameEditText = view.findViewById(R.id.cycleNameEditText);
        //ALL VIEWS
        dryerTemp = view.findViewById(R.id.dryerTemp);delicates = view.findViewById(R.id.delicates);
        soil = view.findViewById(R.id.soil);
        cycle = view.findViewById(R.id.cycle);
        washerTemp = view.findViewById(R.id.washerTemp);
        smallLoad = view.findViewById(R.id.smallLoad);
        //STARTER RADIO BUTTONS
        washer = view.findViewById(R.id.washer);
        dryer = view.findViewById(R.id.dryer);
        //dryer
        dryerHigh = view.findViewById(R.id.dryerHigh);
        dryerMedium = view.findViewById(R.id.dryerMedium);
        dryerLow = view.findViewById(R.id.dryerLow);
        dryerNone = view.findViewById(R.id.dryerNone);
        dryerYes = view.findViewById(R.id.dryerYes);
        dryerNo = view.findViewById(R.id.dryerNo);
        //WASHER RADIO BUTTONS
        soilLight = view.findViewById(R.id.soilLight);
        soilMedium = view.findViewById(R.id.soilMedium);
        soilHeavy = view.findViewById(R.id.soilHeavy);
        cycleNormal = view.findViewById(R.id.cycleNormal);
        cyclePermPress = view.findViewById(R.id.cyclePermPress);
        cycleDelicates = view.findViewById(R.id.cycleDelicates);
        washerHot = view.findViewById(R.id.washerHot);
        washerWarm = view.findViewById(R.id.washerWarm);
        washerCool = view.findViewById(R.id.washerCool);
        washerYes = view.findViewById(R.id.washerYes);
        washerNo = view.findViewById(R.id.washerNo);
        ////////////////group all radio buttons/////////////
        radioGaga = new RadioButton[]{washer, dryer,
                dryerHigh, dryerMedium, dryerLow, dryerNone,
                dryerYes, dryerNo,
                soilLight, soilMedium, soilHeavy,
                cycleNormal, cyclePermPress, cycleDelicates,
                washerHot, washerWarm, washerCool,
                washerYes, washerNo};
        for (RadioButton radio : radioGaga) {
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioButtonClicked(view);
                }
            });
        }
        //THE ADD BUTTON
        addPreferenceButton = view.findViewById(R.id.addPreferenceButton);
        addPreferenceButton.setEnabled(false); //disable the button u cant add nothin yet
        addPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Preference " + cycleNameEditText.getText().toString() + " added!",
                        Toast.LENGTH_SHORT).show();
                dismiss(); //goodbye~
            }
        });
        //select a cycle
        starters = view.findViewById(R.id.laundryType);
        /////////////////////////////////////////////////////////////////////////////
        //hide layouts till they click washer or dryer
        washerVisible = new LinearLayout[]{soil, cycle, washerTemp, smallLoad};
        dryerVisible = new LinearLayout[]{dryerTemp, delicates};
        hideLayouts(washerVisible);
        hideLayouts(dryerVisible);
        builder.setView(view);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }



    //the longest method I have ever wrote, and the longest switch case
    public void radioButtonClicked(View button) {
        switch (button.getId()) {
            case R.id.washer:
                washerSelected = true;
                hideLayouts(dryerVisible);
                showLayouts(washerVisible);
                break;
            case R.id.dryer:
                washerSelected = false;
                hideLayouts(washerVisible);
                showLayouts(dryerVisible);
                break;
            case R.id.dryerHigh:
                dryerTemperature = 1;
                break;
            case R.id.dryerMedium:
                dryerTemperature = 2;
                break;
            case R.id.dryerLow:
                dryerTemperature = 3;
                break;
            case R.id.dryerNone:
                dryerTemperature = 4;
                break;
            case R.id.dryerNo:
                dryerDelicates = 1;
                break;
            case R.id.dryerYes:
                dryerDelicates = 2;
                break;
            case R.id.soilHeavy:
                washerSoil = 1;
                break;
            case R.id.soilMedium:
                washerSoil = 2;
                break;
            case R.id.soilLight:
                washerSoil = 3;
                break;
            case R.id.cycleNormal:
                washerCycle = 1;
                break;
            case R.id.cyclePermPress:
                washerCycle = 2;
                break;
            case R.id.cycleDelicates:
                washerCycle = 3;
                break;
            case R.id.washerHot:
                washerTemperature = 1;
                break;
            case R.id.washerWarm:
                washerTemperature = 2;
                break;
            case R.id.washerCool:
                washerTemperature = 3;
                break;
            case R.id.washerNo:
                washerSmallLoad = 1;
                break;
            case R.id.washerYes:
                washerSmallLoad = 2;
                break;
            default:
                Toast.makeText(getContext(), "Button Not Implemented", Toast.LENGTH_SHORT).show();
                break;
        }
        enableButton();
    }

    public void enableButton() {
        if (!(!washerSelected || (washerSoil > 0 && washerCycle > 0 && washerTemperature > 0 && washerSmallLoad > 0)) //washer
         || !(washerSelected || (dryerTemperature > 0 && dryerDelicates > 0))  //dryer is selected but temp & delicates <= 0
                || cycleNameEditText.getText().toString().isEmpty()) { //empty name
            if (addPreferenceButton.isEnabled())
                addPreferenceButton.setEnabled(false);;
            return;
        }
        if (addPreferenceButton.isEnabled())
            return;
        addPreferenceButton.setEnabled(true);
    }

    //method to hide layouts user should not see
    public void hideLayouts(LinearLayout[] hideThis) {
        for (LinearLayout l : hideThis) {
            l.setVisibility(View.GONE);
        }
    }
    
    //method to show layouts user should see
    public void showLayouts(LinearLayout[] showThis) {
        for (LinearLayout l : showThis) {
            l.setVisibility(View.VISIBLE);
        }
    }

    public AddPreferenceFragment() {
        // Required empty public constructor
    }


    public static AddPreferenceFragment newInstance() {
        return new AddPreferenceFragment();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
     */
}
