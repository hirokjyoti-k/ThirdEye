package com.adbu.thirdeye;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    BottomNavigationView bottomNavigationView;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        fragment = new Home();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame ,fragment).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottonmanivation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mailID = auth.getCurrentUser().getEmail();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
        Query query = mref.orderByChild("Email").equalTo(mailID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    UserInfo userInfo = snapshot.getValue(UserInfo.class);
                    if(userInfo.accident == true){

                        msg ="Emergency !!! \nA vehicle has met with an accident. At this given location. \nMap link :- https://www.google.com/maps/search/?api=1&query="+ userInfo.Latitude+","+userInfo.Longitude;
                        sendSMS();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void sendSMS() {
        String phoneNo = "CONTROL_CENTER_NUMBER";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.home:
                fragment = new Home();
                break;

            case R.id.details:
                fragment = new Details();
                break;

            case R.id.profile:
                fragment = new Profile();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame ,fragment).commit();
        return true;
    }

    public void checkPermission() {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.SEND_SMS }, 1);

        }
        else {
            //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
}
