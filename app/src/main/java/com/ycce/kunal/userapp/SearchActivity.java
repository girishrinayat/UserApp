package com.ycce.kunal.userapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {

    private int startpos;
    private int desPos;
    private int nadult;
    private int nchild;
    private String route;
    private String upDown;
    private String ticketdate;

    private Button bBack;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();

        if (extras!=null) {
            startpos = extras.getInt("startpos");
            desPos = extras.getInt("desPos");
            nadult = extras.getInt("Adult");
            nchild = extras.getInt("Child");
            route = extras.getString("route");
            upDown = extras.getString("upDown");
            ticketdate = extras.getString("date");
        }
       /* startpos desPos  nadult  nchild  route   upDown   ticketdate*/


        bBack = (Button) findViewById(R.id.bBack);
        text =(TextView) findViewById(R.id.text);

        text.setText(" startpos :"+startpos+"\n desPos :"+desPos+"\n nadult :"+nadult+"\n nchild :"+nchild+"\n route :"+route+"\n upDown :"+upDown+"\n ticketdate :"+ticketdate);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(SearchActivity.this,MainActivity.class));
            }
        });
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
