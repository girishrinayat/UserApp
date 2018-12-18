package com.ycce.kunal.userapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    private Button bBack;
    private ListView listView;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    AvailableBus availableBus = null;

    private String setroute;
    private String upDown;
    private ProgressBar loader;
    private ArrayList<HashMap<String,String>> arrayList= new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();

        if (extras!=null) {
            setroute = extras.getString("route");
            upDown = extras.getString("upDown");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Locations");

        myRef.addValueEventListener(listener);
        bBack = (Button) findViewById(R.id.bBack);
        listView =(ListView) findViewById(R.id.list);
        loader = (ProgressBar) findViewById(R.id.loader);
        listView.setEmptyView(loader);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mapIntent = new Intent(SearchActivity.this,MapsActivity.class);
                mapIntent.putExtra("busno",arrayList.get(+i).get("busno"));
                finish();
                startActivity(mapIntent);
                Log.d("CLick",arrayList.get(+i).get("busno"));
            }
        });
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                availableBus = new AvailableBus();

                Log.d("DataSnap", snapshot.getKey().toString());

                for (DataSnapshot childSnap:snapshot.getChildren()) {

                    availableBus.setBusNumber(snapshot.getKey().toString());

                    if (childSnap.getKey().toString().equals("current position")){

                        availableBus.setLocation(childSnap.getValue().toString());

                    }else if(childSnap.getKey().toString().equals("currentStop")){

                        availableBus.setCurrentBusStop(childSnap.getValue().toString());

                    }else if(childSnap.getKey().toString().equals("routeName")){

                        availableBus.setRouteName(childSnap.getValue().toString());

                    }else if(childSnap.getKey().toString().equals("towards")){

                        availableBus.setTowards(childSnap.getValue().toString());

                    }
                }

             if(snapshot!=null){
                 if ((setroute.trim().equalsIgnoreCase(availableBus.getRouteName())) && (upDown.trim().equalsIgnoreCase(availableBus.getTowards()))){

                     HashMap<String,String> hashMap = new HashMap<>();
                     hashMap.put("busno",availableBus.getBusNumber());
                     hashMap.put("position",availableBus.getLocation());
                     hashMap.put("busstop",availableBus.getCurrentBusStop());
                     hashMap.put("route",availableBus.getRouteName());
                     hashMap.put("towards",availableBus.getTowards());
                     arrayList.add(hashMap);
                 }
             }

            }

            if (arrayList.size()!=0){
                ListBusAdapter listBusAdapter = new ListBusAdapter(SearchActivity.this,arrayList);
                listView.setAdapter(listBusAdapter);
            }else{
                Toast.makeText(SearchActivity.this, "No bus available", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    @Override
    public void onBackPressed() {
        finish();
    }
}
