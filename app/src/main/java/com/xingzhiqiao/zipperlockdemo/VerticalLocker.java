package com.xingzhiqiao.zipperlockdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 竖直锁屏locker
 * Created by shaowenwen on 2018/1/2
 * 一直在绘没有关系，bitmap只生成一次。
 */
public class VerticalLocker extends ZipperLock {
    //背景图片
    private Bitmap bmpBg;
    //拉环
    private Bitmap bmpMask;
    //拉链
    private Bitmap bmpPendant;
    //后面的bitmap
    private Bitmap bmpRezBack;
    //前面的bitmap
    private Bitmap bmpRezFront;
    //完整的拉链
    private Bitmap bmpZipper;
    private Bitmap bmpZipperHalf;
    //后面的canvas
    private Canvas canvasBack;
    //前面的canvas
    private Canvas canvasFront;
    private ImageView imgFront;
    private int offset = 0;
    private Paint bgPaint;

    private UnlockListener unlockListener;

    public VerticalLocker(int width, int height, Context context) {
        super(width, height, context);
    }

    private static final String TAG = "sww";
    public void init(ImageView imgZip, ImageView imgFront, UnlockListener unlockListener) {
        Log.e(TAG, "init: 方法");

        this.imgZipper = imgZip;
        this.imgFront = imgFront;
        //创建
        this.bmpRezBack = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        this.bmpRezFront = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        //“展开的拉链”。
        this.bmpZipper = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.zipper_v_0);
        //不知道是什么？像是展开的拉链。
        this.bmpMask = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.mask_vertical);
        //这个就是拉环儿。
        this.bmpPendant = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.pendant_v_0);
        this.unlockListener = unlockListener;

        this.step = (int) (((double) this.bmpZipper.getHeight()) * 0.3535d);
//        this.step = (int) (((double) this.bmpZipper.getHeight()) * 0.4235d);
        if (this.bmpZipper.getHeight() < this.step + this.height) {
            int nS = (int) (((float) this.step) * (((float) this.height) / ((float) (this.bmpZipper.getHeight() - this.step))));
            int bmpWidth = (int) (((float) this.bmpZipper.getWidth()) * (((float) (this.height + nS)) / ((float) this.bmpZipper.getHeight())));
            if (this.width <= bmpWidth) {
                //修改图片大小；
                this.bmpZipper = Bitmap.createScaledBitmap(this.bmpZipper, bmpWidth, this.height + nS, true);
                this.offset = (this.bmpZipper.getWidth() - this.width) / 2;
            } else {
                this.bmpZipper = Bitmap.createScaledBitmap(this.bmpZipper, this.width, (int) (((float) (this.height + nS)) * (((float) this.width) / ((float) bmpWidth))), true);
            }
//            this.step = (int) (((double) this.bmpZipper.getHeight()) * 0.4235d);
            this.step = (int) (((double) this.bmpZipper.getHeight()) * 0.3535d);
            this.bmpMask = Bitmap.createScaledBitmap(this.bmpMask, this.bmpZipper.getWidth(), this.bmpZipper.getHeight(), true);
            this.bmpPendant = Bitmap.createScaledBitmap(this.bmpPendant, this.bmpZipper.getWidth(), this.bmpZipper.getHeight(), true);
        } else if (this.bmpZipper.getWidth() - this.width > 0) {
            this.offset = (this.bmpZipper.getWidth() - this.width) / 2;
        }
        this.bmpZipperHalf = Bitmap.createBitmap(this.bmpZipper, 0, this.step, this.bmpZipper.getWidth(), this.bmpZipper.getHeight() - this.step);
        this.bmpZipperHalf = Bitmap.createScaledBitmap(this.bmpZipperHalf, this.bmpZipperHalf.getWidth(), this.height, true);
        this.bmpZipper = Bitmap.createBitmap(this.bmpZipper, 0, 0, this.bmpZipper.getWidth(), (int)(this.step*1.3));
        //拉链原始背景（未拉开）
        this.bmpBg = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.bg_zipper_0);

        float a = checkDimensions(this.bmpBg.getWidth(), this.bmpBg.getHeight(), this.width, this.height);
        this.bmpBg = Bitmap.createScaledBitmap(this.bmpBg, (int) (((float) this.bmpBg.getWidth()) * a), (int) (((float) this.bmpBg.getHeight()) * a), true);
        this.pendantWidth = ((int) (((double) this.bmpMask.getWidth()) * 0.16d)) / 2;
        this.pendantLength = (int) (((double) this.bmpMask.getHeight()) * 0.1162d);

        this.limit = 0.8d * ((double) this.height);
        this.bgPaint = new Paint(1);
        //设置图像混合模式为取交集
        this.bgPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        this.bgPaint.setAntiAlias(true);
        this.bgPaint.setDither(true);
        this.canvasBack = new Canvas(this.bmpRezBack);
        //绘制背景骷髅。
        this.canvasBack.drawBitmap(this.bmpBg, 0.0f, 0.0f, null);
        //绘制静态拉链 而且就只是，绘制了一次。
        this.canvasBack.drawBitmap(this.bmpZipperHalf, (float) (-this.offset), 0.0f, null);
        this.bmpBg = Bitmap.createBitmap(this.bmpRezBack, 0, 0, this.bmpRezBack.getWidth(), this.bmpRezBack.getHeight());
        this.imgZipper.setImageBitmap(this.bmpBg);
        this.bmpRezBack = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        this.canvasBack = new Canvas(this.bmpRezBack);
        this.canvasFront = new Canvas(this.bmpRezFront);
        setFrontBitmaps(0.0f);
    }

    /**
     * 根据滑动位置绘制图片
     *
     * @param y
     */
    public void changeImages(float y) {
        this.unlock = ((double) ((this.delta / 2.0f) + y)) >= this.limit;
        if (y < ((float) (this.height - (this.pendantLength / 2)))) {
            setBackBitmaps(y);
            setFrontBitmaps(y);
        }
    }

    public void checkMotionEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "checkMotionEvent: "+event.getY()+"  ---  "+event.getRawY() );
                this.unlock = false;
