package com.cs477.dormbuddy;

import android.content.Intent;
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

import java.util.Collections;

import java.util.ArrayList;
import java.util.Date;

public class LaundryBuddyActivity extends AppCompatActivity {
    LaundryAdapter washerAdapter;
    LaundryAdapter dryerAdapter;
    RecyclerView laundryList;
    RecyclerView dryerList;
    ArrayList<LaundryMachine> washers = new ArrayList<LaundryMachine>();
    ArrayList<LaundryMachine> dryers = new ArrayList<LaundryMachine>();
    public final int GOOD = 0;
    public final int CAUTION = 1;
    public final int BROKEN = 2;
    public final int FREE = 0;
    public final int CURRENTLY_IN_USE = 1;
    public final int RESERVED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //BEGIN fake data
        washers.add(new LaundryMachine("01", GOOD, FREE, 0, true));
        washers.add(new LaundryMachine("02", CAUTION, FREE, 0, true));
        washers.add(new LaundryMachine("03", BROKEN, FREE, 0, true));
        washers.add(new LaundryMachine("04", GOOD, RESERVED, 0, true));
        washers.add(new LaundryMachine("05", CAUTION, RESERVED, 0, true));
        /*
        washers.add(new LaundryMachine("10", GOOD, FREE, 0));
        washers.add(new LaundryMachine("11", GOOD, FREE, 0));
        washers.add(new LaundryMachine("12", CAUTION, FREE, 0));
        washers.add(new LaundryMachine("13", BROKEN, FREE, 0));
        washers.add(new LaundryMachine("14", GOOD, RESERVED, 0));
        washers.add(new LaundryMachine("15", CAUTION, RESERVED, 0));
        washers.add(new LaundryMachine("16", BROKEN, RESERVED, 0));
        washers.add(new LaundryMachine("17", GOOD, CURRENTLY_IN_USE, 1));
        washers.add(new LaundryMachine("18", CAUTION, CURRENTLY_IN_USE, 1));
        washers.add(new LaundryMachine("19", BROKEN, CURRENTLY_IN_USE, 1));
        */
        dryers.add(new LaundryMachine("01", GOOD, FREE, 0, false));
        dryers.add(new LaundryMachine("02", CAUTION, FREE, 0, false));
        dryers.add(new LaundryMachine("03", BROKEN, FREE, 0, false));
        dryers.add(new LaundryMachine("04", GOOD, RESERVED, 0, false));
        dryers.add(new LaundryMachine("05", CAUTION, RESERVED, 0, false));
        dryers.add(new LaundryMachine("06", GOOD, CURRENTLY_IN_USE, 0, false));
        dryers.add(new LaundryMachine("07", CAUTION, CURRENTLY_IN_USE, 0, false));
        //end fake data
        ///////////////////////////////////
        washerAdapter = new LaundryAdapter(washers, true);
        dryerAdapter = new LaundryAdapter(dryers, false);
        washerAdapter.sortData();
        dryerAdapter.sortData();
        laundryList.setAdapter(washerAdapter);
        dryerList.setAdapter(dryerAdapter);
        laundryList.setLayoutManager(horizontalLayout);
        dryerList.setLayoutManager(horizontalLayout2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(laundryList.getContext(),
                horizontalLayout.getOrientation());
        laundryList.addItemDecoration(dividerItemDecoration);
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

        public int getTimeLeftSeconds() {
            if (timeDone == 0) {
                return 0;
            } else { //if time expired OR there is time left
                long currentTime = new Date().getTime();
                if (timeDone <= currentTime) {
                    //makes sure the status is updated across the server as it should have been
                    changeStatus(FREE);
                    timeDone = 0;
                    return 0;
                } else {
                    return (int)(timeDone/1000 - currentTime/1000);
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
            boolean isLarger = Integer.parseInt(aSettings) < Integer.parseInt(bSettings); //math is wrong
            boolean isSmaller = Integer.parseInt(aSettings) > Integer.parseInt(bSettings);
            return (isLarger) ? -1 : (isSmaller ? 1 : 0);
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
                                    0, machine.getStatus(), machine.getCondition(), machine.getTimeLeftSeconds(), machine.isWasher);
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
                                    0, machine.getStatus(), machine.getCondition(), machine.getTimeLeftSeconds(), machine.isWasher);
                            newFrag.show(getFragmentManager(),"MachineSelectedFragment");
                        }
                    });
                    break;
            }
            switch (machine.getStatus()) {
                case CURRENTLY_IN_USE:
                    statusIcon.setImageResource(R.drawable.currently_in_use);
                    break;
                case RESERVED:
                    statusIcon.setImageResource(R.drawable.reserved);
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
}
