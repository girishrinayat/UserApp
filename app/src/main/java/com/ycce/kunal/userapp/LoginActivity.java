package com.ycce.kunal.userapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    public  static  final  String MyPref = "BusApp";
    public  static  final  String Password = "Password_Key";
    public  static  final  String username = "username_key";
    SharedPreferences applicationpreferences;
    SharedPreferences.Editor editor;
    Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("User Login");
        applicationpreferences = getSharedPreferences(MyPref,MODE_PRIVATE);

        editor = applicationpreferences .edit();


        flag = applicationpreferences .getBoolean("flag", false);


        if (flag) {
            ///second time activity
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            finish();
            startActivity(i);

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
                    progressDialog.setMessage("Loading......");
                    progressDialog.show();
                    finish();
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                }
            });

           tforgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setMessage("Loading......");
                    progressDialog.show();
                    finish();
                    startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                    progressDialog.dismiss();
                }
            });


    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();


            //first time

            editor.putString(username,mUserName);
            editor.putString(Password,mPassword);
            editor.putBoolean("flag", true);
            editor.commit();
            //Login MainActivity
            progressDialog.dismiss();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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



