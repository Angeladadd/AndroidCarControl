package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static java.lang.Math.*;

public class Roundkey extends Activity{
    private RelativeLayout buttonlayout;
    private TextView directionInfo;
    private long lastTime = new java.util.Date().getTime();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roundkey_control);
        buttonlayout = (RelativeLayout)findViewById(R.id.buttonlayout);
        directionInfo=(TextView) findViewById(R.id.directionInfo);
        buttonlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ControlButton controlButton = (ControlButton) findViewById(R.id.controlButton);
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    controlButton.draw((int) motionEvent.getX(), (int) motionEvent.getY(), buttonlayout.getWidth());
                    onButtonLayoutClick((int) motionEvent.getX()-controlButton.getOriginalX(), (int) motionEvent.getY()-controlButton.getOriginalY());
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    controlButton.recover();
                    sendMessage('S');
                }
                return true;
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void onButtonLayoutClick(int x, int y){
        if (new java.util.Date().getTime() - lastTime < 100)
            return;
        lastTime = new java.util.Date().getTime();
        if (sqrt(x*x + y*y) > buttonlayout.getWidth()/5.0) {
            double sin = y/sqrt(x*x + y*y);
            double cos = x/sqrt(x*x + y*y);
            if (sin < sin(toRadians(-67.5))) {
                sendMessage('A');
                directionInfo.setText("Going Forward");
            }
            else if (sin < sin(toRadians(-22.5)) && cos > cos(toRadians(-67.5))) {
                sendMessage('Y');
                directionInfo.setText("Turing Right Forward");
            }
            else if (cos > cos(toRadians(22.5))) {
                sendMessage('R');
                directionInfo.setText("Turing Right");
            }
            else if (cos > cos(toRadians(67.5))) {
                sendMessage('U');
                directionInfo.setText("Turing Right Back");
            }
            else if (sin > sin(toRadians(67.5))) {
                sendMessage('B');
                directionInfo.setText("Going Backward");
            }
            else if (sin > sin(toRadians(157.5))) {
                sendMessage('X');
                directionInfo.setText("Turing Left Back");
            }
            else if (cos < cos(toRadians(157.5))) {
                sendMessage('L');
                directionInfo.setText("Turing Left");
            }
            else if (sin < sin(toRadians(-22.5))){
                sendMessage('Z');
                directionInfo.setText("Turing Left Forward");
            }
            else {
                sendMessage('S');
                directionInfo.setText("Stop");
            }
        }
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
        Intent intent = new Intent();
        intent.setClass(Roundkey.this,Login.class);
        startActivity(intent);
        //System.exit(0);
    }
    @SuppressLint("NewApi")
    public void onBackPressed() {
        super.onBackPressed();
        sendMessage('S');
        //System.exit(0);
        finishAfterTransition();
    }


}
