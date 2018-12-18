package com.ycce.kunal.userapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String busno;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private double lat ;
    private double lng;
    private LatLng latLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();

        if (extras!=null) {
            busno = extras.getString("busno");
            Log.d("TAG", "Value is: " + busno);
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Locations").child(busno).child("current position");


        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {


        mMap = googleMap;
        myRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("TAG", "Value is: " + value);
                String []str = value.split(",");
                lat = Double.parseDouble(str[0]);
                lng = Double.parseDouble(str[1]);


        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target).zoom(15).bearing(20).tilt(35).build()));
        latLng  = new LatLng(lat,lng);
        LatLng pos = null;
        if(latLng!=pos){
            mMap.clear();

            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(busno));
            pos=latLng;
        }     mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(sydney).title(busno));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
