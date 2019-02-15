package com.example.picture.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.picture.R;

public class ImagePreviewActivity extends AppCompatActivity {
  private ImageView preview_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        preview_img=findViewById(R.id.preview_img);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        if(path!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if(bitmap!=null){
                preview_img.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}
