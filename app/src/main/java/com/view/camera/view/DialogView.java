package com.view.camera.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.view.camera.R;
import com.view.camera.adapter.ImageVpAdapter;

import java.util.ArrayList;

import static android.R.attr.path;
import static android.R.id.list;

/**
 * 自定义Dialog控件
 * Created by 苏奥博 on 2016/12/12.
 */

public class DialogView implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ImageButton ib_back;
    private TextView cur_position;
    private ViewPager vp_photo;


    private Context mContext;
    private AlertDialog builder;
    private View view;
    private Window dialogWindow;
    private ArrayList<Bitmap> bitmaps;
    private int curPosition;

    public DialogView(Context mContext, ArrayList<Bitmap> bitmaps, int curPosition) {
        this.mContext = mContext;
        this.bitmaps = bitmaps;
        this.curPosition = curPosition;
        initDialog();
    }

    public void initDialog() {
        /**************初始化dialog并设置布局样式****************/
        builder = new AlertDialog.Builder(mContext, R.style.Dialog).create(); // 先得到构造器
        builder.show();
        builder.getWindow().setContentView(R.layout.dialog_layout);
        LayoutInflater factory = LayoutInflater.from(mContext);
        view = factory.inflate(R.layout.dialog_layout, null);
        builder.getWindow().setContentView(view);
        /**************初始化dialog并设置布局样式****************/

        /**************获取屏幕状态栏高度****************/
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        /**************获取屏幕状态栏高度****************/

        /**************设置dialog显示位置以及大小****************/
        dialogWindow = builder.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);//显示在底部
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = d.getHeight() - statusBarHeight; // 高度设置为屏幕的0.5
        p.width = d.getWidth(); // 宽度设置为屏幕宽
        dialogWindow.setAttributes(p);
        /**************设置dialog显示位置以及大小****************/

        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        cur_position = (TextView) view.findViewById(R.id.tv_curPositionOfTotal);
        vp_photo = (ViewPager) view.findViewById(R.id.pager);

        ib_back.setOnClickListener(this);
        vp_photo.addOnPageChangeListener(this);

        cur_position.setText((curPosition + 1) + "/" + bitmaps.size());
        vp_photo.setAdapter(new ImageVpAdapter(mContext,bitmaps));
        vp_photo.setCurrentItem(curPosition);
    }

    @Override
    public void onClick(View view) {
        builder.dismiss();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        cur_position.setText(position + 1 + "/" + bitmaps.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public boolean isShow(){
        return builder.isShowing();
    }

}
