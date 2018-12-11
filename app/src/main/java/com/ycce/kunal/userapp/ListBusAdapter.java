package com.ycce.kunal.userapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ListBusAdapter extends BaseAdapter {

    private SearchActivity searchActivity;
    private ArrayList<HashMap<String, String>> arrayList;
    public ListBusAdapter(SearchActivity searchActivity, ArrayList<HashMap<String, String>> arrayList) {
        this.searchActivity = searchActivity;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
        ListViewHolder holder =null;
        if (view==null){
            holder = new ListViewHolder();
            view = LayoutInflater.from(searchActivity).inflate(R.layout.buslayoutactivity, viewGroup, false);
            holder.busno = (TextView) view.findViewById(R.id.busno);
            holder.route = (TextView) view.findViewById(R.id.route);
            holder.towards = (TextView) view.findViewById(R.id.towards);
            holder.busstop = (TextView) view.findViewById(R.id.busstop);
            holder.position = (TextView) view.findViewById(R.id.position);
            view.setTag(holder);

        }else{
            holder = (ListViewHolder) view.getTag();

        }
        holder.route.setId(i);
        holder.towards.setId(i);
        holder.busstop.setId(i);
        holder.position.setId(i);
        holder.busno.setId(i);

        Map<String, String> dataHolder ;

        dataHolder = arrayList.get(i);
        holder.busno.setText(dataHolder.get("busno"));
        holder.route.setText(dataHolder.get("route"));
        holder.towards.setText(dataHolder.get("towards"));
        holder.busstop.setText(dataHolder.get("busstop"));
        holder.position.setText(dataHolder.get("position"));




        return view;
    }
    private class ListViewHolder {
        private TextView busno;
        private TextView route;
        private TextView towards;
        private TextView busstop;
        private TextView position;

//         hashMap.put("position",availableBus.getLocation());
//                    hashMap.put("busstop",availableBus.getCurrentBusStop());
//                    hashMap.put("route",availableBus.getRouteName());
//                    hashMap.put("towards",availableBus.getTowards());
    }
}
