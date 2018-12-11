package com.ycce.kunal.userapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference myRef;

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

    private int walletBalance=500;
    //payment
    private LinearLayout cardLayout;
    private EditText cardNumber;
    private EditText expiry;
    private EditText cvv;
    private TextView bhimUPI;
    private TextView card;
    private TextView net;
    private TextView wallet;
    private TextView bhimclick;
    private TextView netclick;
    private Button pay;
    private Button cancelbtn;
    private Button locateBus;
    private ImageView qrimg;

    private String cardNum;
    private String expDate;
    private String cvvNum;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private   Toolbar toolbar = null;
    private SharedPreferences applicationpreferences;
    private SharedPreferences.Editor editor;

    public  static  final  String MyPref = "BusApp";

    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        paymentLayout= (LinearLayout) findViewById(R.id.paymentactivity);
        locateBus= (Button)findViewById(R.id.locateBus);
        //payment activity
        bhimUPI = (TextView)findViewById(R.id.bhimUPI);
        card = (TextView)findViewById(R.id.creditcard);
        net = (TextView)findViewById(R.id.netBanking);
        bhimclick = (TextView)findViewById(R.id.bhimClick);
        netclick = (TextView)findViewById(R.id.netBanking);
        cardNumber = (EditText)findViewById(R.id.cardNumber);
        expiry = (EditText)findViewById(R.id.expiry);
        cvv = (EditText)findViewById(R.id.cvv);
        cardLayout = (LinearLayout)findViewById(R.id.cardLayout);
        pay = (Button)findViewById(R.id.paybtn);
        cancelbtn = (Button)findViewById(R.id.cancelbtn);
        wallet = (TextView)findViewById(R.id.wallet);
        qrimg = (ImageView)findViewById(R.id.qrimg);

        progressDialog = new ProgressDialog(this);
        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();

        String userId = applicationpreferences.getString("userId","anon");
        Log.d("asa", "userId is: " + userId );
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User Details").child("User").child(userId).child("History");

        locateBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(TicketActivity.this,SearchActivity.class);
                searchIntent.putExtra("route",setroute);
                searchIntent.putExtra("upDown",upDown);
                startActivity(searchIntent);
            }
        });
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
               if (!isNetworkAvailable()){
                   Toast.makeText(TicketActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
               }else {
                   ticketLayout.setVisibility(View.GONE);
                   paymentActivityGateway();
               }

            }
        });
    }

    private void paymentActivityGateway() {
        payButton.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        paymentLayout.setVisibility(View.VISIBLE);


        bhimUPI.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                bhimclick.setVisibility(View.VISIBLE);
                netclick.setVisibility(View.GONE);
                cardLayout.setVisibility(View.GONE);
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardLayout.setVisibility(View.VISIBLE);
                bhimclick.setVisibility(View.GONE);
                netclick.setVisibility(View.GONE);
//code begin
            }
        });

        pay.setText("Pay "+iFare+".00 Rs.");
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if (!isNetworkAvailable()){
                      Toast.makeText(TicketActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                  }else{
                      cardNum = cardNumber.getText().toString();
                      expDate = expiry.getText().toString();
                      cvvNum = cvv.getText().toString();

                      if ((cardNum+expDate+cvvNum).equals("12345678901234560721123")){
                                 progressDialog.setMessage("Processing transaction...");
                                 progressDialog.show();
                                 if (progressDialog.isShowing()){
                                     qrCode();
                                 }

                      }else {
                          Toast.makeText(getApplicationContext(), "Invalid Card input", Toast.LENGTH_SHORT).show();

                      }
                  }



            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentLayout.setVisibility(View.GONE);
                ticketLayout.setVisibility(View.VISIBLE);

                payButton.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
            }
        });

        net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netclick.setVisibility(View.VISIBLE);
                bhimclick.setVisibility(View.GONE);
                cardLayout.setVisibility(View.GONE);
            }
        });


    }

    public void qrCode(){
        pay.setVisibility(View.GONE);
        cancelbtn.setVisibility(View.GONE);
        final String encode ;
        byte[] str= new byte[0];

            str = ("Fare : "+iFare+"\nSource : "+mSource+"\nDestination : "+mDestination+"\nRoute : "+setroute+
                    "\nNo. of Passengers : "+passengers.getText().toString()+"\nTicket Issue Date : "+issuedate.getText().toString()
                    +"\n Valid Till : "+validtill.getText().toString()).getBytes();

        encode=Base64.encodeToString(str,1);
        String dateId = issuedate.getText().toString();
        dateId = dateId.replaceAll("/",":");

        try {
                CreateQr createQr =new CreateQr(TicketActivity.this);
                bitmap = createQr.TextToImageEncode(encode);
                try{
                    ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
                    File file = Environment.getExternalStorageDirectory();
                    file = new File(file+"/TicketQRCode/Images/", dateId+".jpg");
                    OutputStream stream = null;
                    stream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    stream.flush();
                    stream.close();
                    Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                    qrimg.setImageBitmap(bitmap);
                    Log.d("Tag","hello"+bitmap);
                    paymentLayout.setVisibility(View.GONE);
                    ticketLayout.setVisibility(View.VISIBLE);
                    locateBus.setVisibility(View.VISIBLE);
                    myRef.child( dateId).setValue(encode);
                    progressDialog.dismiss();
//                    Log.d("Tag","savedImageURI "+savedImageURI + Uri.parse(file.getPath())+Uri.parse(file.getCanonicalPath())   );
                }catch (Exception e){
                    paymentLayout.setVisibility(View.VISIBLE);
                    locateBus.setVisibility(View.GONE);
                    ticketLayout.setVisibility(View.GONE);
                    pay.setVisibility(View.VISIBLE);
                    cancelbtn.setVisibility(View.VISIBLE);
                    Log.e("Exception ",e.toString());
                }


//                qrimg.setImageBitmap(bitmap);


            }  catch (Exception e) {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Do you really want to get to Booking Activity? ")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                        startActivity(new Intent(TicketActivity.this,MainActivity.class));
                    }

                }).create().show();


    }

}
