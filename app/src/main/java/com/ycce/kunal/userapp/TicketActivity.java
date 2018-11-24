package com.ycce.kunal.userapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import java.util.UUID;
import java.io.UnsupportedEncodingException;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    private ImageView qrimg;

    private String cardNum;
    private String expDate;
    private String cvvNum;
    private ProgressDialog progressDialog;

    public final static int QRcodeWidth = 500 ;
    private Bitmap bitmap;

    private SharedPreferences applicationpreferences;
    private SharedPreferences.Editor editor;

    public  static  final  String MyPref = "BusApp";

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
        paymentLayout= (LinearLayout) findViewById(R.id.paymentactivity);

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
        wallet = (TextView)findViewById(R.id.wallet);
        qrimg = (ImageView)findViewById(R.id.qrimg);

        progressDialog = new ProgressDialog(this);
        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();

        String userId = applicationpreferences.getString("userId","anon");
        Log.d("asa", "userId is: " + userId );
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User Details").child("User").child(userId).child("History");
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
                paymentLayout.setVisibility(View.VISIBLE);
                paymentActivityGateway();

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
                  cardNum = cardNumber.getText().toString();
                  expDate = expiry.getText().toString();
                  cvvNum = cvv.getText().toString();

                  if ((cardNum+expDate+cvvNum).equals("12345678901234560721123")){
                      progressDialog.setMessage("Processing....");
                      progressDialog.show();
                      qrCode();
                  }else {
                      Toast.makeText(getApplicationContext(), "Invalid Card input", Toast.LENGTH_SHORT).show();

                  }



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
        String encode ;
        byte[] str= new byte[0];
            try {
                progressDialog.setMessage("Processing....");
                progressDialog.show();
//            encode = java.util.Base64.getEncoder().encodeToString(str);

            str = ("Fare : "+iFare+"\nSource : "+mSource+"\nDestination : "+mDestination+"\nRoute : "+setroute+
                    "\nNo. of Passengers : "+passengers.getText().toString()+"\nTicket Issue Date : "+issuedate.getText().toString()
                    +"\n Valid Till : "+validtill.getText().toString()).getBytes();

                encode=Base64.encodeToString(str,1);
                bitmap = TextToImageEncode(encode);
                qrimg.setImageBitmap(bitmap);
                paymentLayout.setVisibility(View.GONE);
                ticketLayout.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(), "Transaction Successfull "+"new account balance"+ iFare, Toast.LENGTH_LONG).show();
                Log.d("Tag","hello"+bitmap);
                String dateId = issuedate.getText().toString();
                dateId = dateId.replaceAll("/",":");
                myRef.child( dateId).setValue(encode);
                progressDialog.dismiss();

            } catch (WriterException e) {
            e.printStackTrace();

            } catch (Exception e) {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        Bitmap TextToImageEncode(String Value) throws WriterException {
            BitMatrix bitMatrix;
            try {
                bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null);

            } catch (IllegalArgumentException Illegalargumentexception) {

                return null;
            }
            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();

            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {

                 pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.text_color_icon):getResources().getColor(R.color.bg_user_message);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
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


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Do you really want to get to Booking Activity? \nyour QR Code will get destroy!!! if you have booked any" +
                        "\n Please kindly take Screenshot ")
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
