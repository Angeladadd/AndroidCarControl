package com.sunchenge.clientapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechRecognizer;


public class Voice extends Activity {


    private Button VoiceProcess;
    private static String TAG = Voice.class.getSimpleName();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private TextView et_content;
    private com.iflytek.cloud.SpeechRecognizer mIat;// 语音听写
    private RecognizerDialog iatDialog;//听写动画
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_control);



        VoiceProcess=(Button)findViewById(R.id.button);
        et_content = (TextView) findViewById(R.id.directionInfo);
        et_content.setKeyListener(null);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5c0d11ab");
        // 语音听写1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(this, mTtsInitListener);
        // 1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        iatDialog = new RecognizerDialog(this,
                mTtsInitListener);
        VoiceProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                et_content.setText(null);
                mIatResults.clear();
                starWrite();
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


    private void starWrite() {
        // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        // 语音识别应用领域（：iat，search，video，poi，music）
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 接收语言中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 接受的语言是普通话
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        // 设置听写引擎（云端）
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        iatDialog.setListener(mRecognizerDialogListener);
        iatDialog.show();
        Toast.makeText(getApplication(), "请开始说话…", Toast.LENGTH_SHORT).show();

    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if(count ==0)
                parseResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            Toast.makeText(getApplication(), error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
        }

    };

    /**
     * 初始化语音合成监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @SuppressLint("ShowToast")
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                // showTip("初始化失败,错误码：" + code);
                Toast.makeText(getApplicationContext(), "初始化失败,错误码：" + code,
                        Toast.LENGTH_SHORT).show();
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private void parseResult(RecognizerResult results) {
        System.out.println("parseResult");
        String text = parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        System.out.println("FUCK1!");
        //et_content.setSelection(et_content.length());
        //正则化String，去掉标点符号
        String result=resultBuffer.toString().replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        String printed=null;
        String msg=null;
        if(result.length()<2) {
            printed="ERROR!!!";
            et_content.setText(printed);
            return;
        }
        switch (result.substring(0, 2)) {
            case "左转":
                sendMessage('L');
                printed = "Turning Left";
                break;
            case "右转":
                sendMessage('R');
                printed = "Turning Right";
                break;
            case "前进":
                sendMessage('A');
                printed = "Going Forward";
                break;
            case "后退":
                sendMessage('B');
                printed = "Going Back";
                break;
            case "停车":
                sendMessage('S');
                printed = "Stop";
                break;
            default:
                printed = "ERROR!!!";
        }
        et_content.setText(printed);

    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
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
        intent.setClass(Voice.this,Login.class);
        startActivity(intent);
        //System.exit(0);
    }

}