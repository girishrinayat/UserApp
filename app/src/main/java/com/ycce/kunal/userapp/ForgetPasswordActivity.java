package com.ycce.kunal.userapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText eUserName;
    private Button bLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setTitle("Forget Password");

        eUserName = (EditText) findViewById(R.id.eUserName);
        bLogin = (Button) findViewById(R.id.bLogin);

        progressDialog = new ProgressDialog(this);

        //event perform on login button
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("wait while processing......");
                progressDialog.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(eUserName.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ForgetPasswordActivity.this, "Email was successfully sent on your Emailplease check the inbox.", Toast.LENGTH_LONG).show();
                                    Log.d("", "Email sent.");
                                    finish();
                                    startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(ForgetPasswordActivity.this, "Error while sending email or Please check the email address"
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}
