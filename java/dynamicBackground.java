package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

@SuppressLint("ViewConstructor")
public class dynamicBackground extends SurfaceView {
    RevImageThread revImageThread;
    public static SurfaceHolder surfaceHolder;
    private MyHandler handler;
    private int width = 1280;
    private int height = 720;
    private AvcDecoder avcDecoder;

    private static String ip; //服务端ip
    private static int BROADCAST_PORT = 9898;
    private static String BROADCAST_IP = "224.0.0.1";
    InetAddress inetAddress = null;
    Thread senderThread = null;



    /*发送广播端的socket*/
    MulticastSocket multicastSocket = null;
    /*发送广播的按钮*/
    private volatile boolean isRuning = true;

    public dynamicBackground(SurfaceHolder holder,Context context) throws UnknownHostException {
        super(context);
        surfaceHolder = holder;
        System.out.println("holder.");
        ip = getIPAddress(context);
        System.out.println("get ip");

        // 设置播放时打开屏幕
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                avcDecoder = new AvcDecoder(width, height, holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                multicastSocket.close();
                revImageThread.close();
                isRuning = false;
                avcDecoder.close();
            }
        });

        handler = new MyHandler();
        revImageThread = new RevImageThread(handler);

        //广播ip地址

        try {
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
            multicastSocket.setTimeToLive(1);
            multicastSocket.joinGroup(inetAddress);

        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println(ip);

        senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket dataPacket = null;
                //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
                byte[] data = ip.getBytes();
                dataPacket = new DatagramPacket(data, data.length, inetAddress, BROADCAST_PORT);
                while (true) {
                    if (isRuning) {
                        try {
                            multicastSocket.send(dataPacket);
                            Thread.sleep(3000);
                            System.out.println("再次发送ip地址广播:.....");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        senderThread.start();


        new Thread(revImageThread).start();
    }

    /*@Override
    public void surfaceCreated(SurfaceHolder holder) {
        avcDecoder = new AvcDecoder(width, height, holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        multicastSocket.close();
        revImageThread.close();
        isRuning = false;
        avcDecoder.close();
    }*/

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            byte[] input = (byte[]) msg.obj;
            int length = msg.arg1;
            avcDecoder.offerDecoder(input, length);
        }
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
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

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
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


