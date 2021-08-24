package com.adbu.thirdeye;

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


public class Login extends AppCompatActivity {

    Button login;
    EditText password,email;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this ,MainActivity.class );
            startActivity(intent);
            finish();
        }

        login = (Button) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();
            }
        });

    }

    private void userLogin() {

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if(userEmail.isEmpty()){
            email.setError("Email can't be empty.");
            return;
        }

        if(userPassword.isEmpty()){
            password.setError("Password can't be empty.");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login Please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userEmail , userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(Login.this ,MainActivity.class );
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(Login.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public void gotoRegister(View v){

        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }

    public void gotoPasswordReset(View view) {

        Intent intent = new Intent(Login.this, Password_reset.class);
        startActivity(intent);
        finish();
    }

    public void gotoAdmin(View view) {
        Intent intent = new Intent(Login.this, admn_login.class);
        startActivity(intent);
        finish();
    }
}
