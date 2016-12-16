package com.view.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.view.camera.view.TouchImageView;

import java.util.ArrayList;

/**
 * Created by Destiny on 2016/12/16.
 */
public class ImageVpAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Bitmap> bitmaps;

    public ImageVpAdapter(Context mContext,ArrayList<Bitmap> bitmaps){
        this.mContext=mContext;
        this.bitmaps=bitmaps;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        TouchImageView iv=new TouchImageView(mContext);
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageBitmap(bitmaps.get(position));
        ((ViewPager) view).addView(iv, 0); // 将图片增加到ViewPager
        return iv;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
