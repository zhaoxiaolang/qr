package com.microstorm.qrcode;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/8/15.
 */

public class HardwareActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hardware);
        CommonUtils.setTitle(this,getString(R.string.hardware),"",this);
        getInfo();
    }

    void getInfo () {
        //BOARD 主板
        String phoneInfo = "BOARD: " + android.os.Build.BOARD;
        phoneInfo += "\nBOOTLOADER: " + android.os.Build.BOOTLOADER;
//BRAND 运营商
        phoneInfo += "\nBRAND: " + android.os.Build.BRAND;
        phoneInfo += "\nCPU_ABI: " + android.os.Build.CPU_ABI;
        phoneInfo += "\nCPU_ABI2: " + android.os.Build.CPU_ABI2;

//DEVICE 驱动
        phoneInfo += "\nDEVICE: " + android.os.Build.DEVICE;
//DISPLAY Rom的名字 例如 Flyme 1.1.2（魅族rom） &nbsp;JWR66V（Android nexus系列原生4.3rom）
        phoneInfo += "\nDISPLAY: " + android.os.Build.DISPLAY;
//指纹
        phoneInfo += "\nFINGERPRINT: " + android.os.Build.FINGERPRINT;
//HARDWARE 硬件
        phoneInfo += "\nHARDWARE: " + android.os.Build.HARDWARE;
        phoneInfo += "\nHOST: " + android.os.Build.HOST;
        phoneInfo += "\nID: " + android.os.Build.ID;
//MANUFACTURER 生产厂家
        phoneInfo += "\nMANUFACTURER: " + android.os.Build.MANUFACTURER;
//MODEL 机型
        phoneInfo += "\nMODEL: " + android.os.Build.MODEL;
        phoneInfo += "\nPRODUCT: " + android.os.Build.PRODUCT;
        phoneInfo += "\nRADIO: " + android.os.Build.RADIO;
        phoneInfo += "\nRADITAGSO: " + android.os.Build.TAGS;
        phoneInfo += "\nTIME: " + android.os.Build.TIME;
        phoneInfo += "\nTYPE: " + android.os.Build.TYPE;
        phoneInfo += "\nUSER: " + android.os.Build.USER;
//VERSION.RELEASE 固件版本
        phoneInfo += "\nVERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += "\nVERSION.CODENAME: " + android.os.Build.VERSION.CODENAME;
//VERSION.INCREMENTAL 基带版本
        phoneInfo += "\nVERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL;
//VERSION.SDK SDK版本
        phoneInfo += "\nVERSION.SDK: " + android.os.Build.VERSION.SDK;
        phoneInfo += "\nVERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT;
        TextView tvInfo = (TextView) findViewById(R.id.tv_hardware_info);
        tvInfo.setText(phoneInfo);
        tvInfo.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
