package com.microstorm.qrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.microstorm.qrcode.zbar.ZbarActivity;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Button btn_hardware;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        BroadcastManager.getInstance(this).addAction("ZBAR", new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("ZBAR".equals(action)) {
                    String content = intent.getStringExtra("content");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.result_title))
                            .setMessage(content)
                            .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(this).destroy("ZBAR");
    }

    void initView() {
        findViewById(R.id.btn_hardware).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //硬件信息
                startActivity(new Intent(MainActivity.this, HardwareActivity.class));
            }
        });
        findViewById(R.id.btn_one_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //条码扫描
//                MyCatureActivity.type = 0;
//                setScan(IntentIntegrator.ONE_D_CODE_TYPES,getString(R.string.b_scan_tips));
                Intent intent = new Intent(MainActivity.this, ZbarActivity.class);
                startActivityForResult(intent, 0);
//                switchSelectedImage();
            }
        });
        findViewById(R.id.btn_two_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //二维码扫描
                MyCatureActivity.type = 1;
                setScan(IntentIntegrator.QR_CODE_TYPES, getString(R.string.q_scan_tips));
            }
        });
        findViewById(R.id.btn_exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExceptionActivity.class));
            }
        });
    }


    void setScan(Collection<String> desiredBarcodeFormats, String tips) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        integrator.setDesiredBarcodeFormats(desiredBarcodeFormats);
        integrator.setCaptureActivity(ScanActivity.class); //设置打开摄像头的Activity
        integrator.setPrompt(tips); //底部的提示文字，设为""可以置空
        integrator.setCameraId(0); //前置或者后置摄像头
        integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }


    String rfCode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (REQUESTCODE_IMAGE == requestCode) {

            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String path = cursor.getString(columnIndex);  //获取照片路径
                cursor.close();
                parsePhoto(path);
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                iv_photo.setImageBitmap(bitmap);
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }

        } else {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                rfCode = scanResult.getContents();
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.result_title))
                        .setMessage(rfCode)
                        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .create()
                        .show();
            }
        }


    }

    private final static int REQUESTCODE_IMAGE = 0x11;

    private void switchSelectedImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUESTCODE_IMAGE);
    }

    public void parsePhoto(final String path){
        Observable<String> observable = new Observable<String>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                // 解析二维码/条码
                String result = QRCodeDecoder.syncDecodeQRCode(path);
                // 要做判空，不然可能会报空指针异常
                result = (null == result)?"":result;
                observer.onNext(result);
            }
        };
        // RxJava 根据图片路径获取ZXing扫描结果的过程执行在io线程,获取到结果后的操作在主线程
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {}
        @Override
        public void onNext(String value) {
            if(TextUtils.isEmpty(value)){
                Toast.makeText(MainActivity.this, "未识别到二维码/条码", Toast.LENGTH_SHORT).show();
            }else {
                // 识别到二维码/条码内容：value
            }
        }
        @Override
        public void onError(Throwable e) {}
        @Override
        public void onComplete() {}
    };



//    /**
//     * 扫描二维码图片的方法,返回结果
//     *
//     * @param path
//     * @return 返回结果
//     */
//    public Result scanningImage(String path) {
//        if (TextUtils.isEmpty(path)) {
//            return null;
//        }
//        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
//        hints.put(DecodeHintType.TRY_HARDER, "UTF8"); // 设置二维码内容的编码
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 先获取原大小
//        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
//        options.inJustDecodeBounds = false; // 获取新的大小
//        int sampleSize = (int) (options.outHeight / (float) 100);
//        if (sampleSize <= 0)
//            sampleSize = 1;
//        options.inSampleSize = sampleSize;
//
//        //获取到bitmap对象(相册图片对象通过path)
//        scanBitmap = BitmapFactory.decodeFile(path, options);
//        //输入bitmap解析的二值化结果(就是图片的二进制形式)
//        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
//        //再把图片的二进制形式转换成,图片bitmap对象
//        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//        //CodaBarReader codaBarReader= new CodaBarReader();    //codaBarReader  二维码
//        try {
//            /**创建MultiFormatReader对象,调用decode()获取我们想要的信息,比如条形码的code,二维码的数据等等.这里的MultiFormatReader可以理解为就是一个读取获取数据的类,最核心的就是decode()方法 */
//            return  new MultiFormatReader().decode(bitmap1,hints);      //识别条形码
//
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//


}
