package com.cs477.dormbuddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LaundryBuddyActivity extends AppCompatActivity {
    LaundryAdapter washerAdapter;
    LaundryAdapter dryerAdapter;
    RecyclerView laundryList;
    RecyclerView dryerList;
    ArrayList<String> washers = new ArrayList<String>();
    ArrayList<String> dryers = new ArrayList<String>();

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
        washers.add("HI");
        washers.add("HI Number 2");
        washers.add("HI3");
        dryers.add("hello");
        dryers.add("sup");
        ///////////////////////////////////
        washerAdapter = new LaundryAdapter(washers, true);
        dryerAdapter = new LaundryAdapter(dryers, false);
        laundryList.setAdapter(washerAdapter);
        dryerList.setAdapter(dryerAdapter);
        laundryList.setLayoutManager(horizontalLayout);
        dryerList.setLayoutManager(horizontalLayout2);
    }



    public void cyclePreferencesButtonClicked(View view) {

    }

    public class LaundryHolder extends RecyclerView.ViewHolder {
        public TextView machineName;

        public LaundryHolder(View v) {
            super(v);
            machineName = (TextView) v.findViewById(R.id.machineName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LaundryBuddyActivity.this, "Laundry item clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class LaundryAdapter extends RecyclerView.Adapter<LaundryHolder> {
        private ArrayList<String> machineNames;
        private boolean isWasher;

        public LaundryAdapter(ArrayList<String> data, boolean isWasher) {
            machineNames = data;
            this.isWasher = isWasher;
        }
        @Override
        public LaundryHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            int layout;
            if (isWasher) {
                layout = R.layout.washer_item;
            } else {
                layout = R.layout.dryer_item;
            }
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            LaundryHolder holder = new LaundryHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(final LaundryHolder holder, int position) {
            holder.machineName.setText(machineNames.get(position));

        }
        @Override
        public int getItemCount() {
            return machineNames.size();
        }
    }
}
