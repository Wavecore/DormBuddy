package com.cs477.dormbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static com.cs477.dormbuddy.LocalUserHelper.BUILDING_ID;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_DRYER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.SELECTED_WASHER_TEMPLATE;
import static com.cs477.dormbuddy.LocalUserHelper.TABLE_USER;

public class LaundryBuddyActivity extends AppCompatActivity {
    LaundryAdapter washerAdapter;
    LaundryAdapter dryerAdapter;
    RecyclerView laundryList;
    RecyclerView dryerList;
    ArrayList<LaundryMachine> washers = new ArrayList<LaundryMachine>();
    ArrayList<LaundryMachine> dryers = new ArrayList<LaundryMachine>();
    public static final int GOOD = 0;
    public static final int CAUTION = 1;
    public static final int BROKEN = 2;
    public static final int FREE = 0;
    public static final int CURRENTLY_IN_USE = 1;
    public static final int RESERVED = 2;

    //cursor queries for if user has set up templates yet
    private SQLiteDatabase db = null;
    private LocalUserHelper dbHelper = null;
    private Cursor mCursorUser;
    private LoadLaundryMachinesTask retrieveLaundryTask = null;
    final static String[] columnsUser = {SELECTED_WASHER_TEMPLATE, SELECTED_DRYER_TEMPLATE, BUILDING_ID};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new LocalUserHelper(this);
        db = dbHelper.getWritableDatabase();
        mCursorUser = db.query(TABLE_USER, columnsUser, null, new String[] {}, null, null,
                null);
        mCursorUser.moveToFirst();
        int selectedWasher = mCursorUser.getInt(0);
        int selectedDryer = mCursorUser.getInt(1);
        String buildingId = mCursorUser.getString(2);
        //new users will not see LaundryBuddy unless they have a template selected
        if (selectedWasher < 0 || selectedDryer < 0) {
            Toast.makeText(this, "You Must Select A Washer & Dryer Template To Use LaundryBuddy", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CycleTemplatesActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_laundry_buddy);
        laundryList = findViewById(R.id.washers);
        dryerList = findViewById(R.id.dryers);
        laundryList.setHasFixedSize(true);
        dryerList.setHasFixedSize(true);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this);
        horizontalLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager horizontalLayout2 = new LinearLayoutManager(this);
        horizontalLayout2.setOrientation(LinearLayoutManager.HORIZONTAL);
        /////////fill arraylists here///////
        /*
        Query server for laundry machines
            String[] names = server.get(machine array);
            for (int i = 0; i < names.length(); i++) {
                if (machine.type == washer) {
                    washers.add(machine);
                } else {
                    dryers.add(machine);
                }
            }
         */
        /*BEGIN fake data
        washers.add(new LaundryMachine("01", GOOD, FREE, 0, true));
        washers.add(new LaundryMachine("02", CAUTION, FREE, 0, true));
        washers.add(new LaundryMachine("03", BROKEN, FREE, 0, true));
        washers.add(new LaundryMachine("04", GOOD, RESERVED, 6, true));
        washers.add(new LaundryMachine("05", CAUTION, RESERVED, 7, true));
        dryers.add(new LaundryMachine("01", GOOD, FREE, 0, false));
        dryers.add(new LaundryMachine("02", CAUTION, FREE, 1, false));
        dryers.add(new LaundryMachine("03", BROKEN, FREE, 0, false));
        dryers.add(new LaundryMachine("04", GOOD, RESERVED, 4, false));
        dryers.add(new LaundryMachine("05", CAUTION, RESERVED, 5, false));
        dryers.add(new LaundryMachine("06", GOOD, CURRENTLY_IN_USE,151299356731l
                , false));
        dryers.add(new LaundryMachine("07", CAUTION, CURRENTLY_IN_USE, 7, false));
        //end fake data
        /////////////////////////////////*/
        loadLaundryMachines(buildingId);

