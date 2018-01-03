package com.xingzhiqiao.zipperlockdemo;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by shaowenwen on 2018/1/2
 */
public abstract class ZipperLock {
    public Context context;
    float delta = 0.0f;
    int height;
    ImageView imgZipper;
    double limit;
    int pendantLength;
    int pendantWidth;
    boolean shouldDrag = false;
    int step;
    boolean unlock = false;
    int width;

    public abstract void changeImages(float f);

    public abstract void checkMotionEvent(MotionEvent motionEvent);

    public abstract void destroyBitmaps();

    public abstract void init(ImageView imageView, ImageView imageView2, UnlockListener lockUtils);

    public abstract void resetImage();

    ZipperLock(int width, int height, Context context) {
        this.width = width;
        this.height = height;
        this.context = context;
    }

    /**
     * 获取缩放比例
     *
     * @param bmpWidth     图片宽
     * @param bmpHeight    图片高
     * @param screenWidth  屏幕宽
     * @param screenHeight 屏幕高
     * @return
     */
    float checkDimensions(int bmpWidth, int bmpHeight, int screenWidth, int screenHeight) {
        if (bmpWidth >= screenWidth && bmpHeight >= screenHeight) {
            return Math.max(((float) screenWidth) / ((float) bmpWidth), ((float) screenHeight) / ((float) bmpHeight));
        }
        if (bmpWidth <= screenWidth && bmpHeight <= screenHeight) {
            return Math.max(((float) screenWidth) / ((float) bmpWidth), ((float) screenHeight) / ((float) bmpHeight));
        }
        if (bmpWidth > screenWidth && bmpHeight <= screenHeight) {
            return ((float) screenHeight) / ((float) bmpHeight);
        }
        if (bmpHeight <= screenHeight || bmpWidth > screenWidth) {
            return 1.0f;
        }
        return ((float) screenWidth) / ((float) bmpWidth);
    }
}
