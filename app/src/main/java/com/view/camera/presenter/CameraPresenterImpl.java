package com.view.camera.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.view.camera.R;
import com.view.camera.activity.CameraActivity;
import com.view.camera.adapter.ImageGvAdapter;
import com.view.camera.view.NoScrollGridView;

import java.util.List;
import static android.R.attr.x;
import static android.R.attr.y;
import static com.view.camera.R.id.gridView1;
import static com.view.camera.R.id.horizontalScrollView;
import static com.view.camera.R.id.mSurfaceView;
import static com.view.camera.R.id.tv_cancle;
import static com.view.camera.R.id.tv_save;
import static com.view.camera.R.id.view;
import static com.view.camera.util.BitmapThreadPool.finish;

/**
 * Created by Destiny on 2016/12/16.
 */

public class CameraPresenterImpl implements CameraPresenter,View.OnClickListener{
    private CameraActivity mActivity;
    private int i=0;
    private ImageGvAdapter adapter;

    public CameraPresenterImpl(CameraActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void saveImage(byte[] _data) {
        try {

            if (_data != null && _data.length > 0) {

                Bitmap bm = adjustPhotoRotation(BitmapFactory.decodeByteArray(_data, 0, _data.length), 90);

                if (bm != null) {

                    if (x < -5 && y < 6) {
                        bm = adjustPhotoRotation(bm, 90);
                    } else if (x > 5 && y < 6) {
                        bm = adjustPhotoRotation(bm, 90);
                        bm = adjustPhotoRotation(bm, 90);
                        bm = adjustPhotoRotation(bm, 90);
                    }

                    mActivity.bitmaps.add(bm);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setGridView();
                            adapter.notifyDataSetChanged();
                            mActivity.tv_save.setEnabled(true);
                            mActivity.tv_save.setTextColor(Color.WHITE);
                        }
                    });

                } else {
                    Toast.makeText(mActivity, "拍照失败，请重新尝试！", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(mActivity, "拍照失败，请重新尝试！", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean setPreviewSize(Camera.Parameters parameters, List<Camera.Size> sizeList,  int position) {
        int PreviewWidth = 0;
        int PreviewHeight = 0;

        PreviewWidth = sizeList.get(position).width;
        PreviewHeight = sizeList.get(position).height;

        try {
            if (PreviewWidth > 1400) {
                return false;
            }
            parameters.setPreviewSize(PreviewWidth, PreviewHeight);
            // //获得摄像区域的大小
            parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片输出的格式
            parameters.setJpegQuality(80);// 设置照片质量
            parameters.setPictureSize(PreviewWidth, PreviewHeight);// 设置拍出来的屏幕大小

            mActivity.mCamera.setParameters(parameters);// 把上面的设置 赋给摄像头
            mActivity.mCamera.setPreviewDisplay(  mActivity.mSurfaceView.getHolder());// 把摄像头获得画面显示在SurfaceView控件里面
            mActivity.mCamera.setDisplayOrientation(90);
            mActivity.mCamera.startPreview();// 开始预览
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void getCameraSize(Camera.Parameters parameters, List<Camera.Size> sizeList, boolean sort) {
        int position = 0;

        if (sort) {
            position = i;
        } else {
            position = sizeList.size() - i - 1;
        }

        if (!setPreviewSize(parameters, sizeList,position)) {
            i++;
            getCameraSize(parameters, sizeList, sort);
        }
    }

    @Override
    public void initListener() {
        Log.e("initListener","------------------------");
        mActivity.tv_cancle.setOnClickListener(this);
        mActivity.tv_save.setOnClickListener(this);
        mActivity.mSurfaceView.setOnClickListener(this);
        mActivity.mButton.setOnClickListener(this);
    }

    @Override
    public void initView() {
        Log.e("initView","------------------------");
        mActivity.gridView1 = (NoScrollGridView) mActivity.findViewById(gridView1);
        mActivity.horizontalScrollView = (HorizontalScrollView) mActivity.findViewById(horizontalScrollView);
        mActivity.tv_save = (Button) mActivity.findViewById(tv_save);
        mActivity.tv_cancle = (Button) mActivity.findViewById(tv_cancle);
        mActivity.view = mActivity.findViewById(view);
        mActivity.mSurfaceView = (SurfaceView) mActivity.findViewById(R.id.mSurfaceView);
        mActivity.mButton = (ImageView) mActivity.findViewById(R.id.myButton);
    }

    @Override
    public void initData() {
        Log.e("initData","------------------------");
        if (mActivity.bitmaps.size() == 0) {
            mActivity.tv_save.setEnabled(false);
            mActivity.tv_save.setTextColor(Color.parseColor("#666666"));
        }

        mActivity.holder = mActivity.mSurfaceView.getHolder();
        mActivity.holder.addCallback(mActivity);
        mActivity.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        adapter = new ImageGvAdapter(mActivity, mActivity.bitmaps);
        mActivity.gridView1.setAdapter(adapter);

        setGridView();
    }

    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

    public void setGridView() {
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) (100 * mActivity.bitmaps.size() * density);
        int itemWidth = (int) (90 * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.FILL_PARENT);
        mActivity.gridView1.setLayoutParams(params);
        mActivity.gridView1.setHorizontalSpacing(30);
        mActivity.gridView1.setStretchMode(GridView.NO_STRETCH);
        mActivity.gridView1.setColumnWidth(itemWidth);
        mActivity.gridView1.setNumColumns(mActivity.bitmaps.size());
        mActivity.horizontalScrollView.smoothScrollTo(mActivity.gridView1.getMeasuredWidth(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancle:
                mActivity.finish();
                break;
            case R.id.tv_save:
                mActivity.finish();
                break;
            case R.id.mSurfaceView:
                try {
                    mActivity.mCamera.autoFocus(mActivity.mAutoFocusCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.myButton:
                if (mActivity.view.getVisibility() == View.GONE) {
                    mActivity.view.setVisibility(View.VISIBLE);
                    takePicture();
                }
                break;
        }
    }

    /* 拍照的method */
    private void takePicture() {
        if (mActivity.mCamera != null) {
            mActivity.mCamera.takePicture(null, null, mActivity.jpegCallback);
            System.out.println("this is takePicture()");
        }
    }

}
