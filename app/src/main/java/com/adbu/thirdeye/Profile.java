package com.adbu.thirdeye;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class Profile extends Fragment implements View.OnClickListener {


    public Profile() {
        // Required empty public constructor
    }

    Button logout;
    TextView name,email,vehicleNo;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.email);
        vehicleNo = (TextView) view.findViewById(R.id.vehicleNo);
        logout = (Button) view.findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();
        String mailID = firebaseAuth.getCurrentUser().getEmail();
        logout.setOnClickListener(this);
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
        Query query = mref.orderByChild("Email").equalTo(mailID);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserInfo userInfo = snapshot.getValue(UserInfo.class);

                    name.setText(""+userInfo.Name);
                    email.setText(""+userInfo.Email);
                    vehicleNo.setText(""+userInfo.Vehicle_No);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });

        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity() ,Login.class );
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
