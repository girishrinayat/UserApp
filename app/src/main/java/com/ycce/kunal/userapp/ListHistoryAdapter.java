package com.ycce.kunal.userapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ListHistoryAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public ListHistoryAdapter(HistoryActivity historyActivity, ArrayList<HashMap<String,String>> dataList) {
        activity = historyActivity;
        data=dataList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("fdd", "loop"+i);
        ListHistoryViewHolder holder =null;
            holder = new ListHistoryViewHolder();
            view = LayoutInflater.from(activity).inflate(R.layout.list_row_history, viewGroup, false);
            holder.heading = (TextView) view.findViewById(R.id.titletext);
            holder.body = (TextView) view.findViewById(R.id.ticketText);
            view.setTag(holder);

            holder = (ListHistoryViewHolder) view.getTag();

        holder.heading.setId(i);
        holder.body.setId(i);

        Map<String, String> dataholder ;

        dataholder = data.get(i);
        try{

            holder.heading.setText(dataholder.get("id"));
            holder.body.setText(dataholder.get("value"));
        }catch (Exception e){

        }

        return view;
    }


private class ListHistoryViewHolder {
    private TextView heading;
    private TextView body;
}
}