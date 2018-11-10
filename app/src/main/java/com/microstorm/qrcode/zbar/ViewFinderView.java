package com.microstorm.qrcode.zbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.microstorm.qrcode.R;

import cn.szx.simplescanner.base.IViewFinder;


/**
 * 覆盖在相机预览上的view，包含扫码框、扫描线、扫码框周围的阴影遮罩等
 */
public class ViewFinderView extends RelativeLayout implements IViewFinder {
    private Rect framingRect;//扫码框所占区域
    private float widthRatio = 0.86f;//扫码框宽度占view总宽度的比例
    private float heightWidthRatio = 0.3f;//扫码框的高宽比
    private int leftOffset = -1;//扫码框相对于左边的偏移量，若为负值，则扫码框会水平居中
    private int topOffset = (int) (150*getContext().getResources().getDisplayMetrics().density);//扫码框相对于顶部的偏移量，若为负值，则扫码框会竖直居中

    private boolean isLaserEnabled = true;//是否显示扫描线
    private static final int[] laserAlpha = {0, 64, 128, 192, 255, 192, 128, 64};
    private int laserAlphaIndex;
    private static final long animationDelay = 80l;
    private final int laserColor = Color.parseColor("#ffcc0000");

    private final int maskColor = Color.parseColor("#60000000");
    private final int borderColor = Color.parseColor("#ffafed44");
    private final int borderStrokeWidth = 6;
    protected int borderLineLength = 100;

    protected Paint laserPaint;
    protected Paint maskPaint;
    protected Paint borderPaint;

    public ViewFinderView(Context context) {
        super(context);
        initDraw();
        initLayout();
    }

    private void initDraw() {
        setWillNotDraw(false);//需要进行绘制

        //扫描线画笔
        laserPaint = new Paint();
        laserPaint.setColor(laserColor);
        laserPaint.setStyle(Paint.Style.FILL);

        //阴影遮罩画笔
        maskPaint = new Paint();
        maskPaint.setColor(maskColor);

        //边框画笔
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderStrokeWidth);
        borderPaint.setAntiAlias(true);
    }

    private void initLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_view_finder, this, true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getFramingRect() == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);

        if (isLaserEnabled) {
            drawLaser(canvas);
        }
    }

    /**
     * 绘制扫码框四周的阴影遮罩
     */
    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();
        Log.e("framingRect","width = " + width);
        Log.e("framingRect","height = " + height);
        Log.e("framingRect","framingRect.top = " + framingRect.top);
        Log.e("framingRect","framingRect.top = " + framingRect.top);
        canvas.drawRect(0, 0, width, framingRect.top, maskPaint);//扫码框顶部阴影
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom, maskPaint);//扫码框左边阴影
        canvas.drawRect(framingRect.right, framingRect.top, width, framingRect.bottom, maskPaint);//扫码框右边阴影
        canvas.drawRect(0, framingRect.bottom, width, height, maskPaint);//扫码框底部阴影
    }

    /**
     * 绘制扫码框的边框
     */
    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();
        Log.e("绘制扫码框的边框","left = " + framingRect.left );//134
        Log.e("绘制扫码框的边框","top = " + framingRect.top );//17
        Log.e("绘制扫码框的边框","right = " + framingRect.right );//1785
        Log.e("绘制扫码框的边框","bottom = " + framingRect.bottom );//100
        Log.e("绘制扫码框的边框","borderLineLength = " + borderLineLength );//100

        // Top-left corner
        Path path = new Path();
        path.moveTo(framingRect.left, framingRect.top + borderLineLength); //50,325
        path.lineTo(framingRect.left, framingRect.top);//50,225
        path.lineTo(framingRect.left + borderLineLength, framingRect.top);//150,225
        canvas.drawPath(path, borderPaint);

        // Top-right corner
        path.moveTo(framingRect.right, framingRect.top + borderLineLength);
        path.lineTo(framingRect.right, framingRect.top);
        path.lineTo(framingRect.right - borderLineLength, framingRect.top);
        canvas.drawPath(path, borderPaint);

        // Bottom-right corner
        path.moveTo(framingRect.right, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.right, framingRect.bottom);
        path.lineTo(framingRect.right - borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);

        // Bottom-left corner
        path.moveTo(framingRect.left, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.left, framingRect.bottom);
        path.lineTo(framingRect.left + borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);
    }

//    public Rect getRotatedRect(int previewWidth, int previewHeight, Rect rect) {
//        Rect rotatedRect = new Rect(rect);
//        if (rotatedRect == null) {
//
//            if (rotationCount == 1) {//若相机图像需要顺时针旋转90度，则将扫码框逆时针旋转90度
//                rotatedRect.left = rect.top;
//                rotatedRect.top = previewHeight - rect.right;
//                rotatedRect.right = rect.bottom;
//                rotatedRect.bottom = previewHeight - rect.left;
//            } else if (rotationCount == 2) {//若相机图像需要顺时针旋转180度,则将扫码框逆时针旋转180度
//                rotatedRect.left = previewWidth - rect.right;
//                rotatedRect.top = previewHeight - rect.bottom;
//                rotatedRect.right = previewWidth - rect.left;
//                rotatedRect.bottom = previewHeight - rect.top;
//            } else if (rotationCount == 3) {//若相机图像需要顺时针旋转270度，则将扫码框逆时针旋转270度
//                rotatedRect.left = previewWidth - rect.bottom;
//                rotatedRect.top = rect.left;
//                rotatedRect.right = previewWidth - rect.top;
//                rotatedRect.bottom = rect.right;
//            }
//        }
//
//        return rotatedRect;
//    }


    /**
     * 绘制扫描线
     */
    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();

        laserPaint.setAlpha(laserAlpha[laserAlphaIndex]);
        laserAlphaIndex = (laserAlphaIndex + 1) % laserAlpha.length;
        int middle = framingRect.height() / 2 + framingRect.top;
        canvas.drawRect(framingRect.left + 1, middle - 1, framingRect.right - 1, middle + 1, laserPaint);

        //区域刷新
        postInvalidateDelayed(animationDelay,
                framingRect.left,
                framingRect.top,
                framingRect.right,
                framingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    /**
     * 设置framingRect的值（扫码框所占的区域）
     */
    public synchronized void updateFramingRect() {
        Point viewSize = new Point(getWidth(), getHeight());
        int width, height;
        width = (int) (getWidth() * widthRatio);
        height = (int) (heightWidthRatio * width);

        int left, top;
        if (leftOffset < 0) {
            left = (viewSize.x - width) / 2;//水平居中
        } else {
            left = leftOffset;
        }
        if (topOffset > 0) {
            top = (viewSize.y - height) / 2;//竖直居中
        } else {
            top = topOffset;
        }
        Log.e("updateFramingRect",left + "");
        Log.e("updateFramingRect",top + "");
        Log.e("updateFramingRect",left + width + "");
        Log.e("updateFramingRect",top + height + "");
        framingRect = new Rect(left, top, left + width, top + height);
        framingRect = new Rect(left, top, left + width, top + height);
    }

    public Rect getFramingRect() {
        return framingRect;
    }
}