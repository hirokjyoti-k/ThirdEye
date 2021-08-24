package com.adbu.thirdeye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Password_reset extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

            firebaseAuth = FirebaseAuth.getInstance();
            final Button passwordReset = (Button) findViewById(R.id.passwordReset);
            final EditText email = (EditText) findViewById(R.id.email);

            passwordReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    userEmail = email.getText().toString().trim();

                    if(userEmail.isEmpty()){
                        email.setError("Please enter an email");
                        return;
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(Password_reset.this);
                    progressDialog.setMessage("Sending password reset link to email...");
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(Password_reset.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Password_reset.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(Password_reset.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
            });
    }

    public void goBack(View view) {
        Intent intent = new Intent(Password_reset.this, Login.class);
        startActivity(intent);
        finish();
    }
}
