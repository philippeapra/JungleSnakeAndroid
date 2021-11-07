package com.firstandroid.snakegameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.TextView;


public class UsernameActivity extends AppCompatActivity {

     EditText UsernameEditText ;
     Button SubmitBtn;
     TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        TextView textView = findViewById(R.id.textView);
        UsernameEditText =  findViewById(R.id.UsernameEditText);
        SubmitBtn = findViewById(R.id.SubmitBtn);
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                String text = UsernameEditText.getText().toString();
                if(text.length()>10){textView.setText("Username is too long" );}
                else {Saveusername();
                Intent z = new Intent(UsernameActivity.this,StartingActivity.class);
                startActivity(z);}
            }
        });
    }
    private void Saveusername(){
        SharedPreferences usernamePref = this.getSharedPreferences("username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = usernamePref.edit();
        editor.putString("usernamekey", UsernameEditText.getText().toString());
        editor.commit();
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
}