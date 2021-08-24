package com.adbu.thirdeye;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Details extends Fragment {


    public Details() {
        // Required empty public constructor
    }

    TextView Speed, Temperature, Latitude, Longitude, Satellite, Altitude;
    double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Speed = (TextView) view.findViewById(R.id.speed);
        Temperature = (TextView) view.findViewById(R.id.temperature);
        Latitude = (TextView) view.findViewById(R.id.latitude);
        Longitude = (TextView) view.findViewById(R.id.longitude);
        Satellite = (TextView) view.findViewById(R.id.satellite);
        Altitude = (TextView) view.findViewById(R.id.altitude);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mailID = auth.getCurrentUser().getEmail();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
        Query query = mref.orderByChild("Email").equalTo(mailID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    UserInfo userInfo = snapshot.getValue(UserInfo.class);

                    latitude = userInfo.Latitude;
                    longitude = userInfo.Longitude;

                    Speed.setText(""+userInfo.Speed);
                    Temperature.setText(""+userInfo.Temperature);
                    Latitude.setText("Latitude : "+latitude);
                    Longitude.setText("Longitude : "+longitude);
                    Satellite.setText(""+userInfo.Satellite);
                    Altitude.setText(""+userInfo.Altitude);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
