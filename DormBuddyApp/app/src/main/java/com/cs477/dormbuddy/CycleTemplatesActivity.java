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

import static com.cs477.dormbuddy.LocalUserHelper.TABLE_TEMPLATES;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_DRYER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_WASHER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;
import static com.cs477.dormbuddy.LocalUserHelper.TEMPLATE_ID;
import static com.cs477.dormbuddy.LocalUserHelper.TEMPLATE_IS_WASHER;
import static com.cs477.dormbuddy.LocalUserHelper.TEMPLATE_NAME;

public class CycleTemplatesActivity extends AppCompatActivity implements AddTemplateFragment.AddTemplateDoneListener, EditTemplateFragment.EditTemplateDoneListener {
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursorUser, mCursorTemplates;
    final static String[] columnsUser = {SELECTED_WASHER_TEMPLATE, SELECTED_DRYER_TEMPLATE};

    final static String[] columnsTemplate = {TEMPLATE_ID, TEMPLATE_NAME, TEMPLATE_IS_WASHER};
    public final String[] washerSoils = {"Heavy", "Medium", "Light"};
    public final String[] washerCycles = {"Normal", "Perm Press", "Delicates"};
    public final String[] washerTemperatures = {"Hot", "Warm", "Cool"};
    public final String[] washerExtras = {"None", "Small Load"};
    public final String[] dryerTemperatures = {"High Heat", "Medium Heat", "Low Heat", "No Heat"};
    public final String[] dryerExtras = {"None", "Delicates"};
    TextView selectedWasherTemplate, selectedDryerTemplate;
    int selectedWasher, selectedDryer;
    RecyclerView washerTemplateView, dryerTemplateView;
    ArrayList<MachineTemplate> washerTemplates, dryerTemplates;
    TemplateAdapter washerTemplateAdapter, dryerTemplateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_templates);
        //check if user is logged in first, redirect them to log in if not
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursorUser = db.query(TABLE_USER, columnsUser, null, new String[] {}, null, null,
                null);
        mCursorTemplates =  db.query(TABLE_TEMPLATES, columnsTemplate, null, new String[] {}, null, null,
                null);
        ////////////////////INITIALIZE ELEMENTS////////////////////
        selectedWasherTemplate = findViewById(R.id.selectedWasherTemplate);
        selectedDryerTemplate = findViewById(R.id.selectedDryerTemplate);
        washerTemplateView = findViewById(R.id.washerTemplates);
        dryerTemplateView = findViewById(R.id.dryerTemplates);
        washerTemplates = new ArrayList<MachineTemplate>();
        dryerTemplates = new ArrayList<MachineTemplate>();
        washerTemplateAdapter = new TemplateAdapter(washerTemplates, true);
        dryerTemplateAdapter = new TemplateAdapter(dryerTemplates, false);
        washerTemplateView.setAdapter(washerTemplateAdapter);
        dryerTemplateView.setAdapter(dryerTemplateAdapter);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this);
        verticalLayout.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager verticalLayout2 = new LinearLayoutManager(this);
        verticalLayout2.setOrientation(LinearLayoutManager.VERTICAL);
        ///////////////////FILL UP ELEMENTS////////////////////////
        mCursorUser.moveToFirst();
        mCursorTemplates.moveToFirst();
        selectedWasher = mCursorUser.getInt(0);
        selectedDryer = mCursorUser.getInt(1);
        System.out.println(selectedWasher);
        Cursor findSelected;
        if (selectedWasher > -1) {
            findSelected = db.query(TABLE_TEMPLATES, columnsTemplate,
                    TEMPLATE_ID + "=" + selectedWasher, new String[] {}, null, null,
                    null);
            findSelected.moveToFirst();
            String selectedWasherTemplateString = findSelected.getString(1);
            selectedWasherTemplateString = selectedWasherTemplateString.substring(0,selectedWasherTemplateString.length()-4);
            selectedWasherTemplate.setText(selectedWasherTemplateString); //replaces with name of template
        }
        if (selectedDryer > -1){
            findSelected = db.query(TABLE_TEMPLATES, columnsTemplate,
                    TEMPLATE_ID + "=" + selectedDryer, new String[] {}, null, null,
                    null);
            findSelected.moveToFirst();
            String selectedDryerTemplateString = findSelected.getString(1);
            selectedDryerTemplateString = selectedDryerTemplateString.substring(0,selectedDryerTemplateString.length()-2);
            selectedDryerTemplate.setText(selectedDryerTemplateString);
        }
        retrieveTemplates(); //retrieve all templates
        washerTemplateView.setLayoutManager(verticalLayout);
        dryerTemplateView.setLayoutManager(verticalLayout2);
    }

    private void retrieveTemplates() {
        washerTemplates.clear();
        dryerTemplates.clear();
        mCursorTemplates.moveToFirst();
        //columns 3+ are the templates
        while (!mCursorTemplates.isAfterLast()) {
            boolean isWasher = mCursorTemplates.getInt(2) == 1;
            if (isWasher) {
                washerTemplates.add(new MachineTemplate(mCursorTemplates.getString(1), mCursorTemplates.getInt(0)));
                washerTemplateAdapter.notifyDataSetChanged();
            } else {
                dryerTemplates.add(new MachineTemplate(mCursorTemplates.getString(1), mCursorTemplates.getInt(0)));
                dryerTemplateAdapter.notifyDataSetChanged();
            }
            mCursorTemplates.moveToNext();
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
        actualName = insertTemplateToLocalServer(templateName, isWasher);
        if (isWasher) {
            Toast.makeText(this, "Washer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dryer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //handler for edit template returning
    @Override
    public void onTemplateReplaced(String newTemplateName, int position, boolean isWasher) {
        TemplateAdapter adapter = (isWasher) ? washerTemplateAdapter : dryerTemplateAdapter;
        modifyInDatabase(newTemplateName, adapter.getItemID(position), isWasher);
        if (isWasher) {
            washerTemplates.set(position, new MachineTemplate(newTemplateName, mCursorTemplates.getPosition()));
            washerTemplateAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Washer Template modified successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            dryerTemplates.set(position, new MachineTemplate(newTemplateName, mCursorTemplates.getPosition()));
            dryerTemplateAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Dryer Template  modified successfully!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String insertTemplateToLocalServer(String templateName, boolean isWasher) {
        /////////////////////////////////Extracts template name//////////
        String actualName = (isWasher) ?
            templateName.substring(0, templateName.length() - 4) : //washers have 4 additional chars
            templateName.substring(0, templateName.length() - 2); //dryers have 2
        /////////////////////////inserts template onto appropriate column////////////////////
        insertInDatabase(templateName, isWasher);
        if (isWasher) {
            Toast.makeText(this, "Washer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Dryer Template " + actualName + " added!",
                    Toast.LENGTH_SHORT).show();
        }
        return actualName;
    }

    private void insertInDatabase(String templateString, boolean isWasher) {
        ContentValues cv = new ContentValues(2);
        int isWasherInt = isWasher ? 1 : 0;
        cv.put(TEMPLATE_NAME, templateString);
        cv.put(TEMPLATE_IS_WASHER, isWasherInt);
        db.insert(TABLE_TEMPLATES, null, cv); //inserts to database
        mCursorTemplates =  db.query(TABLE_TEMPLATES, columnsTemplate, null, new String[] {}, null, null,
                null); //requery
        mCursorTemplates.moveToLast();
        if (isWasher) {
            washerTemplates.add(new MachineTemplate(templateString, mCursorTemplates.getInt(0)));
            washerTemplateAdapter.notifyDataSetChanged();
        } else {
            dryerTemplates.add(new MachineTemplate(templateString, mCursorTemplates.getInt(0)));
            dryerTemplateAdapter.notifyDataSetChanged();
        }
    }

    private void modifyInDatabase(String newTemplateName, int templateId, boolean isWasher) {
        ContentValues cv = new ContentValues(2);
        int isWasherInt = isWasher ? 1 : 0;
        cv.put(TEMPLATE_NAME, newTemplateName);
        cv.put(TEMPLATE_IS_WASHER, isWasherInt);
        db.update(TABLE_TEMPLATES, cv, TEMPLATE_ID + "=" + templateId, new String[]{});
    }

    public void updateSelected(int position, boolean isWasher) {
        String COLUMN_NAME = (isWasher) ? SELECTED_WASHER_TEMPLATE : SELECTED_DRYER_TEMPLATE;
        TemplateAdapter adapter = (isWasher) ? washerTemplateAdapter : dryerTemplateAdapter;
        TextView selectedTemplate = (isWasher) ? selectedWasherTemplate : selectedDryerTemplate;
        int id = adapter.getItemID(position);
        String machineName = adapter.getItemName(position);
        String actualName = adapter.getName(position);
        ContentValues cv = new ContentValues(1);
        cv.put(COLUMN_NAME, id); //update selected washer
        db.update(TABLE_USER, cv, null, null); //updates the database
        mCursorUser.moveToFirst();
        System.out.println(mCursorUser.getInt(0));
        if (isWasher) {
            selectedWasher = id;
            washerTemplateAdapter.notifyDataSetChanged();
        } else {
            selectedDryer = id;
            dryerTemplateAdapter.notifyDataSetChanged();
        }
        selectedTemplate.setText(actualName);
    }

    public void launchEditFragment(int position, boolean isWasher) {
        TemplateAdapter adapter = (isWasher) ? washerTemplateAdapter : dryerTemplateAdapter;
        String templateString = adapter.getItemName(position);
        EditTemplateFragment newFragment = EditTemplateFragment.newInstance(templateString, position, isWasher);
        newFragment.show(getSupportFragmentManager(),"EditTemplateFragment");
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
                    launchEditFragment(getLayoutPosition(), isWasher);
                    return true;
                }
            });
        }
    }

    class MachineTemplate {
        String templateName; //machine name still includes settings
        int templateId;
        MachineTemplate(String name, int id) {
            templateName = name;
            templateId = id;
        }
        String getTemplateName() {
            return templateName;
        }
        int getTemplateId() {
            return templateId;
        }
    }

    class TemplateAdapter extends RecyclerView.Adapter<TemplateHolder> {
        private boolean isWasher;
        private ArrayList<MachineTemplate> templates;

        TemplateAdapter(ArrayList<MachineTemplate> data, boolean isWasher) {
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
            String fullName = templates.get(position).getTemplateName();
            String[] nameAndSettings = retrieveActualNameAndSettings(fullName, isWasher);
            String actualName = nameAndSettings[0];
            String settings = nameAndSettings[1];
            if ((isWasher && templates.get(position).getTemplateId() == selectedWasher)
                    || (!isWasher && templates.get(position).getTemplateId() == selectedDryer)) {
                holder.v.setBackgroundColor(Color.parseColor("#00ff00")); //green
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

        public String getName(int position) { return retrieveActualNameAndSettings(templates.get(position).getTemplateName(), isWasher)[0]; }

        public String getItemName(int position) { return templates.get(position).getTemplateName(); }

        public int getItemID(int position) { return templates.get(position).getTemplateId(); }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Cycle Templates Destroyed");
        mCursorUser.close();
        mCursorTemplates.close();
        db.close();
    }
}
