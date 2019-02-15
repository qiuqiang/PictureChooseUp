package com.example.picture.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.picture.R;
import com.example.picture.adapter.AllPictureAdapter;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.AlbumUtil;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.PostProDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllPictureActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recycleView;
    private Context context;
    private List<ImagesInfo> list = new ArrayList<>();
    private AllPictureAdapter adapter;
    private TextView all_finishBtn;
    private TextView all_cancelBtn;
    private ImageView all_backBtn;
    private PostProDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_picture);
        context = this;
        initUtil();
        initView();
    }

    private void initUtil() {
        dialog = new PostProDialog(context, "请稍等");
    }

    private void initView() {
        recycleView = findViewById(R.id.all_recycleView);
        all_finishBtn = findViewById(R.id.all_finishBtn);
        all_finishBtn.setOnClickListener(this);
        all_cancelBtn = findViewById(R.id.all_cancelBtn);
        all_cancelBtn.setOnClickListener(this);
        all_backBtn = findViewById(R.id.all_backBtn);
        all_backBtn.setOnClickListener(this);
        Intent intent = getIntent();
        String bucket_id = intent.getStringExtra("bucket_id");
        if (bucket_id != null) {
            dialog.show();
            List<ImagesInfo> data = AlbumUtil.getInstance().getAllImagesOfAlbum(context, bucket_id, true);
            int len = BitmapCacheUtil.tempSelectBitmap.size();
            try {
                if (len > 0) {
                    for (ImagesInfo next : BitmapCacheUtil.tempSelectBitmap) {
                        int index = getIndex(next, data);
                        if (index != -1) {
                            data.get(index).setChecked(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            list.addAll(data);
            GridLayoutManager manager = new GridLayoutManager(context, 3);
            recycleView.setLayoutManager(manager);
            adapter = new AllPictureAdapter(context, list, recycleView);
            recycleView.setAdapter(adapter);
        }
    }

    private int getIndex(ImagesInfo info, List<ImagesInfo> list) {
        if (info != null && list != null) {
            int len = list.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    ImagesInfo info1 = list.get(i);
                    if (info1.getPath().equals(info.getPath())) {
                        return i;
                    }
                }
                return -1;
            } else {
                return -1;
            }
        }
        return -1;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_finishBtn:
                back();
                break;
            case R.id.all_cancelBtn:
                adapter.cancelAllChecked();
                break;
            case R.id.all_backBtn:
                if (adapter.getCheckCount() > 0) {
                    back();
                } else {
                    onBackPressed();
                }
                break;
        }
    }


    private void back() {
        List<ImagesInfo> selectPathList = adapter.getSelectPathList();
        for (int i = 0; i < selectPathList.size(); i++) {
            ImagesInfo info = selectPathList.get(i);
            boolean contains = isContains(info, BitmapCacheUtil.tempSelectBitmap);
            if (!contains) {
                BitmapCacheUtil.tempSelectBitmap.add(info);
            }
        }
        setResult(RESULT_OK, null);
        finish();
    }

    private boolean isContains(ImagesInfo info, List<ImagesInfo> data) {
        boolean isContain = false;
        int size = data.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                ImagesInfo info1 = data.get(i);
                if (info.getPath().equals(info1.getPath())) {
                    isContain = true;
                    break;
                } else {
                    isContain = false;
                }
            }
        }
        return isContain;
    }


}
