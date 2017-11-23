package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SelectTimeSlotFragment extends DialogFragment {
    final private int MAXRESERVEMINUTES = 120;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_select_time_slot,null);
        //DateFormat ft = DateFormat.getDateInstance();
       // Date d = new Date();
        //d.setHours(0);
       // Calendar c;
        final TextView timeLeft = (TextView)v.findViewById(R.id.timeLeft);
      //  timeLeft.setText(ft.format(d));
        final ListView timeSlots = (ListView)v.findViewById(R.id.timeList);
        final TimeSlotAdapter listAdapter = new TimeSlotAdapter(getActivity(),R.layout.timeslot_item);
        for(int x = 0; x <=23; x++) {
            for(int y = 00;y < 60; y +=15){
                int endM = y + 15;
                int endH = x;
                if(endM == 60){
                    endH++;
                    endM = 0;
                }
                if(endH == 24)
                    endH = 0;
                listAdapter.add( new TimeSlot(x,y,endH,endM,0));
            }
        }
        timeSlots.setAdapter(listAdapter);
        timeSlots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(i);
                if(MAXRESERVEMINUTES-(listAdapter.slotsReserved*15) > 0 || listAdapter.getItem(i).owner == 1) {
                    listAdapter.reserveTimeSlot(i);
                    listAdapter.notifyDataSetChanged();
                    timeLeft.setText(""+(MAXRESERVEMINUTES - (listAdapter.slotsReserved * 15)));
                }
            }
        });
        builder.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Select Time Slots", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    static SelectTimeSlotFragment newInstance(){
        SelectTimeSlotFragment display = new SelectTimeSlotFragment();
        return display;
    }
    private class TimeSlotAdapter extends ArrayAdapter<TimeSlot> {
        public int slotsReserved = 0;
        private int startHour = 0;
        private int startMinute = 0;
        private int endHour = 0;
        private int endMinute = 0;
        public TimeSlotAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public TimeSlotAdapter(Context context, int resource, List<TimeSlot> items) {
            super(context, resource, items);
        }
        public boolean reserveTimeSlot(int position){
            TimeSlot p = getItem(position);
            if(p.owner == 0){
                if(slotsReserved == 0){
                    p.owner = 1;
                    slotsReserved++;
                    startHour = p.startHour;
                    startMinute = p.startMinute;
                    endHour = p.endHour;
                    endMinute = p.endMinute;
                    return true;
                }
                else if(position != 0 && getItem(position-1).owner == 1){
                    p.owner = 1;
                    slotsReserved++;
                    endHour = p.endHour;
                    endMinute = p.endMinute;
                    return true;
                }else if(position != getCount()-1 && getItem(position+1).owner == 1){
                    p.owner = 1;
                    slotsReserved++;
                    startHour = p.startHour;
                    startMinute = p.startMinute;
                    return true;
                }
            }
            else if(p.owner == 1){
                if(position == 0 || getItem(position-1).owner != 1){
                    p.owner =0;
                    slotsReserved--;
                    startMinute = p.endHour;
                    startHour = p.endMinute;
                    return true;
                }else if(position == getCount()-1 || getItem(position+1).owner != 1){
                    p.owner = 0;
                    slotsReserved--;
                    endHour = p.startHour;
                    endMinute = p.startMinute;
                    return true;
                }
            }
            return false;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.timeslot_item, null);
            }
            TimeSlot p = getItem(position);
            if (p != null) {
                TextView tt1 = (TextView) v.findViewById(R.id.timeSlotID);
                if (tt1 != null) {
                    tt1.setText(p.toString());
                    if(p.owner == 1)
                        tt1.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    else if(p.owner == 2)
                        tt1.setBackgroundColor(getResources().getColor(R.color.darkRed));
                    else if (p.owner == 0)
                        tt1.setBackgroundColor(Color.TRANSPARENT);
                    else{
                        tt1.setBackgroundColor(getResources().getColor(R.color.slightlyGray));
                        tt1.setFocusable(false);
                    }
                }
            }
            return v;
        }

    }
    private class TimeSlot{
        public int startHour;
        public int startMinute;
        public int endHour;
        public int endMinute;
        public int owner = 0; //0 no owner, 1 you own, 2 someone else owns
        public TimeSlot(int startH,int startM, int endH, int endM){
            startHour = startH;
            startMinute = startM;
            endHour = endH;
            endMinute = endM;
        }
        public TimeSlot(int startH,int startM, int endH, int endM, int o){
            startHour = startH;
            startMinute = startM;
            endHour = endH;
            endMinute = endM;
            owner = o;
        }
        public String toString(){
            return String.format("%d:%02d to %d:%02d",startHour,startMinute,endHour,endMinute);
        }
    }
}
