package com.example.b3326.camerapreviewcrop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.constraint.ConstraintLayout.LayoutParams;
//import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class MainActivity extends Activity implements SensorEventListener {

    private Preview mPreview;
    private ImageView mTakePicture;
    private TouchView mView;

    private boolean mAutoFocus = true;

    private boolean mFlashBoolean = false;

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mInitialized = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private Rect rec = new Rect();
    Bitmap cropImage;
    private int mScreenHeight;
    private int mScreenWidth;

    private boolean mInvalidate = false;

    private File mLocation = new File(Environment.
            getExternalStorageDirectory(), "DCIM/Camera/test.jpg");
    // this is the autofocus call back
    private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

        public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
            //Wait.oneSec();

            if (autoFocusSuccess) {// 对焦成功
                Log.d("WillyCheng","Focus!");
                //\
            } else {// 对焦失败
                Log.i("WillyCheng", "Focus Failed");
                //失败就画个红框框
            }

            mAutoFocus = true;
        }
    };
    // I am not using this in this example, but its there if you want
    // to turn on and off the flash.
    private OnClickListener flashListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mFlashBoolean) {
                mPreview.setFlash(false);
            } else {
                mPreview.setFlash(true);
            }
            mFlashBoolean = !mFlashBoolean;
        }

    };
    // This method takes the preview image, grabs the rectangular
    // part of the image selected by the bounding box and saves it.
    // A thread is needed to save the picture so not to hold the UI thread.
    private OnClickListener previewListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            /*if (mAutoFocus)*/ {

                mAutoFocus = false;

                //mPreview.setCameraFocus(myAutoFocusCallback);
                Wait.oneSec();
                Thread tGetPic = new Thread(new Runnable() {
                    public void run() {
                        long pre=System.currentTimeMillis();
                        Double[] ratio = getRatio();
                        int left = (int) (ratio[1] * (double) mView.getmLeftTopPosX());
                        // 0 is height
                        int top = (int) (ratio[0] * (double) mView.getmLeftTopPosY());

                        int right = (int) (ratio[1] * (double) mView.getmRightBottomPosX());

                        int bottom = (int) (ratio[0] * (double) mView.getmRightBottomPosY());
                      //  Log.d("WillyCheng","r-l="+(right-left));

                        mPreview.setCameraFocus(myAutoFocusCallback);

                        cropImage =mPreview.getPic(left, top, right, bottom);
                        savePhoto(cropImage);
                        mAutoFocus = true;
                        ByteArrayOutputStream bs=new ByteArrayOutputStream();
                        cropImage.compress(CompressFormat.JPEG,100,bs);
                        Intent intent =new Intent(MainActivity.this,OCRResult.class);
                        intent.putExtra("Image",bs.toByteArray());
                        long aft=System.currentTimeMillis();
                        Log.d("WillyCheng","Use Time="+(aft-pre));
                        startActivity(intent);

                    }
                });
                tGetPic.start();

            }
            boolean pressed = false;
            if (!mTakePicture.isPressed()) {
                pressed = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // display our (only) XML layout - Views already ordered
        setContentView(R.layout.main);

        // the accelerometer is used for autofocus
        mSensorManager = (SensorManager) getSystemService(Context.
                SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);

        // get the window width and height to display buttons
        // according to device screen size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        mScreenWidth = displaymetrics.widthPixels;
        Log.d("WillyCheng","height="+mScreenHeight+"  width="+mScreenWidth);
        // I need to get the dimensions of this drawable to set margins
        // for the ImageView that is used to take pictures
        Drawable mButtonDrawable = this.getResources().
                getDrawable(R.drawable.camera);

        mTakePicture = (ImageView) findViewById(R.id.startcamerapreview);

        // setting where I will draw the ImageView for taking pictures
      //  LayoutParams lp = new LayoutParams(mTakePicture.getLayoutParams());
//        lp.setMargins((int) ((double) mScreenWidth * .85),
//                (int) ((double) mScreenHeight * .70),
//                (int) ((double) mScreenWidth * .85) + mButtonDrawable.
//                        getMinimumWidth(),
//                (int) ((double) mScreenHeight * .70) + mButtonDrawable.
//                        getMinimumHeight());
      //  mTakePicture.setLayoutParams(lp);
        // rec is used for onInterceptTouchEvent. I pass this from the
        // highest to lowest layer so that when this area of the screen
        // is pressed, it ignores the TouchView events and passes it to
        // this activity so that the button can be pressed.
//        rec.set((int) ((double) mScreenWidth * .85),
//                (int) ((double) mScreenHeight * .10),
//                (int) ((double) mScreenWidth * .85) + mButtonDrawable.getMinimumWidth(),
//                (int) ((double) mScreenHeight * .70) + mButtonDrawable.getMinimumHeight());
        mButtonDrawable = null;
        mTakePicture.setOnClickListener(previewListener);
        // get our Views from the XML layout
        mPreview = (Preview) findViewById(R.id.preview);
        mView = (TouchView) findViewById(R.id.left_top_view);
        mView.setRec(rec);

    }

    // with this I get the ratio between screen size and pixels
    // of the image so I can capture only the rectangular area of the
    // image and save it.
    public Double[] getRatio() {
        Size s = mPreview.getCameraParameters().getPreviewSize();
        //Log.d("WillyCheng","h="+s.height+"   w="+s.width);
        double heightRatio = (double) s.height / (double) mScreenHeight;
        double widthRatio = (double) s.width / (double) mScreenWidth;
        Double[] ratio = {heightRatio, widthRatio};
        //Log.d("WillyCheng","ratio.h="+ratio[0]+"   ratio.w="+ratio[1]);
        return ratio;
    }

    // just to close the app and release resources.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean savePhoto(Bitmap bm) {
        FileOutputStream image = null;
        try {
            image = new FileOutputStream(mLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(CompressFormat.JPEG, 100, image);
        if (bm != null) {
            int h = bm.getHeight();
            int w = bm.getWidth();
            //Log.i(TAG, "savePhoto(): Bitmap WxH is " + w + "x" + h);
        } else {
            //Log.i(TAG, "savePhoto(): Bitmap is null..");
            return false;
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean intercept = false;
        switch (action) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                float x = ev.getX();
                float y = ev.getY();
                // here we intercept the button press and give it to this
                // activity so the button press can happen and we can take
                // a picture.
                if ((x >= rec.left) && (x <= rec.right) && (y >= rec.top) && (y <= rec.bottom)) {
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    // mainly used for autofocus to happen when the user takes a picture
    // I also use it to redraw the canvas using the invalidate() method
    // when I need to redraw things.
    public void onSensorChanged(SensorEvent event) {

        if (mInvalidate == true) {
            mView.invalidate();
            mInvalidate = false;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaY > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaZ > .5 && mAutoFocus) { //AUTOFOCUS (while it is not autofocusing) */
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;

    }

    // extra overrides to better understand app lifecycle and assist debugging
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause()");
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
        //Log.i(TAG, "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart()");
    }
//    public void processImage(View view) {
//
//        progressBar = new ProgressDialog(view.getContext());
//        if (image != null)
//            new OCR().execute(image);
//        else {
//            Toast.makeText(MainActivity.this, "尚未選擇圖片", Toast.LENGTH_SHORT).show();
//        }
//
//        //if (mFile.exists()) mFile.delete();
//    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}