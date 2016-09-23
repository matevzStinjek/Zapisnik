package com.matevz.zapisnik;

import android.app.Activity;
import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CallLogAdapter extends ArrayAdapter {

    private Activity activity;
    private List<Log> values;
    private static LayoutInflater inflater = null;

    CallLogAdapter(Activity activity, ArrayList<Log> values) {
        super(activity, 0, values);
        try {
            this.activity = activity;
            this.values = values;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
        }
    }

    public int getCount() {
        return values.size();
    }

    public Log getItem(Log position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView name;
        TextView initial;
        ImageView typeImage;
        TextView number;
        TextView dateAndTime;
        TextView comment;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                view = inflater.inflate(R.layout.row, null);
                holder = new ViewHolder();

                holder.name = (TextView) view.findViewById(R.id.name);
                holder.initial = (TextView) view.findViewById(R.id.callerInitial);
                holder.typeImage = (ImageView) view.findViewById(R.id.callStatus);
                holder.number = (TextView) view.findViewById(R.id.number);
                holder.dateAndTime = (TextView) view.findViewById(R.id.dateAndTime);
                holder.comment = (TextView) view.findViewById(R.id.comment);
                holder.typeImage = (ImageView) view.findViewById(R.id.callStatus);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String dateSet = values.get(position).getDate();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Date formDate = new Date(Long.valueOf(dateSet));
            Date today = c.getTime();

            SimpleDateFormat formatter;

            if (formDate.before(today))
                formatter = new SimpleDateFormat("dd MMM HH:mm");
            else
                formatter = new SimpleDateFormat("HH:mm");

            String date = formatter.format(formDate);
            holder.dateAndTime.setText(date);

            if(values.get(position).getName().equals("")){
                holder.name.setText(values.get(position).getNumber());
                holder.number.setText("Unknown");
                holder.initial.setText("#");
            }else{
                String firstLetter = Character.toString(values.get(position).getName().charAt(0));
                holder.name.setText(values.get(position).getName());
                holder.number.setText(values.get(position).getNumber());
                holder.initial.setText(firstLetter);
            }

            holder.comment.setText(values.get(position).getComment());

            String callType = values.get(position).getCallType();
            switch (Integer.parseInt(callType)) {
                case CallLog.Calls.INCOMING_TYPE:
                    holder.typeImage.setImageResource(R.drawable.circle_incoming);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    holder.typeImage.setImageResource(R.drawable.circle_outgoing);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    holder.typeImage.setImageResource(R.drawable.circle_missed);
                    break;
                default:
                    break;
            }


        } catch (Exception e) {
        }
        return view;
    }

}
