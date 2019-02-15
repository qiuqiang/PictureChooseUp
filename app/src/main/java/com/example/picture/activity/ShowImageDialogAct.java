package com.example.picture.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.picture.R;
import com.example.picture.util.Configure;

public class ShowImageDialogAct extends AppCompatActivity {
    private ImageView item_image_dialogImg;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_dialog);
        context = this;
        initView();
    }

    private void initView() {
        item_image_dialogImg = findViewById(R.id.item_image_dialogImg);
        Intent intent = getIntent();
        if (intent != null) {
            String extra = intent.getStringExtra(Configure.FILE_IMG_PATH);
            if(extra!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(extra);
                if(bitmap!=null){
                    item_image_dialogImg.setImageBitmap(bitmap);
                }
            }
        }
    }

}
