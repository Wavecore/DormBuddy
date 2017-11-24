package com.cs477.dormbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        washers.add("HI24");
        washers.add("HI23");
        washers.add("HI35");
        washers.add("HI36");
        washers.add("HI33");
        washers.add("HI31");
        washers.add("HI41");
        washers.add("HI51");
        washers.add("HI61");
        washers.add("HI71");
        washers.add("HI21");
        washers.add("HI11");
        washers.add("HI61");
        washers.add("HI71");
        dryers.add("Hello");
        dryers.add("Sup");
        dryers.add("Buddy");
        ///////////////////////////////////
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



    public void cycleTemplatesButtonClicked(View view) {

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
        if (id == R.id.gear) {
            startActivity(new Intent(this, CycleTemplatesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class LaundryHolder extends RecyclerView.ViewHolder {
        TextView machineName;

        LaundryHolder(View v) {
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

    class LaundryAdapter extends RecyclerView.Adapter<LaundryHolder> {
        private ArrayList<String> machineNames;
        private boolean isWasher;

        LaundryAdapter(ArrayList<String> data, boolean isWasher) {
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
