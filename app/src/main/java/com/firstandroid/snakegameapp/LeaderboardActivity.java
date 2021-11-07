package com.firstandroid.snakegameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PostProcessor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    ListView listView;
    ArrayList<String> StringsToBeAdded = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        listView = findViewById(R.id.list_view);
        adapter= new ArrayAdapter<String>(this,
                R.layout.k40,StringsToBeAdded)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                if(position==rank)
                {
                    // do something change color
                    row.setBackgroundColor (Color.parseColor("#D9FF5E"));// some color
                    }
                else{
                    row.setBackgroundColor(Color.WHITE);
                }
                return row;
            }
        }
        ;
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child("users");
        DatabaseReference ref = firebaseDatabase.getReference();
        getValue();
        Handler handlerscroll = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(rank+2);
            }
        };
        handlerscroll.postDelayed(runnable,2000);


    }
    private void getValue() {
        Query query = mDatabase.orderByChild("HighScore");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringsToBeAdded.clear();
                int i =(int)snapshot.getChildrenCount()+1;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    i--;
                    if (dataSnapshot.getKey().equals( Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))){
                        rank = i-1;
                    }
                    String Score = (dataSnapshot.child("HighScore").getValue().toString());
                    String Username = ((dataSnapshot.child("username").getValue()).toString());
                    String S = "                     ";
                    String S1 = "#" + i;
                    String S2 = Username;
                    String S3 = Score;
                    String StringToBeAdded =S1+S.substring(0,4-S1.length())
                            + S2+S.substring(0,10-S2.length())+
                            S.substring(0,4-S3.length())+ S3;
                        StringsToBeAdded.add(StringToBeAdded);
//                        arrayListusername.add(Username);
//                    Collections.sort(arrayListint);
                }
                Collections.reverse(StringsToBeAdded);
//                Toast.makeText(LeaderboardActivity.this, "done", Toast.LENGTH_SHORT).show();

                listView.setAdapter(adapter);
                listView.setSmoothScrollbarEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}