package com.firstandroid.snakegameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartingActivity extends AppCompatActivity {

    private Button PlayBtn;
    private Button SettingsBtn;
    private Button LeaderboardBtn;
    private TextView WelcomeTV;
    public static MediaPlayer buttonclicksoundeffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        StartingActivity.buttonclicksoundeffect = MediaPlayer.create(StartingActivity.this,R.raw.buttonclick);
        SharedPreferences usernamePref = getSharedPreferences("username", MODE_PRIVATE);
        String username = usernamePref.getString("usernamekey", "");
        WelcomeTV = findViewById(R.id.WelcomeTV);
        WelcomeTV.setTextColor(Color.parseColor("#B2CC48"));
        WelcomeTV.setText("Welcome " + username);
        PlayBtn = (Button) findViewById(R.id.PlayBtn);
        PlayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonclicksoundeffect.start();
                Intent k = new Intent(StartingActivity.this,MainActivity.class);
                startActivity(k);
            }
        });
        SettingsBtn = (Button) findViewById(R.id.SettingsBtn);
        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonclicksoundeffect.start();
                Intent k = new Intent(StartingActivity.this,SettingsActivity.class);
                startActivity(k);
            }
        });
        LeaderboardBtn = (Button) findViewById(R.id.LeaderboardBtn);
        LeaderboardBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonclicksoundeffect.start();
                Intent q = new Intent(StartingActivity.this,LeaderboardActivity.class);
                startActivity(q);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //
//        SharedPreferences usernamePref2 = this.getSharedPreferences("username", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = usernamePref2.edit();
//        editor.putString("usernamekey", "");
//        editor.commit();
        //
        SharedPreferences usernamePref = getSharedPreferences("username", MODE_PRIVATE);
        String username = usernamePref.getString("usernamekey", ""); //"" is the default value
        if (username.equals("")){
            Intent p = new Intent(StartingActivity.this,UsernameActivity.class);
            startActivity(p);
        }

    }
}