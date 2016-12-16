package com.view.camera.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;


import com.view.camera.R;
import com.view.camera.adapter.ImageGvAdapter;
import com.view.camera.presenter.CameraPresenterImpl;
import com.view.camera.util.BitmapThreadPool;
import com.view.camera.view.DialogView;
import com.view.camera.view.NoScrollGridView;

import static com.view.camera.R.id.mSurfaceView;


public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    public HorizontalScrollView horizontalScrollView;
    public Camera mCamera;
    public ImageView mButton;
    public SurfaceView mSurfaceView;
    public SurfaceHolder holder;
    public AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    public NoScrollGridView gridView1;
    public View view;
    public Button tv_save, tv_cancle;
    public ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private DialogView dialogView;
    public CameraPresenterImpl presenter;
    private HandlerThread mHandlerThread;
    private Handler mHandlerJpeg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photograph_layout_1);

        presenter = new CameraPresenterImpl(this);

        initHandlerThread();

        presenter.initView();
        presenter.initListener();
        presenter.initData();

        gridView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (dialogView == null || !dialogView.isShow()) {
                    dialogView = new DialogView(CameraActivity.this, bitmaps, arg2);
                }
            }
        });

    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        try {
            /* 打开相机， */
            System.out.println("打开照相功能！");
            mCamera = Camera.open();
            mCamera.startPreview();
            mCamera.setPreviewDisplay(holder);
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(this, "打开相机失败，请查看系统设置中相机权限是否开启", Toast.LENGTH_SHORT).show();
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }

        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {
        /* 相机初始化 */
        initCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        stopCamera();
        if (mCamera != null) {
            mCamera.release();
        }
        mCamera = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            stopCamera();
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = null;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    public PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            Message message = Message.obtain();
            message.what = 0x1111;
            message.obj = _data;
            mHandlerJpeg.sendMessage(message);

        }
    };

    public void initHandlerThread() {
        mHandlerThread = new HandlerThread("Test");
        mHandlerThread.start();

        mHandlerJpeg = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0x1111) {

                    final byte[] _data = (byte[]) msg.obj;

                    BitmapThreadPool.post(new Runnable() {
                        @Override
                        public void run() {
                            // do everything
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // 把摄像头获得画面显示在SurfaceView控件里面
                                        mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                                        mCamera.setDisplayOrientation(90);
                                        mCamera.startPreview();// 开始预览
                                        view.setVisibility(View.GONE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            presenter.saveImage(_data);
                        }
                    });
                }
                return true;
            }
        });

    }


    /* 告定义class AutoFocusCallback */
    public final class AutoFocusCallback implements Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
			/* 对到焦点拍照 */
            if (focused) {
            }
        }
    }

    /* 相机初始化的method */
    private void initCamera() {
        if (mCamera != null) {

            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

            boolean flag = false;
            // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            if (sizeList.size() > 0) {

                for (int i = 0; i < sizeList.size(); i++) {

                    if (i != 0 && sizeList.get(0).width > sizeList.get(i).width && !flag) {
                        flag = true;
                        // 从大到小
                        break;
                    }

                }
                presenter.getCameraSize(parameters, sizeList, flag);
            } else {
                Toast.makeText(this, "您的手机暂不支持拍照", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /* 停止相机的method */
    private void stopCamera() {
        if (mCamera != null) {
            try {
				/* 停止预览 */
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}