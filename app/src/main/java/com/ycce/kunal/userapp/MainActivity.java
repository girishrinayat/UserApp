package com.ycce.kunal.userapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView aSource;
    private AutoCompleteTextView aDestinaion;
    private Button bSearch;

    private ArrayAdapter<String> source;
    private ArrayAdapter<String> destination;
    private  String [] stops;
    private String mSource= null;
    private String mDestination=null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Search");

        progressDialog = new ProgressDialog(this);


        stops= new String[]{"Pardi", "subhan nagar", "old pardi naka", "railway crossing", "sangharsh nagar","swaminarayan mandir", "wathoda" , "karbi",
                "dighori flyover","mhalgi nagar", "manevada square","omkar nagar","rameshawari", "Tukram hall", "narendra nagar",
                "chatrapati square","sawrakar square", "pratap nagar", "padole hospital square","sambhaji square", "NIT garden",
                "Trimurti nagar","mangalmurti square","balaji nagar", "mahindra company", "IC square", "electric zone", "Hingna T-point", "crpf", "Ycce","Wanadongri"
                ,"Buldi", "Dhantoli", "Lokmat Square", "Rahate Colony", "Jail Gate", "Ajni","Sai Mandir", "Chatrapatti", "Sneh Nagar", "Rajeev Nagar", "Somalwada",
                "Ujjwal Nagar", "Sonegaon", "Airport(Pride Hotel)", "Bara Kholi", "Shivangaon","Chinchbhavan", "Khapri Naka", "Khapri", "Khapri Fata", "Parsodi",
                "Gauvsi Manapur", "Jamtha", "Ashokvan", "Dongargaon", "Gothali", "Mohgaon","Satgaon Fata", "Butibori"};

        aSource = (AutoCompleteTextView) findViewById(R.id.aSource);
        aDestinaion = (AutoCompleteTextView) findViewById(R.id.aDestination);
        bSearch = (Button) findViewById(R.id.bSearch);

        source = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,stops);
        aSource.setAdapter(source);

        destination = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,stops);
        aDestinaion.setAdapter(destination);


        aSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSource = (String) adapterView.getItemAtPosition(i);
            }
        });

        aDestinaion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDestination = (String) adapterView.getItemAtPosition(i);
            }

        });

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Loading......");
                    progressDialog.show();

                    if (mSource==null||mDestination==null||mSource==mDestination){
                        progressDialog.dismiss();
                        if (mSource==null){

                            aSource.setError("Source Empty");
                        }
                        if (mDestination==null){

                            aDestinaion.setError("Destination Empty");
                        }
                        if (mDestination!= null&& mSource == mDestination){
                            Toast.makeText(MainActivity.this, "Source and Destination cannot be same!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }else{

                        if (!mSource.equals(null)&&!mDestination.equals(null)&&!mSource.equals(mDestination)){

                            BusStops busStops = new BusStops(mSource,mDestination);
                            busStops.checkRoute();

                            int startpos = busStops.getsourcePosition();
                            int desPos = busStops.getdestinationPosition();
                            String route = busStops.getRoute();
                            String upDown = busStops.upDown();

                            Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);

                            if (startpos != 0 && desPos != 0){

                                searchIntent.putExtra("startpos",startpos);
                                searchIntent.putExtra("desPos",desPos);
                                searchIntent.putExtra("route",route);
                                searchIntent.putExtra("upDown",upDown);
                                Toast.makeText(MainActivity.this, "startpos "+startpos+" desPos "+desPos+" route "+route+ " upDown "+upDown, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                                startActivity(searchIntent);

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Error while requesting ", Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(MainActivity.this, "work in progress go safe ", Toast.LENGTH_SHORT).show();

                        }
                    }

                }

            }
        });

    }

    private boolean isNetworkAvailable() {
                ConnectivityManager cm =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }

                }).create().show();
    }
}
