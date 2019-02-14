package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


public class Video extends Activity {
    RevImageThread revImageThread;
    public static SurfaceView sView;
    public static SurfaceHolder surfaceHolder;
    private MyHandler handler;
    private int width=1280;
    private int height=720;
    private  AvcDecoder avcDecoder;

    private static String ip;
    private static int BROADCAST_PORT=9898;
    private static String BROADCAST_IP="224.0.0.1";
    InetAddress inetAddress=null;
    Thread senderThread=null;

    MulticastSocket multicastSocket=null;

    private volatile boolean isRuning= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);


        sView=(SurfaceView)findViewById(R.id.videoview);
        surfaceHolder = sView.getHolder();
        ip = getIPAddress(this);


        surfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                avcDecoder=new AvcDecoder(width,height,holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                avcDecoder.close();
            }
        });

        handler = new MyHandler();
        revImageThread = new RevImageThread(handler);


        try
        {
            inetAddress=InetAddress.getByName(BROADCAST_IP);
            multicastSocket=new MulticastSocket(BROADCAST_PORT);
            multicastSocket.setTimeToLive(1);
            multicastSocket.joinGroup(inetAddress);
            System.out.println("哦客户端套接字加入组播");


        }catch(Exception e)
        {
            e.printStackTrace();

        }
        System.out.println(ip);

        senderThread=new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket dataPacket = null;

                byte[] data = ip.getBytes();
                dataPacket = new DatagramPacket(data, data.length, inetAddress,BROADCAST_PORT);
                while(true)
                {
                    if(isRuning)
                    {
                        try
                        {
                            multicastSocket.send(dataPacket);
                            Thread.sleep(3000);
                            System.out.println("哦再次发送ip地址广播:....."+ip);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        senderThread.start();



        new Thread(revImageThread).start();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        multicastSocket.close();
        revImageThread.close();
        isRuning = false;
        //System.exit(0);
        finishAfterTransition();
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            byte[] input =(byte[])msg.obj;
            int length=msg.arg1;
            avcDecoder.offerDecoder(input,length);
        }
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            }
        } else {

        }
        return null;
    }

    /**
     *
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


}