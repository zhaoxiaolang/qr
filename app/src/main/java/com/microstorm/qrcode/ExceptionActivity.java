package com.microstorm.qrcode;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/16.
 */

public class ExceptionActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView;
    //获取剪贴板管理器：
    ClipboardManager cm;
    ClipData mClipData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);
        CommonUtils.setTitle(this, getString(R.string.exception), getString(R.string.copy), this);
        textView = (TextView) findViewById(R.id.tv_exception_info);
        readThread();
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }


    void readThread () {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String path = Environment.getExternalStorageDirectory().toString()+"/1crash/";
                File file = new File(path);
                List<File> fileList = new ArrayList<>();
                if (file.exists()) {
                    File[] files = file.listFiles();
                    for (int i = files.length -1 ; i > 0; i--) {
                        String txt_path = files[i].getAbsolutePath();
                        Log.e("name", txt_path);
                        Message message = new Message();
                        message.what = READ_TXT_UPDATE_UI;
                        message.obj = "path:" + files[i].getAbsolutePath() + "\n\n" +  ReadTxtFromSDCard(txt_path);
                        mHandler.sendMessage(message);
                        break;
//                fileList.add(files[i]);
                    }
                }
            }
        }.start();
    }

    //这是这篇的重点，按ctrl+f关注input的操作
    private String ReadTxtFromSDCard(String url){

        StringBuilder sb = new StringBuilder("");
        //判断是否有读取权限
        if(Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)){

            //打开文件输入流
            try {
                FileInputStream input = new FileInputStream(url);
                byte[] temp = new byte[1024];

                int len = 0;
                //读取文件内容:
                while ((len = input.read(temp)) > 0) {
                    sb.append(new String(temp, 0, len));
                }
                //关闭输入流
                input.close();
            } catch (java.io.IOException e) {
                Log.e("ReadTxtFromSDCard","ReadTxtFromSDCard");
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back :
                finish();
                break;
            case R.id.tv_right :
                // 创建普通字符型ClipData
                mClipData = ClipData.newPlainText("Exception", readText);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
        }
    }

    String readText ;

    private final int READ_TXT_UPDATE_UI = 0x11;
    Handler mHandler = new Handler(Looper.myLooper()){
        public void handleMessage(Message msg){
            //process incoming message here
            switch (msg.what) {
                case READ_TXT_UPDATE_UI :
                    readText = (String) msg.obj;
                    textView.setText(readText);
                    textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                    break;
            }
        }
    };
}
