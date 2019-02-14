package com.sunchenge.clientapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RevImageThread implements Runnable {

    public DatagramSocket socket;

    //向UI线程发送消息
    private Handler handler;
    private boolean isRunning;
    public RevImageThread(Handler handler){
        this.handler = handler;
        isRunning = true;
    }

    public void close()
    {
        isRunning = false;
        socket.close();
    }
    public void run()
    {
        try {
            socket = new DatagramSocket(9000);
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        while(isRunning == true){

            try {
                Log.e("start", "get UDP!");
                byte[] buf = new byte[65536];//接受内容的大小，注意不要溢出
                DatagramPacket packet = new DatagramPacket(buf, buf.length);//定义一个接收的包
                socket.receive(packet);
                Message msg =handler.obtainMessage();
                int index;
                for(index=packet.getData().length-1;index>=3;index-=4)
                {
                    if(((int)packet.getData()[index]+(int)packet.getData()[index-1]+(int)packet.getData()[index-2]+(int)packet.getData()[index-3])!=0)
                        break;
                }
                System.out.println(index);
                msg.obj=packet.getData();
                msg.arg1=index;
                handler.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
