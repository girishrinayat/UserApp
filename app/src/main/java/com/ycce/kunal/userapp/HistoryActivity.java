package com.ycce.kunal.userapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SharedPreferences applicationpreferences;
    private SharedPreferences.Editor editor;
    public  static  final  String MyPref = "BusApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();

        String userId = applicationpreferences.getString("userId","anon");
        Log.d("asa", "userId is: " + userId );

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("as", "Value is: " + value );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("fdd", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

        startActivity(new Intent(this,MainActivity.class));
    }
}
