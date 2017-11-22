package com.cs477.dormbuddy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class LaundryBuddyActivity extends AppCompatActivity {
    LaundryAdapter washerAdapter;
    LaundryAdapter dryerAdapter;
    ListView laundryList;
    ListView dryerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_buddy);
        laundryList = findViewById(R.id.washers);
        dryerList = findViewById(R.id.dryers);
        washerAdapter = new LaundryAdapter(this, true);
        dryerAdapter = new LaundryAdapter(this, false);
        washerAdapter.addItem("HI");
        washerAdapter.addItem("HI Number 2");
        washerAdapter.addItem("HI3");
        dryerAdapter.addItem("hello");
        dryerAdapter.addItem("sup");
        laundryList.setAdapter(washerAdapter);
        dryerList.setAdapter(dryerAdapter);
    }



    public void cyclePreferencesButtonClicked(View view) {

    }

    private class LaundryAdapter extends BaseAdapter {
        private ArrayList<String> machineNames = new ArrayList<String>();
        Context context;
        LayoutInflater mInflater;
        boolean isWasher;
        public LaundryAdapter(Context applicationContext, boolean isWasher) {
            this.context = context;
            this.isWasher = isWasher;
            mInflater = (LayoutInflater.from(applicationContext));
        }
        public void addItem(final String item) {
            machineNames.add(item);
            notifyDataSetChanged();
        }
        public void removeItem(final String item) {
            machineNames.remove(item);
            notifyDataSetChanged();
        }
        public int getCount() {
            return machineNames.size();
        }
        public String getItem(int position) {
            return machineNames.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int layout;
            if (isWasher) {
                layout = R.layout.washer_item;
            } else {
                layout = R.layout.dryer_item;
            }
            view = mInflater.inflate(layout, null);
            TextView name = view.findViewById(R.id.machineName);
            name.setText(machineNames.get(i));
            return view;
        }
    }
}
