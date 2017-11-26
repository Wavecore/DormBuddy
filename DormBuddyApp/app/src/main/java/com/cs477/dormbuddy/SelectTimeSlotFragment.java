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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SelectTimeSlotFragment extends DialogFragment {
    public static interface OnCompleteListener{
        public abstract void onComplete(Calendar start, Calendar end);
    }
    private OnCompleteListener mListener;
    static final String SELECT_TIME_SLOT_TAG = "SelectTimeSlotTag";
    final private long MAXRESERVETIME = 7200000;
    private Calendar startTime;
    private Calendar endTime;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        long start = getArguments().getLong("start",0);
        if(start != 0){
            startTime= Calendar.getInstance();
            startTime.setTime(new Date(start));
        }
        long end = getArguments().getLong("end",0);
        if(end != 0){
            endTime= Calendar.getInstance();
            endTime.setTime(new Date(end));
        }
        System.out.println(startTime);
        System.out.println(endTime);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.fragment_select_time_slot,null);
        Spinner daySpinner = (Spinner)v.findViewById(R.id.reservationDay);
        ArrayAdapter<CharSequence> dayAdapter = new ArrayAdapter<CharSequence>(v.getContext(),R.layout.spinner_day);
        Calendar today = Calendar.getInstance();
        final SimpleDateFormat ft = new SimpleDateFormat("EEE, d MMM yyyy");
        final ListView timeSlots = (ListView) v.findViewById(R.id.timeList);
        setListAdapter(Calendar.getInstance(),v,timeSlots);
        for(int x = 0; x < 7;x++){
            dayAdapter.add(ft.format(today.getTime()));
            today.add(Calendar.DAY_OF_MONTH,1);
        }
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                 @Override
                                                 public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                     String s = (String) adapterView.getItemAtPosition(i);
                                                     Date d = null;
                                                     try{
                                                         d = ft.parse(s);
                                                     }
                                                     catch(ParseException e){
                                                         Toast.makeText(v.getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                                     }
                                                     if(d != null) {
                                                         Calendar c = Calendar.getInstance();
                                                         if(d.after(c.getTime()))
                                                             c.setTime(d);
                                                         setListAdapter(c, v, timeSlots);
                                                     }
                                                 }

                                                 @Override
                                                 public void onNothingSelected(AdapterView<?> adapterView) {

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
                mListener.onComplete(startTime,endTime);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    private void setListAdapter(Calendar c, View view, ListView timeSlots){
        final View v = view;
        final TextView timeLeftText = (TextView)v.findViewById(R.id.timeLeft);
        final TimeSlotAdapter listAdapter = new TimeSlotAdapter(getActivity(),R.layout.timeslot_item,c,this.startTime,this.endTime);
        timeSlots.setAdapter(listAdapter);
        timeSlots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long timeReserved;
                if( listAdapter.getItem(i).owner ==2) {
                    Toast.makeText(v.getContext(),"Time slot has been reserved by someone else",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if( listAdapter.getItem(i).owner > 2) {
                    Toast.makeText(v.getContext(),"Time slot has already passed, can not be reserved",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(listAdapter.endTime != null || listAdapter.startTime != null) {
                    timeReserved = listAdapter.endTime.getTime().getTime() - listAdapter.startTime.getTime().getTime();
                    if (MAXRESERVETIME <= timeReserved && listAdapter.getItem(i).owner == 0) {
                        Toast.makeText(v.getContext(),"You can not reserve any more time slots",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(!listAdapter.reserveTimeSlot(i)){
                    Toast.makeText(v.getContext(),"Reserved time slots must be continuous",Toast.LENGTH_SHORT).show();
                }
                listAdapter.notifyDataSetChanged();
                if(listAdapter.endTime != null || listAdapter.startTime != null)
                    timeReserved = listAdapter.endTime.getTime().getTime() - listAdapter.startTime.getTime().getTime();
                else
                    timeReserved = 0;
                timeLeftText.setText(""+(MAXRESERVETIME - (timeReserved))/60000);
                startTime = listAdapter.startTime;
                endTime = listAdapter.endTime;
            }
        });
    }

    static SelectTimeSlotFragment newInstance(Calendar start, Calendar end){
        SelectTimeSlotFragment display = new SelectTimeSlotFragment();
        Bundle args = new Bundle();
        if(start != null && end != null) {

            args.putLong("start", start.getTime().getTime());
            args.putLong("end", end.getTime().getTime());

        }
        display.setArguments(args);
        return display;
    }
    private class TimeSlotAdapter extends ArrayAdapter<TimeSlot> {
        private Calendar startTime;
        private Calendar endTime;
        public TimeSlotAdapter(Context context, int textViewResourceId,Calendar current, Calendar start, Calendar end) {
            super(context, textViewResourceId);
            this.startTime = start;
            this.endTime = end;
            Calendar times = (Calendar)current.clone();
            times.set(Calendar.HOUR_OF_DAY, 0);
            times.set(Calendar.MINUTE,0);
            times.set(Calendar.SECOND,0);
            times.set(Calendar.MILLISECOND,0);
            for(int x = 0; x < 96;x++){
                Calendar endingTime = (Calendar)times.clone();
                endingTime.add(Calendar.MINUTE,15);
                TimeSlot t;
                if(current.after(times)) {
                    t = new TimeSlot(times, endingTime, 3);
                    if(this.startTime != null && current.after(this.startTime)){
                        this.startTime = endingTime;
                        if(this.endTime != null  && current.after(this.endTime)){
                            this.startTime = null;
                            this.endTime = null;
                        }
                    }
                }
                else if(startTime != null && endTime != null &&                              // Check if there are any reservations yet
                        (startTime.before(times)||startTime.compareTo(times)==0) &&                // If the timeslot occurred during the user's reserved time
                        (endTime.after(endingTime) || endTime.compareTo(endingTime)==0)){
                    t = new TimeSlot(times,endingTime,1);                                 // Set the user to you
                }
                else
                    t = new TimeSlot(times,endingTime);
                add(t);
                times = endingTime;
            }
        }
        public boolean reserveTimeSlot(int position){
            TimeSlot p = getItem(position);
            if(p.owner == 0){
                if(this.startTime == null || this.endTime == null){
                    p.owner = 1;
                    this.endTime = p.endTime;
                    this.startTime = p.startTime;
                    return true;
                }
                else if(p.startTime.compareTo(this.endTime)==0){
                    p.owner = 1;
                    this.endTime = p.endTime;
                    return true;
                }
                else if(p.endTime.compareTo(this.startTime)==0){
                    p.owner = 1;
                    this.startTime = p.startTime;
                    return true;
                }
            }
            else if(p.owner == 1){
                if(this.startTime.compareTo(p.startTime)==0 && this.endTime.compareTo(p.endTime)==0){
                    p.owner = 0;
                    this.startTime = null;
                    this.endTime = null;
                    return true;
                }
                else if(this.endTime.compareTo(p.endTime)==0){
                    p.owner = 0;
                    this.endTime = p.startTime;
                    return true;
                }
                else if(this.startTime.compareTo(p.startTime)==0){
                    p.owner = 0;
                    this.startTime = p.endTime;
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
        public int owner = 0; //0 no owner, 1 you own, 2 someone else owns
        public Calendar startTime;
        public Calendar endTime;
        public TimeSlot(Calendar start,Calendar end){
            startTime = start;
            endTime = end;
        }
        public TimeSlot(Calendar start,Calendar end, int o){
            startTime = start;
            endTime = end;
            owner = o;
        }
        public String toString(){
            SimpleDateFormat ft = new SimpleDateFormat("h:mm a");
            return String.format("%s to %s",ft.format(startTime.getTime()),ft.format(endTime.getTime()));
        }
    }
}
