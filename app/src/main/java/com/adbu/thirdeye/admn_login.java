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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class admn_login extends AppCompatActivity {

    Button login;
    EditText password,email;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String userEmail,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admn_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(admn_login.this ,admin_view.class );
            startActivity(intent);
            finish();
        }

        login = (Button) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
                Query query = mref.orderByChild("Email").equalTo(userEmail);

                if(userEmail.isEmpty()){
                    email.setError("Email can't be empty.");
                    return;
                }

                if(userPassword.isEmpty()){
                    password.setError("Password can't be empty.");
                    return;
                }

                progressDialog = new ProgressDialog(admn_login.this);
                progressDialog.setMessage("Login Please wait...");
                progressDialog.show();

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            UserInfo userInfo = snapshot.getValue(UserInfo.class);

                            boolean access = userInfo.admin;

                            if(access == true){
                                userLogin();
                            }else{
                                Toast.makeText(admn_login.this, "You Don't Have Admin Access", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(admn_login.this, ""+databaseError, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void userLogin() {

        firebaseAuth.signInWithEmailAndPassword(userEmail , userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(admn_login.this ,admin_view.class );
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(admn_login.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public void gotoUser(View view) {

        Intent intent = new Intent(admn_login.this ,Login.class );
        startActivity(intent);
        finish();
    }
}
