package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.UnknownHostException;


public class Directkey extends Activity{

    private Button turnleft;
    private Button forward;
    private Button turnright;
    private Button backward;
    private Button stop;
    //public SurfaceView tmp_video;
    //private dynamicBackground background;

    private int fast_flag=0;

    private TextView directionInfo;
    private Switch fastInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directkey_control);


        turnleft=(Button)findViewById(R.id.turnleftB);
        forward=(Button)findViewById(R.id.forwardB);
        turnright=(Button)findViewById(R.id.turnrightB);
        backward=(Button)findViewById(R.id.backwardB);
        stop=(Button)findViewById(R.id.stopB);
        directionInfo=(TextView) findViewById(R.id.directInfo);
        fastInfo=(Switch)findViewById(R.id.fastSwitch);
        //tmp_video= (SurfaceView)findViewById(R.id.videoView) ;
        //System.out.println("prepare to create background.....");
//        try {
//            background = new dynamicBackground(tmp_video.getHolder(),this);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        System.out.println("create background.....");

        fastInfo.setChecked(false);

        fastInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    fast_flag = 1;
                } else {
                    fast_flag = 0;
                }
            }
        });


        turnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fast_flag == 1) {
                    directionInfo.setText("Turning Left Speed: Fast");
                    sendMessage('L');
                    System.out.println('L');
                }
                else{
                    directionInfo.setText("Turning Left Speed: Slow");
                    sendMessage('l');
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fast_flag == 1) {
                    directionInfo.setText("Going Forward Speed: Fast");
                    sendMessage('A');
                }
                else{
                    directionInfo.setText("Going Forward Speed: Slow");
                    sendMessage('a');
                }
            }
        });
        turnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fast_flag == 1) {
                    directionInfo.setText("Turning Right Speed: Fast");
                    sendMessage('R');
                }
                else{
                    directionInfo.setText("Turning Right Speed: Slow");
                    sendMessage('r');
                }
            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fast_flag == 1) {
                    directionInfo.setText("Going Backward Speed: Fast");
                    sendMessage('B');
                }
                else {
                    directionInfo.setText("Going Backward Speed: Slow");
                    sendMessage('b');
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directionInfo.setText("Stop");
                sendMessage('S');
            }
        });


    }
    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendMessage('S');
        finishAfterTransition();
        //System.exit(0);
    }
    public boolean sendMessage(char m)
    {
        try{
            char msg = m;
            ((MainActivity)getApplication()).mmOutputStream.write(msg);
            System.out.println("Data Sent!");
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void returnBack(View view){
        sendMessage('S');
        Intent intent = new Intent();
        intent.setClass(Directkey.this,Login.class);
        startActivity(intent);
    }

}