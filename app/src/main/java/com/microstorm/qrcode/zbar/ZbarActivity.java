package com.microstorm.qrcode.zbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.microstorm.qrcode.CommonUtils;
import com.microstorm.qrcode.R;

import cn.szx.simplescanner.zbar.Result;
import cn.szx.simplescanner.zbar.ZBarScannerView;


/**
 * 最简单的使用示例
 */
public class ZbarActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private static final String TAG = "ZbarActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 0;
    private ZBarScannerView zBarScannerView;
    private Handler handler = new Handler();
    MediaPlayer player = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_zbar_capture);
//        CommonUtils.setTitle(this,getString(R.string.barcode_scanner),"",this);
        initView();
        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);

    }

    private void initView() {
        ViewGroup container = (ViewGroup) findViewById(R.id.container);

        //ViewFinderView是根据需求自定义的视图，会被覆盖在相机预览画面之上，通常包含扫码框、扫描线、扫码框周围的阴影遮罩等
        zBarScannerView = new ZBarScannerView(this, new ViewFinderView(this), this);
        //zBarScannerView.setShouldAdjustFocusArea(true);//自动调整对焦区域

        container.addView(zBarScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            zBarScannerView.startCamera();//打开系统相机，并进行基本的初始化
        } else {//没有相机权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        zBarScannerView.stopCamera();//释放相机资源等各种资源
    }

    @Override
    public void handleResult(Result rawResult) {
        player.start();
//        Intent intent = new Intent("ZBAR");
//        intent.putExtra("content",rawResult.getContents());
//        sendBroadcast(intent);
//        finish();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.result_title))
                .setMessage(rawResult.getContents()+"")
                .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {// 中间级

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                       finish();
                    }
                })
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //        2秒后再次识别
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                zBarScannerView.getOneMoreFrame();//再获取一帧图像数据进行识别
                            }
                        }, 2000);
                    }
                })
                .create()
                .show();


    }

}