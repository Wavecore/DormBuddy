package com.cs477.dormbuddy;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import static com.cs477.dormbuddy.LocalUserHelper.TEMPLATE_ID;
import static com.cs477.dormbuddy.LocalUserHelper.TEMPLATE_IS_WASHER;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTemplateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTemplateFragment extends DialogFragment {
    private static final String TEMPLATE_STRING = "template_string";
    private static final String POSITION = "position";
    // private OnFragmentInteractionListener mListener;
    //all views with text + button group
    LinearLayout dryerTemp, delicates, soil, cycle, washerTemp, smallLoad;
    //the edit text
    EditText cycleNameEditText;
    //all radio buttons
    //dryer
    RadioButton dryerHigh, dryerMedium, dryerLow, dryerNone, dryerYes, dryerNo;
    //washer
    RadioButton soilLight, soilMedium, soilHeavy, cycleNormal, cyclePermPress, cycleDelicates,
            washerHot, washerWarm, washerCool, washerYes, washerNo;
    RadioButton[] radioGaga;
    //THE ADD BUTTON
    Button editTemplateButton;
    LinearLayout starters;
    //if washer is selected, make these visible and the others gone
    LinearLayout[] washerVisible;
    //if dryer is selected, make these visible and the others gone
    LinearLayout[] dryerVisible;
    View view; //shared view
    //what the user selects
    int dryerTemperature = 0, dryerDelicates = 0,
            washerSoil = 0, washerCycle = 0, washerTemperature = 0, washerSmallLoad = 0;
    //NEW and revised fields
    boolean washerSelected;
    int position;
    String templateName;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_edit_template,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ///////////////////////////////RETRIEVE PASSED IN ARGUMENTS////////////////
        templateName = getArguments().getString(TEMPLATE_STRING);
        position = getArguments().getInt(POSITION);
        washerSelected = getArguments().getBoolean(TEMPLATE_IS_WASHER);
        //////////////////////////INITIALIZE EVERY SINGLE THING//////////////////////
        //edit text
        cycleNameEditText = view.findViewById(R.id.cycleNameEditText);
        cycleNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        //ALL VIEWS
        dryerTemp = view.findViewById(R.id.dryerTemp);delicates = view.findViewById(R.id.delicates);
        soil = view.findViewById(R.id.soil);
        cycle = view.findViewById(R.id.cycle);
        washerTemp = view.findViewById(R.id.washerTemp);
        smallLoad = view.findViewById(R.id.smallLoad);
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
        radioGaga = new RadioButton[]{
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
        editTemplateButton = view.findViewById(R.id.editTemplateButton);
        editTemplateButton.setEnabled(false); //disable the button u cant edit nothin yet
        editTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updates the selected template
                EditTemplateFragment.EditTemplateDoneListener activity = (EditTemplateFragment.EditTemplateDoneListener) getActivity();
                String nameWithSettings = cycleNameEditText.getText().toString().trim();
                //simply concatenates the settings at the end of the name to be stored in the database
                if (washerSelected) {
                    nameWithSettings += "" + washerSoil + washerCycle + washerTemperature + washerSmallLoad;
                } else {
                    nameWithSettings += "" + dryerTemperature + dryerDelicates;
                }
                activity.onTemplateReplaced(nameWithSettings, position, washerSelected); //lets the parent know that we're done
                dismiss(); //goodbye~
            }
        });
        //select a cycle
        starters = view.findViewById(R.id.laundryType);
        /////////////////////////////////////////////////////////////////////////////
        //hide layouts till they click washer or dryer
        washerVisible = new LinearLayout[]{soil, cycle, washerTemp, smallLoad};
        dryerVisible = new LinearLayout[]{dryerTemp, delicates};
        if (washerSelected) {
            hideLayouts(dryerVisible);
        } else {
            hideLayouts(washerVisible);
        }
        builder.setView(view);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        magicFillAndSelect(view); //THE MAGIC behind Edit Template
        return builder.create();
    }



    //the longest method I have ever wrote, and the longest switch case
    public void radioButtonClicked(View button) {
        switch (button.getId()) {
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
        if ((washerSelected && (washerSoil == 0 || washerCycle == 0 || washerTemperature == 0 || washerSmallLoad == 0)) //washer
                || (!washerSelected && (dryerTemperature == 0 || dryerDelicates == 0))  //dryer is selected but temp & delicates <= 0
                || cycleNameEditText.getText().toString().trim().isEmpty()) { //empty name
            if (editTemplateButton.isEnabled())
                editTemplateButton.setEnabled(false);;
            return;
        }
        if (editTemplateButton.isEnabled())
            return;
        editTemplateButton.setEnabled(true);
    }

    //method to hide layouts user should not see
    public void hideLayouts(LinearLayout[] hideThis) {
        for (LinearLayout l : hideThis) {
            l.setVisibility(View.GONE);
        }
    }

    public EditTemplateFragment() {
        // Required empty public constructor
    }


    public static EditTemplateFragment newInstance(String templateString, int position, boolean isWasher) {
        EditTemplateFragment instance = new EditTemplateFragment();
        Bundle args = new Bundle();
        args.putString(TEMPLATE_STRING, templateString);
        args.putInt(POSITION, position);
        args.putBoolean(TEMPLATE_IS_WASHER, isWasher);
        instance.setArguments(args);
        return instance;
    }

    private void magicFillAndSelect(View view) {
        String[] nameAndSettings = retrieveActualNameAndSettings(templateName, washerSelected);
        cycleNameEditText.setText(nameAndSettings[0]);
        String settings = nameAndSettings[1];
        //does the opposite of selecting a radio button
        RadioButton buttonToCheck;
        if (washerSelected) {
            switch (Character.getNumericValue(settings.charAt(0))) {
                case 1:
                    washerSoil = 1;
                    buttonToCheck = view.findViewById(R.id.soilHeavy);
                    break;
                case 2:
                    washerSoil = 2;
                    buttonToCheck = view.findViewById(R.id.soilMedium);
                    break;
                case 3:
                    washerSoil = 3;
                    buttonToCheck = view.findViewById(R.id.soilLight);
                    break;
                default:
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    buttonToCheck = view.findViewById(R.id.soilLight);
                    break;
            }
            buttonToCheck.setChecked(true);
            switch (Character.getNumericValue(settings.charAt(1))) {
                case 1:
                    washerCycle = 1;
                    buttonToCheck = view.findViewById(R.id.cycleNormal);
                    break;
                case 2:
                    washerCycle = 2;
                    buttonToCheck = view.findViewById(R.id.cyclePermPress);
                    break;
                case 3:
                    washerCycle = 3;
                    buttonToCheck = view.findViewById(R.id.cycleDelicates);
                    break;
                default:
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    break;
            }
            buttonToCheck.setChecked(true);
            switch (Character.getNumericValue(settings.charAt(2))) {
                case 1:
                    washerTemperature = 1;
                    buttonToCheck = view.findViewById(R.id.washerHot);
                    break;
                case 2:
                    washerTemperature = 2;
                    buttonToCheck = view.findViewById(R.id.washerWarm);
                    break;
                case 3:
                    washerTemperature = 3;
                    buttonToCheck = view.findViewById(R.id.washerCool);
                    break;
                default:
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    break;
            }
            buttonToCheck.setChecked(true);
            switch (Character.getNumericValue(settings.charAt(3))) {
                case 1:
                    washerSmallLoad = 1;
                    buttonToCheck = view.findViewById(R.id.washerNo);
                    break;
                case 2:
                    washerSmallLoad = 2;
                    buttonToCheck = view.findViewById(R.id.washerYes);
                    break;
                default:
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    break;
            }
            buttonToCheck.setChecked(true);
        }
        else { //DRYERS: Temperature and Extras
            switch (Character.getNumericValue(settings.charAt(0))) {
                case 1:
                    dryerTemperature = 1;
                    buttonToCheck = view.findViewById(R.id.dryerHigh);
                    break;
                case 2:
                    dryerTemperature = 2;
                    buttonToCheck = view.findViewById(R.id.dryerMedium);
                    break;
                case 3:
                    dryerTemperature = 3;
                    buttonToCheck = view.findViewById(R.id.dryerLow);
                    break;
                case 4:
                    dryerTemperature = 4;
                    buttonToCheck = view.findViewById(R.id.dryerNone);
                    break;
                default:
                    buttonToCheck = view.findViewById(R.id.soilHeavy);
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    break;
            }
            buttonToCheck.setChecked(true);
            switch (Character.getNumericValue(settings.charAt(1))) {
                case 1:
                    dryerDelicates = 1;
                    buttonToCheck = view.findViewById(R.id.dryerNo);
                    break;
                case 2:
                    dryerDelicates = 2;
                    buttonToCheck = view.findViewById(R.id.dryerYes);
                    break;
                default:
                    Toast.makeText(getContext(), "A digit was unexpected", Toast.LENGTH_SHORT).show();
                    break;
            }
            buttonToCheck.setChecked(true);

            enableButton();
        }
    }

    private String[] retrieveActualNameAndSettings(String templateName, boolean isWasher) {
        String actualName = (isWasher) ?
                templateName.substring(0, templateName.length() - 4) : //washers have 4 additional chars
                templateName.substring(0, templateName.length() - 2); //dryers have 2
        String templateSettings = (isWasher) ?
                templateName.substring(templateName.length()-4) :
                templateName.substring(templateName.length()-2);
        return new String[]{actualName, templateSettings};
    }

    public interface EditTemplateDoneListener {
        void onTemplateReplaced(String newTemplateString, int position, boolean isWasher);
    }
    
}
