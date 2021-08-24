package com.adbu.thirdeye;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class admin_view extends AppCompatActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    Marker vechile;
    SupportMapFragment mapFragment;
    String vechile_no;
    double Lat, preLat, Lng, preLng, bearing;
    EditText searchBar;
    Button searchBtn;
    Query query;
    String vNo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        searchBtn = (Button) findViewById(R.id.searchbtn);
        searchBar = (EditText) findViewById(R.id.searchbar);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vNo = searchBar.getText().toString().trim();
                if (TextUtils.isEmpty(vNo)){
                    searchBar.setError("Enter Vehicle No");
                    return;
                }


                DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
                query = mref.orderByChild("Vehicle_No").equalTo(vNo);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            UserInfo userInfo = snapshot.getValue(UserInfo.class);
                            Lat = userInfo.Latitude;
                            Lng = userInfo.Longitude;
                            vechile_no = userInfo.Vehicle_No;
                            mapFragment.getMapAsync(admin_view.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(admin_view.this, ""+databaseError, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();    }
}
