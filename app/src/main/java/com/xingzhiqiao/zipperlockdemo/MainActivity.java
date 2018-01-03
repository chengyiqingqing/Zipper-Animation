package com.xingzhiqiao.zipperlockdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * MainActivity
 * Created by shaowenwen on 2018/1/2.
 */
public class MainActivity extends Activity implements View.OnTouchListener {

    //背景kull和静态拉锁。
    private ImageView imgZipper;
    //动态的那部分图片；
    private ImageView imgFront;
    //抽象类。
    private ZipperLock mZipperLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_lock);
        initLocker();
    }

    private void initLocker() {

        //用于获取手机屏幕的大小。
        DisplayMetrics metrics = new DisplayMetrics();
        //所取出的屏幕信息（宽高值等），就存放在metrics里面.
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.mZipperLock = new VerticalLocker(metrics.widthPixels, metrics.heightPixels, this);
        //分别给他们两个设置监听动画。
        this.imgZipper = (ImageView) findViewById(R.id.imgZipper);
        this.imgZipper.setOnTouchListener(this);
        this.imgFront = (ImageView) findViewById(R.id.imgFront);
        this.imgFront.setOnTouchListener(this);
        //调用ZipperLock的类的init方法。
        this.mZipperLock.init(this.imgZipper, this.imgFront, new UnlockListener() {
            @Override
            public void unLock() {
                finish();
            }
        });

    }

    /**
     * 调用onTouch方法。
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mZipperLock.checkMotionEvent(event);
        return true;
    }

}
