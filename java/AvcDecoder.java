package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;


public class AvcDecoder
{

    private MediaCodec mediaCodec;
    private int TIMEOUT_USEC = -1;
    int m_width;
    int m_height;
    int i=0;
    private Surface surface;

    @SuppressLint("NewApi")
    public AvcDecoder(int width, int height, Surface surface) {
        this.m_width=width;
        this.m_height=height;
        this.surface=surface;

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(
                "video/avc", m_width, m_height);
        try {
            mediaCodec = MediaCodec.createDecoderByType("video/avc");//这里是建立的解码器
        }catch(Exception e){}
        mediaCodec.configure(mediaFormat, surface, null, 0);//注意上面编码器的注释，看看区别
        mediaCodec.start();

    }

    @SuppressLint("NewApi")
    public void close() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void offerDecoder(byte[] input,int length)
    {
        System.out.println(length);
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);//获取输入缓冲区的索引
        ByteBuffer inputBuffers[]=mediaCodec.getInputBuffers();
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(input,0,length);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, i * 1000000 / 30, 0);//四个参数，第一个是输入缓冲区的索引，第二个是放入的数据大小，第三个是时间戳，保证递增就是
            i++;
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        while (outputBufferIndex >= 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);//释放缓冲区解码的数据到surfaceview，一般到了这一步，surfaceview上就有画面了
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }


    }




}