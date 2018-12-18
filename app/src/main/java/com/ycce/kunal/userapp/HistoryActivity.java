package com.ycce.kunal.userapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SharedPreferences applicationpreferences;
    private SharedPreferences.Editor editor;
    public  static  final  String MyPref = "BusApp";

    private ListView listView;
    private ProgressBar loader;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();

        listView = (ListView) findViewById(R.id.historyView);
        loader = (ProgressBar) findViewById(R.id.loader);
        listView.setEmptyView(loader);

        String userId = applicationpreferences.getString("userId","anon");
        Log.d("asa", "userId is: " + userId );
                // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User Details").child("User").child(userId).child("History");

        myRef.addValueEventListener(valueEventListener );
//        myRef.removeEventListener(valueEventListener);


    }
    ValueEventListener valueEventListener = new ValueEventListener() {

        @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String,String> map = new HashMap<String,String>();
                    String value = snapshot.getValue(String.class);
                    String ticket = snapshot.getKey();
                    value = new String(Base64.decode(value, 1));
                    map.put("id",ticket);
                    map.put("value",value);
                    dataList.add(map);
                    Log.d("asa", "value is: " + value);
        }
        retrive();
    }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("fdd", "Failed to read value.", error.toException());
        }
    };

    private void retrive() {
                ListHistoryAdapter historyAdapter = new ListHistoryAdapter(this,dataList);
                listView.setAdapter(historyAdapter);
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("click","adapter"+adapterView+" "+view+" " +i+" "+l);
            }
        });
*/
    }





    @Override
    public void onBackPressed() {
        finish();

        startActivity(new Intent(this,MainActivity.class));
    }

}
