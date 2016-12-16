package com.view.camera.presenter;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by Destiny on 2016/12/16.
 */

public interface CameraPresenter {
    void saveImage(byte[] _data);
    boolean setPreviewSize(Camera.Parameters parameters, List<Camera.Size> sizeList,  int position);
    void getCameraSize(Camera.Parameters parameters, List<Camera.Size> sizeList, boolean sort);
    void initListener();
    void initView();
    void initData();
}
