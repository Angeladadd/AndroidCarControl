package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class Login extends Activity {
    public Button gesture;
    public Button directionkey;
    public Button voice;
    public Button round;
    public Button gravity;
    public Button face;
    public Button video;
    public TextView bluetooth;
   // public Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_menu);
        directionkey = (Button) findViewById(R.id.dirkeyButton);
        gesture = (Button) findViewById(R.id.gesButton);
        voice = (Button) findViewById(R.id.voiButton);
        round = (Button) findViewById(R.id.roundButton);
        gravity = (Button) findViewById(R.id.gravityButton);
        face = (Button) findViewById(R.id.facButton);
        video = (Button) findViewById(R.id.VideoButton);
        bluetooth = (TextView) findViewById(R.id.bluetoothInfo);
        int count = 0;

        boolean bluetoothinfo = ((MainActivity) getApplication()).bluetoothInfo;

        if (bluetoothinfo) {
            bluetooth.setText("bluetooth access sucessful");
        } else {
            bluetooth.setText("bluetooth access failed");
        }

        sendMessage('S');

        directionkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Directkey.class);
                startActivity(intent);
            }
        });
        gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Gesture.class);
                startActivity(intent);
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Voice.class);
                startActivity(intent);
            }
        });
        round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Roundkey.class);
                startActivity(intent);
            }
        });
        gravity.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Gravity.class);
                startActivity(intent);
            }

        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Video.class);
                startActivity(intent);
            }
        });
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, FaceControl.class);
                startActivity(intent);
            }
        });
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                sendMessage('S');
//            }
//        },0,2000);
//        while(true){
//            count++;
//            if (count >= 20) {sendMessage('S');count = 0;}
//        }
    }

    public boolean sendMessage(char m) {
        try {
            char msg = m;
            ((MainActivity) getApplication()).mmOutputStream.write(msg);
            System.out.println("Data Sent!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}