package com.example.picture.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.Toast;

import com.example.picture.R;
import com.example.picture.UpImageMainActivity;
import com.example.picture.adapter.AlbumChooseAdapter;
import com.example.picture.entity.AlbumInfo;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.AlbumUtil;
import com.example.picture.util.JumpUtil;
import com.example.picture.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class AlbumChooseActivity extends AppCompatActivity {
    private RecyclerView recycleView;
    private List<AlbumInfo> albumInfoList;
    private AlbumChooseAdapter albumChooseAdapter;
    private static final int REQUEST_EXTERNAL_STORAGE = 2121;
    private Context context;
    private final int IMAGE_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_choose);
        context = this;
        if (!PermissionUtil.lacksPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !PermissionUtil.lacksPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            initView();
        } else {
            ActivityCompat.requestPermissions(AlbumChooseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView();
            } else {
                Toast.makeText(context, "请给予读取存储卡的权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        recycleView = findViewById(R.id.album_recycleView);
        albumInfoList = new ArrayList<>();
        List<AlbumInfo> list = AlbumUtil.getInstance().getAllAlbumInfo(context);
        albumInfoList.addAll(list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recycleView.setLayoutManager(manager);
        albumChooseAdapter = new AlbumChooseAdapter(albumInfoList, context, recycleView);
        recycleView.setAdapter(albumChooseAdapter);
        albumChooseAdapter.setOnItemClickListener(new AlbumChooseAdapter.onAlbumItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AlbumInfo itemByPosition = albumChooseAdapter.getItemByPosition(position);
                //相册id传入
                Intent t = new Intent(context, AllPictureActivity.class);
                t.putExtra("bucket_id", itemByPosition.getBucket_id());
                startActivityForResult(t, IMAGE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case IMAGE_CODE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, null);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(11, null);
        super.onBackPressed();
    }
}