//                if (event.getX() > ((float) ((this.width / 2) - this.pendantWidth)) && event.getX() < ((float) ((this.width / 2) + this.pendantWidth))
//                        && event.getY() < ((float) this.pendantLength)) {
                if (event.getX() > ((float) ((this.width / 2) - this.pendantWidth)) && event.getX() < ((float) ((this.width / 2) + this.pendantWidth))
                        && event.getY()>460f&&event.getY() < 720f) {
                    this.shouldDrag = true;
                    this.delta = event.getY();
                    return;
                }
                return;

            case MotionEvent.ACTION_MOVE:
                if (this.shouldDrag &&event.getY()-this.delta>0.5f) {
                    Log.e(TAG, "checkMotionEvent: "+event.getY()+"  --- "+this.delta +"  ---  " +(event.getY() - this.delta) );
//                    changeImages(event.getY() - this.delta);
                    changeImages(event.getY() - this.delta);

                    return;
                }
                /*if (this.shouldDrag && event.getY() - this.delta >= 0.5f) {
                    Log.e(TAG, "checkMotionEvent: "+event.getY()+"  --- "+this.delta +"  ---  " +(event.getY() - this.delta) );

                    changeImages(event.getY() - this.delta);
                    return;
                }*/
                return;

            case MotionEvent.ACTION_UP:
                this.shouldDrag = false;
                if (!this.unlock) {
                    this.imgZipper.setImageBitmap(this.bmpBg);
                    setFrontBitmaps(0.0f);
                    return;
                } else {
                    //TODO 解锁处理
                    if (unlockListener != null) {
                        unlockListener.unLock();
                    }
                    return;
                }

            default:
                return;
        }
    }

    /**
     * 重置locker
     */
    public void resetImage() {
        this.imgZipper.setVisibility(View.VISIBLE);
        this.imgFront.setVisibility(View.VISIBLE);
        this.imgZipper.setImageBitmap(this.bmpBg);
        setFrontBitmaps(0.0f);
    }

    /**
     * 回收bitmap
     */
    public void destroyBitmaps() {
        if (this.bmpMask != null) {
            this.bmpMask.recycle();
            this.bmpMask = null;
        }
        if (this.bmpRezBack != null) {
            this.bmpRezBack.recycle();
            this.bmpRezBack = null;
        }
        if (this.bmpRezFront != null) {
            this.bmpRezFront.recycle();
            this.bmpRezFront = null;
        }
        if (this.bmpZipper != null) {
            this.bmpZipper.recycle();
            this.bmpZipper = null;
        }
        if (this.bmpPendant != null) {
            this.bmpPendant.recycle();
            this.bmpPendant = null;
        }
        if (this.bmpBg != null) {
            this.bmpBg.recycle();
            this.bmpBg = null;
        }
        if (this.bmpZipperHalf != null) {
            this.bmpZipperHalf.recycle();
            this.bmpZipperHalf = null;
        }
    }

    /**
     * 设置背景图片
     *
     * @param y
     */
    private void setBackBitmaps(float y) {
        if (this.bmpMask != null && this.bmpBg != null && this.bmpRezBack != null) {
            this.canvasBack.drawColor(0, Mode.CLEAR);
            //mask
            this.canvasBack.drawBitmap(this.bmpMask, (float) (-this.offset), ((float) (-this.step)) + y, new Paint());
            //bg_zipper_0
            this.canvasBack.drawBitmap(this.bmpBg, 0.0f, 0.0f, this.bgPaint);
            this.imgZipper.setImageBitmap(this.bmpRezBack);
        }
    }

    /**
     * 设置前面图片
     *
     * @param y
     */
    private void setFrontBitmaps(float y) {
        if (this.bmpZipper != null && this.bmpPendant != null && this.bmpRezFront != null) {
            this.canvasFront.drawColor(0, Mode.CLEAR);
            //绘制zipper_v_0 拉锁完整图片。
            this.canvasFront.drawBitmap(this.bmpZipper, (float) (-this.offset), ((float) (-this.step)) + y, null);
            //绘制拉锁环儿  pendant_v_0
            this.canvasFront.drawBitmap(this.bmpPendant, (float) (-this.offset), ((float) (-this.step)) + y, null);
            this.imgFront.setImageBitmap(this.bmpRezFront);
        }
    }
}
