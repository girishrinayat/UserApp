package com.ycce.kunal.userapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.Toast;

import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView aSource;
    private AutoCompleteTextView aDestinaion;
    private EditText adult;
    private EditText child;
    private EditText date;
    private Button bSearch;

    private SharedPreferences applicationpreferences;
    private SharedPreferences.Editor editor;
    public  static  final  String MyPref = "BusApp";
    private ArrayAdapter<String> source;
    private ArrayAdapter<String> destination;
    private  String [] stops;
    private String mSource= null;
    private String mDestination=null;
    private String mAdult = null;
    private String mchild = null;
    private ProgressDialog progressDialog;
    private  Date mDate;
    private  SimpleDateFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setTitle("Main Menu");
        setSupportActionBar(myToolbar);

        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();
        stops= new String[]{"Pardi", "subhan nagar", "old pardi naka",  "sangharsh nagar","swaminarayan mandir", "wathoda" , "karbi",
                "dighori flyover","mhalgi nagar", "manevada square","omkar nagar","rameshawari", "Tukram hall", "narendra nagar",
                "chatrapati square","sawrakar square", "pratap nagar", "padole hospital square","sambhaji square", "NIT garden",
                "Trimurti nagar","mangalmurti square","balaji nagar", "mahindra company", "IC square", "electric zone", "Hingna T-point", "crpf", "Ycce","Wanadongri"
                ,"Buldi", "Dhantoli", "Lokmat Square", "Rahate Colony", "Jail Gate", "Ajni","Sai Mandir",  "Sneh Nagar", "Rajeev Nagar", "Somalwada",
                "Ujjwal Nagar", "Sonegaon", "Airport(Pride Hotel)", "Bara Kholi", "Shivangaon","Chinchbhavan", "Khapri Naka", "Khapri", "Khapri Fata", "Parsodi",
                "Gauvsi Manapur", "Jamtha", "Ashokvan", "Dongargaon", "Gothali", "Mohgaon","Satgaon Fata", "Butibori"};

        formatter = new SimpleDateFormat("dd/MM/yyyy");
        mDate = new Date();

        aSource = (AutoCompleteTextView) findViewById(R.id.aSource);
        aDestinaion = (AutoCompleteTextView) findViewById(R.id.aDestination);
        bSearch = (Button) findViewById(R.id.bSearch);
        adult = (EditText)findViewById(R.id.numberofadult);
        child = (EditText)findViewById(R.id.numberofchild);
        date = (EditText)findViewById(R.id.currentDate);

        date.setText(formatter.format(mDate));

        progressDialog = new ProgressDialog(this);

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
                int nchild = 0;
                int nadult = 0;
                mAdult = adult.getText().toString();
                mchild = child.getText().toString();

                if (!isNetworkAvailable()){
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Loading......");
                    progressDialog.show();

                    try{
                        nadult = Integer.parseInt(mAdult);
                        nchild = Integer.parseInt(mchild);

                    }catch (NumberFormatException e){
                            child.setError("please enter the number ");
                            adult.setError("please enter the number ");
                    }
                    if (mSource==null||mDestination==null||mSource==mDestination){
                        progressDialog.dismiss();

                        if (nadult==0&&nchild==0){
                            child.setError("child cannot be 0");
                            adult.setError("Adult cannot be 0");
                        }
                        if (mSource==null){

                            aSource.setError("Source Empty");
                        }
                        if (mDestination==null){

                            aDestinaion.setError("Destination Empty");
                        }
                        if (mDestination!= null&& mSource == mDestination){
//                            Toast.makeText(MainActivity.this, "Source and Destination cannot be same!!!!", Toast.LENGTH_SHORT).show();
//                            aSource.setError("Source same as Destination");
                            aDestinaion.setError("Destination same as Source");
                        }
                    }else{

                        if (!mSource.equals(null)&&!mDestination.equals(null)&&!mSource.equals(mDestination)){

                            BusStops busStops = new BusStops(mSource,mDestination);
                            busStops.checkRoute();

                            int startpos = busStops.getsourcePosition();
                            int desPos = busStops.getdestinationPosition();
                            String route = busStops.getRoute();
                            String upDown = busStops.upDown();

                            Intent ticketIntent = new Intent(MainActivity.this,TicketActivity.class);

                            if (startpos != 0 && desPos != 0 &&(nadult!=0||nchild!=0)){

                                int distance = desPos-startpos;
                                if (distance<0){
                                    distance = -1*distance;
                                }
                                String ticketdate =formatter.format(mDate);
                                ticketIntent.putExtra("distance",distance);
                                ticketIntent.putExtra("route",route);
                                ticketIntent.putExtra("upDown",upDown);
                                ticketIntent.putExtra("Adult",nadult);
                                ticketIntent.putExtra("Child",nchild);
                                ticketIntent.putExtra("date",ticketdate);
                                ticketIntent.putExtra("source",mSource);
                                ticketIntent.putExtra("destination",mDestination);
//                                Toast.makeText(MainActivity.this, "startpos "+startpos+" child "+nchild+" Adult "+ nadult+" date " +date+" desPos "+desPos+" route "+route+ " upDown "+upDown, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                                startActivity(ticketIntent);

                            }else{
                                progressDialog.dismiss();
                                if (nadult==0&&nchild==0){
                                    child.setError("child cannot be 0");
                                    adult.setError("Adult cannot be 0");
                                }else{
                                    Toast.makeText(MainActivity.this, "Error while requesting ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            // Toast.makeText(MainActivity.this, "work in progress go safe ", Toast.LENGTH_SHORT).show();

                        }
                    }

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.history) {
            finish();
           startActivity(new Intent(this,HistoryActivity.class));
            return true;
        } else if (id == R.id.setting) {
            Toast.makeText(this, "We will develop setting soon...", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.contactUs) {
            Toast.makeText(this, "We will develop contact Us  soon...", Toast.LENGTH_SHORT).show();
            return true;

        }else if (id == R.id.logout){

            editor.clear();
            editor.commit();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return true;

        }else if (id == R.id.exit){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
