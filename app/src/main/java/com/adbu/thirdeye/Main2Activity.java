package com.adbu.thirdeye;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerrView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserDetails");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    List<UserInfo> userInfo = (List<UserInfo>) snapshot.getValue(UserInfo.class);
                    recyclerView.setAdapter(new DataAdapter(Main2Activity.this, userInfo));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Main2Activity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
