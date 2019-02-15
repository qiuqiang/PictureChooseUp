package com.example.picture.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.picture.R;
import com.example.picture.entity.ImagesInfo;

import java.util.List;

/*Created by 邱强 on 2019/1/31.
 * E-Mail 2536555456@qq.com
 */
public class ImageDetailDialog {
    private Context context;
    private String path;
    private AlertDialog dialog;

    public ImageDetailDialog(Context context, String path) {
        this.context = context;
        this.path = path;
        initView();
    }

    private void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_dialog, null, false);
        ImageView imageView = view.findViewById(R.id.item_dialog_img);
        ImageView deleteImg = view.findViewById(R.id.item_dialog_deleteImg);

        int windowWidth = getWindowWidth(context) * 3 / 4;
        int windowHeight = getWindowWidth(context) * 3 / 4;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(windowWidth, windowWidth);
        imageView.setLayoutParams(params);



        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            Bitmap compressBitmap = ImageUtil.getInstance().compressBitmapBySize(bitmap, windowWidth, windowHeight);
            bitmap.recycle();
            imageView.setImageBitmap(compressBitmap);
        }
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClick != null) {
                    deleteClick.delete();
                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
    }

    //dp转px
    private static float dpToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //dp转px
    public static float pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private OnDeleteClick deleteClick;

    public void setOnDeleteClick(OnDeleteClick deleteClick) {
        this.deleteClick = deleteClick;
    }

    public interface OnDeleteClick {
        void delete();
    }

    private int getWindowWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = null;
        if (manager != null) {
            defaultDisplay = manager.getDefaultDisplay();
            Point p = new Point();
            defaultDisplay.getSize(p);
            return p.x;
        }
        return 0;
    }

}
