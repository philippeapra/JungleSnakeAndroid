package com.firstandroid.snakegameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    private Button GreenBtn;
    private Button PinkBtn;
    private Button BlueBtn;
    private Button SpeedSlowBtn;
    private Button SpeedNormalBtn;
    private Button SpeedFastBtn;
    private Button BackBtn;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        GreenBtn = (Button) findViewById(R.id.GreenBtn);
        GreenBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.SnakeColor = "Green";
                sharedPref.edit().putString("Color", MainActivity.SnakeColor).commit();
                GreenBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
                PinkBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                BlueBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
            }
        });
        PinkBtn = (Button) findViewById(R.id.PinkBtn);
        PinkBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.SnakeColor = "Pink";
                sharedPref.edit().putString("Color", MainActivity.SnakeColor).commit();
                GreenBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                PinkBtn.setBackgroundColor(Color.parseColor("#F90075"));
                BlueBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
            }
        });
        BlueBtn = (Button) findViewById(R.id.BlueBtn);
        BlueBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.SnakeColor = "Blue";
                sharedPref.edit().putString("Color", MainActivity.SnakeColor).commit();
                GreenBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                PinkBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                BlueBtn.setBackgroundColor(Color.parseColor("#000AD3"));
            }
        });
        SpeedSlowBtn = (Button) findViewById(R.id.SpeedSlowBtn);
        SpeedSlowBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.delay = 300;
                sharedPref.edit().putInt("Speed", MainActivity.delay).commit();
                SpeedSlowBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
                SpeedNormalBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                SpeedFastBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
            }
        });
        SpeedNormalBtn = (Button) findViewById(R.id.SpeedNormalBtn);
        SpeedNormalBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.delay = 250;
                sharedPref.edit().putInt("Speed", MainActivity.delay).commit();
                SpeedSlowBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                SpeedNormalBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
                SpeedFastBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
            }
        });
        SpeedFastBtn = (Button) findViewById(R.id.SpeedFastBtn);
        SpeedFastBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                MainActivity.delay = 200;
                sharedPref.edit().putInt("Speed", MainActivity.delay).commit();
                SpeedSlowBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                SpeedNormalBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
                SpeedFastBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
            }
        });
        BackBtn = (Button) findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                Intent k = new Intent(SettingsActivity.this,StartingActivity.class);
                startActivity(k);
            }
        });
        GreenBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        PinkBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        BlueBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        if (MainActivity.SnakeColor .equals("Green") ){
        GreenBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
        }
        if (MainActivity.SnakeColor.equals("Pink")){
            PinkBtn.setBackgroundColor(Color.parseColor("#F90075"));
        }
        if (MainActivity.SnakeColor.equals("Blue")){
            BlueBtn.setBackgroundColor(Color.parseColor("#000AD3"));
        }
        SpeedSlowBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        SpeedNormalBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        SpeedFastBtn.setBackgroundColor(Color.parseColor("#D6D7D7"));
        MainActivity.delay = sharedPref.getInt("Speed",250);
        if (MainActivity.delay==300){
            SpeedSlowBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
        }
        if (MainActivity.delay==250){
            SpeedNormalBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
        }
        if (MainActivity.delay==200){
            SpeedFastBtn.setBackgroundColor(Color.parseColor("#B2CC48"));
        }
    }


}