package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.cs477.dormbuddy.LocalUserHelper.DRYER_TEMPLATE_1;
import static com.cs477.dormbuddy.LocalUserHelper.DRYER_TEMPLATE_2;
import static com.cs477.dormbuddy.LocalUserHelper.DRYER_TEMPLATE_3;
import static com.cs477.dormbuddy.LocalUserHelper.DRYER_TEMPLATE_4;
import static com.cs477.dormbuddy.LocalUserHelper.DRYER_TEMPLATE_5;
import static com.cs477.dormbuddy.LocalUserHelper.LOGGED_IN;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_DRYER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_WASHER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_NAME;
import static com.cs477.dormbuddy.LocalUserHelper.WASHER_TEMPLATE_1;
import static com.cs477.dormbuddy.LocalUserHelper.WASHER_TEMPLATE_2;
import static com.cs477.dormbuddy.LocalUserHelper.WASHER_TEMPLATE_3;
import static com.cs477.dormbuddy.LocalUserHelper.WASHER_TEMPLATE_4;
import static com.cs477.dormbuddy.LocalUserHelper.WASHER_TEMPLATE_5;
import static com.cs477.dormbuddy.LocalUserHelper._ID;

public class CycleTemplatesActivity extends AppCompatActivity implements AddTemplateFragment.AddTemplateDoneListener {
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursor;
    final static String[] columns = {_ID, LOGGED_IN,
            SELECTED_WASHER_TEMPLATE, SELECTED_DRYER_TEMPLATE, //selected washer/dryer are now ints for more accuracy
            WASHER_TEMPLATE_1, DRYER_TEMPLATE_1,
            WASHER_TEMPLATE_2, DRYER_TEMPLATE_2,
            WASHER_TEMPLATE_3, DRYER_TEMPLATE_3,
            WASHER_TEMPLATE_4, DRYER_TEMPLATE_4,
            WASHER_TEMPLATE_5, DRYER_TEMPLATE_5
    };
    public final String[] washerSoils = {"Heavy", "Medium", "Light"};
    public final String[] washerCycles = {"Normal", "Perm Press", "Delicates"};
    public final String[] washerTemperatures = {"Hot", "Warm", "Cool"};
    public final String[] washerExtras = {"None", "Small Load"};
    public final String[] dryerTemperatures = {"High Heat", "Medium Heat", "Low Heat", "No Heat"};
    public final String[] dryerExtras = {"None", "Delicates"};
    TextView selectedWasherTemplate, selectedDryerTemplate;
    int storedGNumber, selectedWasher, selectedDryer;
    RecyclerView washerTemplateView, dryerTemplateView;
    ArrayList<String> washerTemplateList, dryerTemplateList;
    TemplateAdapter washerTemplateAdapter, dryerTemplateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_templates);
        //check if user is logged in first, redirect them to log in if not
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursor = db.query(TABLE_NAME, columns, null, new String[] {}, null, null,
                null);
        /////////
        //try and check if user is logged in, storing g number
        try {
            mCursor.moveToPosition(0);
            if (mCursor.getInt(1) == 0) {
                //user is not logged in, redirect to log in screen
                startActivity(new Intent(this, CredentialsActivity.class));
                finish();
                return;
            }
            storedGNumber = mCursor.getInt(0);

        } catch (Exception e) {
            startActivity(new Intent(this, CredentialsActivity.class));
            finish();
            return;
        }
        ////////////////////INITIALIZE ELEMENTS////////////////////
        selectedWasherTemplate = findViewById(R.id.selectedWasherTemplate);
        selectedDryerTemplate = findViewById(R.id.selectedDryerTemplate);
        washerTemplateView = findViewById(R.id.washerTemplates);
        dryerTemplateView = findViewById(R.id.dryerTemplates);
        washerTemplateList = new ArrayList<String>();
        dryerTemplateList = new ArrayList<String>();
        washerTemplateAdapter = new TemplateAdapter(washerTemplateList, true);
        dryerTemplateAdapter = new TemplateAdapter(dryerTemplateList, false);
        washerTemplateView.setAdapter(washerTemplateAdapter);
        dryerTemplateView.setAdapter(dryerTemplateAdapter);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        ///////////////////FILL UP ELEMENTS////////////////////////
        selectedWasher = mCursor.getInt(2);
        selectedDryer = mCursor.getInt(3);
        if (selectedWasher > 0) {
            String selectedWasherTemplateString = mCursor.getString(selectedWasher);
            selectedWasherTemplateString = selectedWasherTemplateString.substring(0,selectedWasherTemplateString.length()-4);
            selectedWasherTemplate.setText(selectedWasherTemplateString); //replaces with name of template
        }
        if (selectedDryer > 0){
            String selectedDryerTemplateString = mCursor.getString(selectedDryer);
            selectedDryerTemplateString = selectedDryerTemplateString.substring(0,selectedDryerTemplateString.length()-2);
            selectedDryerTemplate.setText(selectedDryerTemplateString);
        }
        retrieveTemplates(); //retrieve all templates
        washerTemplateView.setLayoutManager(verticalLayout);
        dryerTemplateView.setLayoutManager(verticalLayout2);
    }

    private void retrieveTemplates() {
        washerTemplateList.clear();
        dryerTemplateList.clear();
        //columns 3+ are the templates
        for (int i = 0; i < 5; i++) {
            String washerTemplateString = mCursor.getString(2*i+4);
            String dryerTemplateString = mCursor.getString(2*i+5);
            if (!washerTemplateString.equals("")) {
                washerTemplateList.add(washerTemplateString);
            }
            if (!dryerTemplateString.equals("")) {
                dryerTemplateList.add(dryerTemplateString);
                dryerTemplateAdapter.notifyDataSetChanged();
            }
        }
        washerTemplateAdapter.notifyDataSetChanged();
        dryerTemplateAdapter.notifyDataSetChanged();
    }

    public void onAddTemplateClicked(View v) {
        AddTemplateFragment newFragment = AddTemplateFragment.newInstance();
        newFragment.show(getSupportFragmentManager(),"AddTemplateFragment");
    }

    //handler for add template returning
    @Override
    public void onTemplateAdded(String templateName, boolean isWasher) {
        String actualName;
        if (isWasher) {
            actualName = insertTemplateToLocalServer(templateName, isWasher);
            Toast.makeText(this, "Washer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
            selectedWasherTemplate.setText(actualName); //replaces with name of template
        } else {
            actualName = insertTemplateToLocalServer(templateName, isWasher);
            Toast.makeText(this, "Dryer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
            selectedDryerTemplate.setText(actualName); //replaces with name of template
        }
    }

    private String insertTemplateToLocalServer(String templateName, boolean isWasher) {
        /////////////////////////////////Extracts template name//////////
        String actualName = (isWasher) ?
            templateName.substring(0, templateName.length() - 4) : //washers have 4 additional chars
            templateName.substring(0, templateName.length() - 2); //dryers have 2
        /////////////////////////inserts template onto appropriate column////////////////////
        int columnNumber = insertInDatabase(templateName, isWasher);
        if (isWasher) {
            Toast.makeText(this, "Washer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Dryer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
        }
        return actualName;
    }

    private int insertInDatabase(String templateString, boolean isWasher) {
        String currentMachineName;
        ContentValues cv = new ContentValues(1);
        int laundryMachine0 = (isWasher) ? 4 : 5;
        for (int i = 0; i < 5; i++) {
            currentMachineName = mCursor.getString(laundryMachine0 + 2*i);
            if (currentMachineName.equals("") || i == 4) { //always replace the last data set, everyone hates the bottom
                cv.put(columns[laundryMachine0 + 2*i], templateString);
                db.update(TABLE_NAME, cv, _ID + "=" + storedGNumber, null); //updates the database
                if (isWasher) {
                    if (washerTemplateList.size() == 5)
                        washerTemplateList.remove(washerTemplateList.size()-1);
                    washerTemplateList.add(templateString);
                    washerTemplateAdapter.notifyDataSetChanged();
                    selectedWasherTemplate.setText(currentMachineName);
                } else {
                    if (dryerTemplateList.size() == 5)
                        dryerTemplateList.remove(dryerTemplateList.size()-1);
                    dryerTemplateList.add(templateString);
                    dryerTemplateAdapter.notifyDataSetChanged();
                    selectedDryerTemplate.setText(currentMachineName);
                }
                return laundryMachine0 + 2*i; //this is the column number
            }
        }
        return -1;
    }

    public void updateSelected(int position, boolean isWasher) {
        String COLUMN_NAME = (isWasher) ? SELECTED_WASHER_TEMPLATE : SELECTED_DRYER_TEMPLATE;
        TemplateAdapter adapter = (isWasher) ? washerTemplateAdapter : dryerTemplateAdapter;
        TextView selectedTemplate = (isWasher) ? selectedWasherTemplate : selectedDryerTemplate;
        int laundryMachine0 = (isWasher) ? 4 : 5;
        int columnNumber = laundryMachine0 + position*2;
        if (isWasher) {
            selectedWasher = columnNumber;
            washerTemplateAdapter.notifyDataSetChanged();
        } else {
            selectedDryer = columnNumber;
            dryerTemplateAdapter.notifyDataSetChanged();
        }
        String actualName = adapter.getName(position);
        ContentValues cv = new ContentValues(1);
        cv.put(COLUMN_NAME, columnNumber);

        db.update(TABLE_NAME, cv, _ID + "=" + storedGNumber, null); //updates the database
        selectedTemplate.setText(actualName);
    }

    //holder for template elements
    class TemplateHolder extends RecyclerView.ViewHolder {
        TextView washerSoil;
        TextView washerCycle;
        TextView washerTemp;
        TextView washerExtra;
        TextView dryerTemp;
        TextView dryerExtra;
        TextView templateName;
        View v;

        TemplateHolder(View v, final boolean isWasher) {
            super(v);
            this.v = v;
            if (isWasher) {
                templateName = (TextView) v.findViewById(R.id.washerTemplateName);
                washerSoil = (TextView) v.findViewById(R.id.washerSoil);
                washerCycle = (TextView) v.findViewById(R.id.washerCycle);
                washerTemp = (TextView) v.findViewById(R.id.washerTemp);
                washerExtra = (TextView) v.findViewById(R.id.washerExtra);
            } else {
                templateName = (TextView) v.findViewById(R.id.dryerTemplateName);
                dryerTemp = (TextView) v.findViewById(R.id.dryerTemp);
                dryerExtra = (TextView) v.findViewById(R.id.dryerExtra);
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelected(getLayoutPosition(), isWasher);
                    String washerOrDryer = (isWasher) ? "washer" : "dryer";
                    TemplateAdapter adapter = (isWasher) ? washerTemplateAdapter : dryerTemplateAdapter;
                    AlertDialog alertDialog = new AlertDialog.Builder(CycleTemplatesActivity.this).create();
                    alertDialog.setTitle("Current Template");
                    alertDialog.setMessage("Your current " + washerOrDryer + " template is now " +
                            adapter.getName(getLayoutPosition()) + ".");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(CycleTemplatesActivity.this, "Template item long clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    class TemplateAdapter extends RecyclerView.Adapter<TemplateHolder> {
        private boolean isWasher;
        private ArrayList<String> templates;

        TemplateAdapter(ArrayList<String> data, boolean isWasher) {
            this.isWasher = isWasher;
            templates = data;
        }
        @Override
        public TemplateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout;
            if (isWasher) {
                layout = R.layout.washer_template_item;
            } else {
                layout = R.layout.dryer_template_item;
            }
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new TemplateHolder(view, isWasher);
        }
        @Override
        public void onBindViewHolder(final TemplateHolder holder, int position) {
            String fullName = templates.get(position);
            String[] nameAndSettings = retrieveActualNameAndSettings(fullName, isWasher);
            String actualName = nameAndSettings[0];
            String settings = nameAndSettings[1];
            if ((isWasher && position*2+4 == selectedWasher) || (!isWasher && position*2+5 == selectedDryer)) {
                holder.v.setBackgroundColor(Color.parseColor("#00ff00"));
            } else {
                holder.v.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            if (isWasher) {
                holder.washerSoil.setText(washerSoils[Character.getNumericValue(settings.charAt(0))-1]);
                holder.washerCycle.setText(washerCycles[Character.getNumericValue(settings.charAt(1))-1]);
                holder.washerTemp.setText(washerTemperatures[Character.getNumericValue(settings.charAt(2))-1]);
                holder.washerExtra.setText(washerExtras[Character.getNumericValue(settings.charAt(3))-1]);
            } else {
                holder.dryerTemp.setText(dryerTemperatures[Character.getNumericValue(settings.charAt(0))-1]);
                holder.dryerExtra.setText(dryerExtras[Character.getNumericValue(settings.charAt(1))-1]);
            }
            holder.templateName.setText(actualName);
        }
        @Override
        public int getItemCount() {
            return templates.size();
        }

        public String getName(int position) { return retrieveActualNameAndSettings(templates.get(position), isWasher)[0]; }

        private String[] retrieveActualNameAndSettings(String templateName, boolean isWasher) {
            String actualName = (isWasher) ?
                    templateName.substring(0, templateName.length() - 4) : //washers have 4 additional chars
                    templateName.substring(0, templateName.length() - 2); //dryers have 2
            String templateSettings = (isWasher) ?
                    templateName.substring(templateName.length()-4) :
                    templateName.substring(templateName.length()-2);
            return new String[]{actualName, templateSettings};
        }
    }
}