        washerAdapter = new LaundryAdapter(washers, true);
        dryerAdapter = new LaundryAdapter(dryers, false);
        laundryList.setAdapter(washerAdapter);
        dryerList.setAdapter(dryerAdapter);
        laundryList.setLayoutManager(horizontalLayout);
        dryerList.setLayoutManager(horizontalLayout2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(laundryList.getContext(),
                horizontalLayout.getOrientation());
        laundryList.addItemDecoration(dividerItemDecoration);
    }

    public void loadLaundryMachines(String buildingID) {
        retrieveLaundryTask = new LoadLaundryMachinesTask(buildingID, this);
        retrieveLaundryTask.execute((Void) null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, CycleTemplatesActivity.class));
            return true;
        }
        else if (id == R.id.action_legend) {
            LegendFragment newFragment = new LegendFragment();
            newFragment.show(getFragmentManager(), "LegendFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LaundryMachine implements Comparable<LaundryMachine>{
        String machineName;
        boolean isWasher;
        int condition;
        int status;
        long timeDone;
        LaundryMachine(String machineName, int condition, int status, long timeDone, boolean isWasher) {
            this.machineName = machineName;
            this.condition = condition;
            this.status = status;
            this.timeDone = timeDone;
            this.isWasher = isWasher;
        }

        public boolean reserve(long reservationLength) {
            if (status == RESERVED || condition == BROKEN || status == CURRENTLY_IN_USE) {
                return false; //can't reserve broken or reserved machines
            }
            //UTC based time of reservation, with a 20 minute extra minutes
            this.timeDone = new Date().getTime() + reservationLength + 20*60*1000;
            this.status = RESERVED;
            //update database
            /*
            try {
                update a value
            } catch IllegalModification {

            }
             */
            return true;
        }

        public void cancel() {
            this.timeDone = 0;
            //update database
        }

        public int getTimeLeft() {
            if (timeDone == 0) {
                return 0;
            } else { //if time expired OR there is time left
                long currentTime = new Date().getTime();
                //time of it being done has passed
                if (timeDone <= currentTime) {
                    //makes sure the status is updated across the server as it should have been
                    //changeStatus(FREE); //this machine is supposed to actually be free
                    timeDone = 0;
                    return 0;
                } else {
                    return (int)(timeDone/(60*1000) - currentTime/(1000*60));
                }
            }
        }

        //setters
        //must be admin for this one
        public void changeCondition(int newCondition) {
            //check for admin
            if (condition == BROKEN) {
                this.status = RESERVED; //marks broken machines as reserve to aid with comparison
            }
            this.condition = newCondition;
            //update database
        }

        //any user can reserve a machine
        public void changeStatus(int newStatus) {
            this.status = newStatus;
            //updateDatabase
        }

        //getters
        public int getCondition() {
            return condition;
        }

        public int getStatus() {
            return status;
        }

        public String getMachineName() {
            return machineName;
        }

        //ORDER IS
        /*
           GOOD FREE => CAUTION FREE => GOOD IN_USE => CAUTION IN_USE => GOOD RESERVED => CAUTION RESERVED => BROKEN RESERVED
           00 > 10 > 01 > 11 > 02 > 12 > 22
           OR, if we switch TO status and condition
           00 > 01 > 10 > 11 > 20 > 21 > 22
         */
        public int compareTo(LaundryMachine b) {
            String aSettings = "" + status + condition;
            String bSettings = "" + b.status + b.condition;
            if (condition == BROKEN && b.condition == BROKEN) {
                return 0;
            } else if (condition == BROKEN) {
                return 1;
            } else if (b.condition == BROKEN) {
                return -1;
            }
            //special case when both machines are in use
            if (status == CURRENTLY_IN_USE && b.status == CURRENTLY_IN_USE) {
                return (int)(timeDone - b.timeDone);
            }
            boolean isLarger = Integer.parseInt(aSettings) < Integer.parseInt(bSettings);
            boolean isSmaller = Integer.parseInt(aSettings) > Integer.parseInt(bSettings);
            //sorts them by status and condition, otherwise asciiabetically
            return (isLarger) ? -1 : (isSmaller ? 1 : machineName.compareTo(b.machineName));
        }
    }

    class LaundryHolder extends RecyclerView.ViewHolder {
        TextView machineName;
        ImageView machinePhoto;
        ImageView statusIcon;
        ImageView conditionIcon;
        LaundryMachine machine;
        View v;

        LaundryHolder(View v) {
            super(v);
            machineName = v.findViewById(R.id.machineName);
            machinePhoto = v.findViewById(R.id.machinePhoto);
            statusIcon = v.findViewById(R.id.machineStatus);
            conditionIcon = v.findViewById(R.id.machineCondition);
            this.v = v;
        }

        void setMachine(final LaundryMachine machine) {
            this.machine = machine;
            machineName.setText(machine.getMachineName());
            //change icons according to condition
            if (!machine.isWasher) {
                machinePhoto.setImageResource(R.drawable.dryer);
            }
            //adjust the icons
            switch (machine.getCondition()) {
                case CAUTION:
                    conditionIcon.setImageResource(R.drawable.caution);
                    machinePhoto.setAlpha(1f); //obligatory make image fully opague due to android bug
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MachineSelectedFragment newFrag = MachineSelectedFragment.newInstance(machine.getMachineName(),
                                    0, machine.getStatus(), machine.getCondition(), machine.getTimeLeft(), machine.isWasher);
                            newFrag.show(getFragmentManager(),"MachineSelectedFragment");
                        }
                    });
                    break;
                case BROKEN:
                    conditionIcon.setImageResource(R.drawable.broken);
                    machinePhoto.setAlpha(0.5f); //makes disabled machines half visible
                    //gets rid of time left
                    v.findViewById(R.id.optionalTimer).setVisibility(View.GONE); //HIDES TIMER
                    break;
                default: //GOOD
                    conditionIcon.setImageResource(R.drawable.working);
                    machinePhoto.setAlpha(1f);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MachineSelectedFragment newFrag = MachineSelectedFragment.newInstance(machine.getMachineName(),
                                    0, machine.getStatus(), machine.getCondition(), machine.getTimeLeft(), machine.isWasher);
                            newFrag.show(getFragmentManager(),"MachineSelectedFragment");
                        }
                    });
                    break;
            }
            //sets up a 00:00 padded string for time left
            int hoursLeft = (machine.getTimeLeft()/60);
            String hoursLeftString = (hoursLeft < 10) ? "0" + hoursLeft: "" + hoursLeft;
            int minutesLeft = (machine.getTimeLeft()%60);
            String minutesLeftString = (minutesLeft < 10) ? "0" + minutesLeft: "" + minutesLeft;
            String timeLeftString = "" + hoursLeftString + ":" + minutesLeftString;
            switch (machine.getStatus()) {
                case CURRENTLY_IN_USE:
                    statusIcon.setImageResource(R.drawable.currently_in_use);
                    ((TextView)v.findViewById(R.id.timeLeftTextView)).setText(timeLeftString);
                    break;
                case RESERVED:
                    statusIcon.setImageResource(R.drawable.reserved);
                    ((TextView)v.findViewById(R.id.timeLeftTextView)).setText(timeLeftString);
                    break;
                default: //FREE
                    statusIcon.setImageResource(R.drawable.free);
                    v.findViewById(R.id.optionalTimer).setVisibility(View.GONE); //HIDES TIMER
                    break;

            }
        }
    }

    class LaundryAdapter extends RecyclerView.Adapter<LaundryHolder> {
        private ArrayList<LaundryMachine> machines;
        private boolean isWasher;

        LaundryAdapter(ArrayList<LaundryMachine> data, boolean isWasher) {
            machines = data;
            this.isWasher = isWasher;
        }
        @Override
        public LaundryHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            int layout;
            //pick image to represent view
            layout = R.layout.laundry_machine_item;
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new LaundryHolder(view);
        }
        @Override
        public void onBindViewHolder(final LaundryHolder holder, int position) {
            LaundryMachine machine = machines.get(position);
            holder.setMachine(machine);

        }
        @Override
        public int getItemCount() {
            return machines.size();
        }

        public void sortData() {
            Collections.sort(machines); //sort all machines
            notifyDataSetChanged(); //updates the data
        }
    }

    public class LoadLaundryMachinesTask extends AsyncTask<Void, Void, Boolean> {

        private final String requestURL;
        private final String requestUsingLaundryURL;
        private Context context;
        private String buildingId;
        private JSONObject occupiedMachines;

        LoadLaundryMachinesTask(String buildingID, Context context) {
            this.buildingId = buildingID;
            requestURL = String.format("https://hidden-caverns-60306.herokuapp.com/laundryMachines/%s", buildingId);
            long now = new Date().getTime();
            requestUsingLaundryURL = String.format("https://hidden-caverns-60306.herokuapp.com/usingLaundryMachines/%s/%s", buildingId, now);
            this.context = context;
        }

        //sends the request in background
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL requestMachinesURL = new URL(requestURL);
                URL requestUsingLaundryMachinesURL = new URL(requestUsingLaundryURL);

                //GETS the machines
                HttpsURLConnection connection = (HttpsURLConnection) requestMachinesURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                //GETS BUSY/RESERVED machines
                HttpsURLConnection connection2 = (HttpsURLConnection) requestUsingLaundryMachinesURL.openConnection();
                connection2.setRequestMethod("GET");
                connection2.setDoOutput(false);
                connection2.setConnectTimeout(5000);
                connection2.setReadTimeout(5000);
                connection2.connect();

                //get the responses
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream())),
                        rd2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
                String getMachinesContent = "", getMachinesLine, getUsingMachinesContent = "", getUsingMachinesLine;

                while ((getMachinesLine = rd.readLine()) != null) {
                    getMachinesContent += getMachinesLine + "\n";
                }

                while ((getUsingMachinesLine = rd2.readLine()) != null) {
                    getUsingMachinesContent += getUsingMachinesLine + "\n";
                }

                washers.clear();
                dryers.clear();

                //parse the response JSON
                JSONObject getMachinesResponse = new JSONObject(getMachinesContent);
                System.out.println(getMachinesResponse);
                Iterator<?> keys = getMachinesResponse.keys();

                JSONObject getUsingMachinesResponse = new JSONObject(getUsingMachinesContent);
                System.out.println(getUsingMachinesResponse);

                //iterate over each key, adding the laundry machine in
                while (keys.hasNext()) {
                    String keyString = (String) keys.next();
                    if (getMachinesResponse.get(keyString) instanceof JSONObject) {
                        JSONObject machineJSON = (JSONObject) getMachinesResponse.get(keyString);
                        boolean isWasher = machineJSON.getBoolean("IsWasher");
                        //tries to see if laundry machine is being used currently before adding the machine as free
                        ArrayList<LaundryMachine> machinesList = (isWasher) ? washers : dryers;
                        if (getUsingMachinesResponse.has(keyString)) {
                            JSONObject reservationObject = getUsingMachinesResponse.getJSONObject(keyString);
                            System.out.println("Status of reserved machine is " + reservationObject.getInt("Status"));
                            //adds the machine with the correct status and time left
                            machinesList.add(new LaundryMachine(
                                    machineJSON.getString("Name"), machineJSON.getInt("Condition"),
                                    reservationObject.getInt("Status"), reservationObject.getLong("TimeDone"), isWasher));
                        } else { //didn't find a reservation
                            //adds the machine with a FREE status and 0 time done
                            machinesList.add(new LaundryMachine(
                                    machineJSON.getString("Name"), machineJSON.getInt("Condition"), FREE, 0, isWasher));
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                System.out.println(e.toString());
                return false;
            }
        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            washerAdapter.sortData();
            dryerAdapter.sortData();
            washerAdapter.notifyDataSetChanged();
            dryerAdapter.notifyDataSetChanged();
            super.onPostExecute(aBoolean);
        }
    }
}
