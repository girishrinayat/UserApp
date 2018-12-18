package com.ycce.kunal.userapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText eName;
    private EditText eEmail;
    private EditText ePassword;
    private EditText eConfirmPass;
    private EditText eContact;
    private Button bRegister;

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mConfirmPass;
    private String mContact;

    private boolean validate = false;

    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("User Registration");

        progressDialog = new ProgressDialog(this);


        //firebase auth instance ....
        mAuth = FirebaseAuth.getInstance();

        eName = (EditText) findViewById(R.id.eName);
        eEmail = (EditText) findViewById(R.id.eEmail);
        ePassword = (EditText) findViewById(R.id.ePassword);
        eConfirmPass = (EditText) findViewById(R.id.eConfirmPassword);
        eContact = (EditText) findViewById(R.id.eMobile);
        bRegister = (Button) findViewById(R.id.bregister);

        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading......");
                progressDialog.show();
               mName = eName.getText().toString();
               mEmail = eEmail.getText().toString();
               mPassword = ePassword.getText().toString();
               mConfirmPass = eConfirmPass.getText().toString();
               mContact = eContact.getText().toString();

               if (isValidate()==true){


              register();
               }

            }
        });
    }

    private void register() {
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                                       Log.d(TAG, "createUserWithEmail:success");

                            sendVerificationEmail();


                        } else {

                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
//                                       Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }



    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();
       final String userId =  user.getUid();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            // email sent

                            myRef = database.getReference("User Details").child("User").child(userId);

                            // store in Real time database of firebase
                            myRef.child("Name").setValue(mName);
                            myRef.child("Email").setValue(mEmail);
                            myRef.child("Mobile").setValue(mContact);


                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Email verification link send sucessfully check your inbox.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            FirebaseAuth.getInstance().signOut();
                            //restart this activity
                            Toast.makeText(RegisterActivity.this, "Error while sending verification link check email", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public boolean isValidate() {
        String mobilePattern = "[0-9]{10}";
        //String email1 = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!mName.equals("")&&!mName.equals(null)&&!mPassword.equals("") &&!mPassword.equals(null)
                &&!mConfirmPass.equals(null)&&!mConfirmPass.equals("")&& !mEmail.equals("")&&!mEmail.equals(null)
                &&!mContact.equals("")&&!mContact.equals(null)) {



            if (mEmail.matches(emailPattern)) {
                validate = true;
            }else{
                eEmail.setError("InCorrect Email pattern");
                validate = false;
            }

            if (mContact.matches(mobilePattern)){
                validate = true;
            }else{
                eContact.setError("Incorrect Mobile no.....");
                validate = false;
            }
            if (mConfirmPass.equals(mPassword)){
                validate = true;
            }else{
                eConfirmPass.setError("Password not matched..");
                validate = false;
            }


        }else{

            progressDialog.dismiss();
            if (mName.equals("")||mName.equals(null)){
                eName.setError("Name is empty!!!");
                validate = false;
            }
            if (mPassword.equals("") ||mPassword.equals(null)){
                ePassword.setError("Password is empty!!!");
                validate = false;
            }
            if (mConfirmPass.equals(null)||mConfirmPass.equals("")){
                eConfirmPass.setError("Confirm Pass is Empty!!!");
                validate = false;
            }
            if (mEmail.equals("")||mEmail.equals(null)){
                eEmail.setError("Email is Empty!!!");
                validate = false;
            }
            if (mContact.equals("")||mContact.equals(null)){
                eContact.setError("Mobile Number is Empty!!!");
                validate = false;
            }


        }
        return validate;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
}
