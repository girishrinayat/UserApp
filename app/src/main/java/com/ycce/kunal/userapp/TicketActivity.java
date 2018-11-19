package com.ycce.kunal.userapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketActivity extends AppCompatActivity {

    private int iFare = 8;

    private LinearLayout ticketLayout;
    private LinearLayout paymentLayout;

    private Button payButton;
    private Button cancel;

    private TextView source;
    private TextView destination;
    private TextView route;
    private TextView passengers;
    private TextView fare;
    private TextView issuedate;
    private TextView validtill;

    private int distance;
    private int nadult;
    private int nchild;
    private String setroute;
    private String upDown;
    private String ticketdate;
    private String mSource;
    private String mDestination;
    SimpleDateFormat formatter;
    Date mDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbars = (Toolbar) findViewById(R.id.my_toolbars);
        setSupportActionBar(myToolbars);
        setContentView(R.layout.activity_ticket);

        Bundle extras = getIntent().getExtras();

        if (extras!=null) {
            distance = extras.getInt("distance");
            nadult = extras.getInt("Adult");
            nchild = extras.getInt("Child");
            setroute = extras.getString("route");
            upDown = extras.getString("upDown");
            ticketdate = extras.getString("date");
            mSource = extras.getString("source");
            mDestination = extras.getString("destination");
        }
        formatter = new SimpleDateFormat("HH:mm");
        mDate = new Date();

        source = (TextView) findViewById(R.id.source);
        destination = (TextView) findViewById(R.id.destination);
        route = (TextView) findViewById(R.id.route);
        passengers = (TextView) findViewById(R.id.passengers);
        fare = (TextView) findViewById(R.id.fare);
        issuedate = (TextView) findViewById(R.id.issuedate);
        validtill = (TextView) findViewById(R.id.validtill);
        payButton = (Button)findViewById(R.id.payButton);
        cancel = (Button)findViewById(R.id.cancel);
        ticketLayout= (LinearLayout) findViewById(R.id.ticketLayout);
        paymentLayout= (LinearLayout) findViewById(R.id.ticketLayout);

        //ticket logic
        fare();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity( new Intent(TicketActivity.this, MainActivity.class));
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ticketLayout.setVisibility(View.GONE);
//                paymentLayout.setVisibility(View.VISIBLE);
            }
        });



    }

    void fare(){
        //Fair calculate
        if (distance > 4){
            iFare = iFare + (distance-4);

        }
        iFare = (int) ((iFare*nadult) + (iFare*nchild*0.5));
        fare.setText(iFare +".00 Rs");
        source.setText(mSource);
        destination.setText(mDestination);
        route.setText(setroute);
        passengers.setText( "Adult: "+nadult+" Child: "+nchild);
        issuedate.setText(ticketdate+" _ "+formatter.format(mDate));
        String time =formatter.format(mDate);
        String times[] = time.split(":");
        int hh = (Integer.parseInt(times[0])+2);
        String mm = times[1];
        validtill.setText(ticketdate+" _ "+hh+":"+mm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ticket_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.searchBus) {
//            startActivity(new Intent(this,HistoryActivity.class));
            return true;
        }  else if (id == R.id.contactUs) {
            Toast.makeText(this, "We will develop contact Us  soon...", Toast.LENGTH_SHORT).show();
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

}
