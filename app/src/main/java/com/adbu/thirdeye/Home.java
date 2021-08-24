package com.adbu.thirdeye;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class Home extends Fragment implements OnMapReadyCallback {


    public Home() {
        // Required empty public constructor
    }

    GoogleMap mMap;
    Marker vechile;
    SupportMapFragment mapFragment;
    String vechile_no;
    double Lat, preLat, Lng, preLng, bearing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String mailID = auth.getCurrentUser().getEmail();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
        Query query = mref.orderByChild("Email").equalTo(mailID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    UserInfo userInfo = snapshot.getValue(UserInfo.class);
                    Lat = userInfo.Latitude;
                    Lng = userInfo.Longitude;
                    vechile_no = userInfo.Vehicle_No;

                    mapFragment.getMapAsync(Home.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // remove a marker if available
        if(vechile!=null){
            vechile.remove();
        }

        bearing = getBearing(Lat, Lng ,preLat, preLng);

        preLat = Lat;
        preLng = Lng;

        // Add a marker in location and move the camera
        LatLng location = new LatLng(Lat,Lng);
        vechile = mMap.addMarker(new MarkerOptions()
                .rotation((float) bearing)
                .position(location)
                .title(vechile_no)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .flat(true)
                .anchor(0.5f, 0.5f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));

    }


    public double getBearing(double lat, double lng, double preLat, double preLng) {

        double bearing, x, y;

        x = Math.cos(lat) * Math.sin(lng - preLng);

        y = ( Math.cos(preLat) * Math.sin(lat) ) - ( Math.sin(preLat) * Math.cos(lat) ) * Math.cos(lng - preLng);

        bearing = Math.atan2(x,y);

        bearing = Math.toDegrees(bearing);

        return bearing;
    }
}
