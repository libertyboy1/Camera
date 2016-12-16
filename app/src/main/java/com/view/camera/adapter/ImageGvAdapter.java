package com.view.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.view.camera.R;
import com.view.camera.activity.CameraActivity;

import java.util.ArrayList;

import static com.view.camera.R.id.tv_save;

/**
 * Created by Destiny on 2016/12/16.
 */
public class ImageGvAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Bitmap> bitmaps;

    public ImageGvAdapter(Context mContext,ArrayList<Bitmap> bitmaps){
        this.mContext=mContext;
        this.bitmaps=bitmaps;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder horld;
        if (convertView == null) {
            horld = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
            horld.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            horld.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(horld);
        } else {
            horld = (ViewHolder) convertView.getTag();
        }

        horld.iv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bitmaps.remove(position);
                ((CameraActivity)mContext).presenter.setGridView();
                notifyDataSetChanged();

                if (bitmaps.size() == 0) {
                    ((CameraActivity)mContext).tv_save.setEnabled(false);
                    ((CameraActivity)mContext).tv_save.setTextColor(Color.parseColor("#666666"));
                } else {
                    ((CameraActivity)mContext).tv_save.setEnabled(true);
                    ((CameraActivity)mContext).tv_save.setTextColor(Color.WHITE);
                }

            }
        });


        horld.iv_photo.setImageBitmap(bitmaps.get(position));

        return convertView;
    }

    class ViewHolder {
        ImageView iv_photo, iv_delete;
        ;
    }

}
