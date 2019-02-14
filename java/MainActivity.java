package com.sunchenge.clientapp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Application {
    private String mylabel ;
    public BluetoothSocket mmSocket;
    public OutputStream mmOutputStream;
    public InputStream mmInputStream;
    public static final int MAX_BUFFER = 15;
    public String getLabel(){
        return mylabel;
    }
    public void setLabel(String s){
        this.mylabel = s;
    }
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mBTAdapter;
    private UUID uuid;
    private BufferedReader pht;
    public boolean bluetoothInfo = false;

    public void onCreate() {
        super.onCreate();
        System.out.println("welcome");
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();

        if(pairedDevices.size()!=0)
            mmDevice = pairedDevices.iterator().next();
        uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothInfo = true;
        }
        catch (Exception e){
            bluetoothInfo = false;
        }
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        if(sendMessage('-')) {
                            Thread.sleep(1000);
                            continue;
                        }
                        mmSocket.connect();
                        mmOutputStream = mmSocket.getOutputStream();
                        mmInputStream = mmSocket.getInputStream();
                        pht=new BufferedReader(new InputStreamReader(mmInputStream));
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    boolean sendMessage(char m){
        try{
            char msg = m;
            mmOutputStream.write(msg);
            System.out.println("Data Sent!");
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
