package com.ycce.kunal.userapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    //firebase authentication
    private FirebaseAuth mAuth;

    //xml declare things
    private EditText eUserName;
    private EditText ePassword;
    private TextView tRegister;
    private TextView tforgetPassword;
    private CheckBox cShowPassword;
    private Button bLogin;

    private ProgressDialog progressDialog;

    //Variables
    private String mUserName;
    private String mPassword;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public  static  final  String MyPref = "BusApp";
    public  static  final  String Password = "Password_Key";
    public  static  final  String username = "username_key";
    public  static  final  String userID = "userId";
    SharedPreferences applicationpreferences;
    SharedPreferences.Editor editor;
    Boolean flag;
    String userId =  null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("User Login");

        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = applicationpreferences .edit();
        flag = applicationpreferences .getBoolean("flag", false);

        if(checkAndRequestPermissions()){
            if (flag) {
                ///second time activity
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(i);

            }

        }

        progressDialog = new ProgressDialog(this);

        //creating instance of firebase...
        mAuth = FirebaseAuth.getInstance();

        //geting connection to xml using id
        eUserName = (EditText) findViewById(R.id.eUserName);
        ePassword = (EditText) findViewById(R.id.ePassword);
        tRegister = (TextView) findViewById(R.id.tRegister);
        tforgetPassword = (TextView) findViewById(R.id.tforgetPassword);
        cShowPassword = (CheckBox) findViewById(R.id.cShowPassword);
        bLogin = (Button) findViewById(R.id.bLogin);



        //event perform on login button
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable() ){
                    Toast.makeText(LoginActivity.this, "check internet connection ", Toast.LENGTH_SHORT).show();

                }else{
                progressDialog.setMessage("Loading......");
                progressDialog.show();
                mUserName = eUserName.getText().toString();
                mPassword = ePassword.getText().toString();
                if (!mUserName.equals("")&&!mUserName.equals(null)&&!mPassword.equals("")&&!mPassword.equals(null)) {

                    //user checking through firebase auth
                    mAuth.signInWithEmailAndPassword(mUserName, mPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        userId=user.getUid();
                                        checkIfEmailVerified();

                                     //   Toast.makeText(LoginActivity.this, "Login Sucess", Toast.LENGTH_SHORT).show();
//                                updateUI(user);
//                                        Log.d("User Login",user.toString());


                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        // If sign in fails, display a message to the user.
//                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        // updateUI(null);
                                    }
                                }
                            });
                }else{
                    if (mUserName.equals("")||mUserName.equals(null)){
                        eUserName.setError("UserName cannot be empty!!!");
                        progressDialog.dismiss();
                    }
                    if (mPassword.equals("")||mPassword.equals(null)){
                        ePassword.setError("Password cannot be empty!!!");
                        progressDialog.dismiss();
                    }
                }

            }
            }
    });
            cShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(!b){
                        ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }else{
                        ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    }
                }
            });

            tRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isNetworkAvailable()){
                        Toast.makeText(LoginActivity.this, "check internet connection ", Toast.LENGTH_SHORT).show();

                    }else{
                        if (checkAndRequestPermissions()){
                            progressDialog.setMessage("Loading......");
                            progressDialog.show();
                            finish();
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this, "Please allow the permission", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });

           tforgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isNetworkAvailable() ){
                        Toast.makeText(LoginActivity.this, "check internet connection ", Toast.LENGTH_SHORT).show();

                    }else{
                        if (checkAndRequestPermissions()){
                            progressDialog.setMessage("Loading......");
                            progressDialog.show();
                            finish();
                            startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(LoginActivity.this, "Please allow the permission", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified())
        {
            if (checkAndRequestPermissions()){

                // user is verified, so you can finish this activity or send user to activity which you want.
                finish();


                //first time

                editor.putString(username,mUserName);
                editor.putString(Password,mPassword);
                editor.putString(userID,userId);
                editor.putBoolean("flag", true);
                editor.commit();
                //Login MainActivity
                progressDialog.dismiss();

                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }else{
                Toast.makeText(this, "Please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Please verify your email address check the link in your inbox.", Toast.LENGTH_SHORT).show();

            finish();
            startActivity(new Intent(LoginActivity.this,LoginActivity.class));

        }
    }
    //runtime network state
    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }
    //runtime permission for
    private  boolean checkAndRequestPermissions() {
//        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int internet =  ContextCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

       /* if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }*/
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
       /* if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }*/
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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



