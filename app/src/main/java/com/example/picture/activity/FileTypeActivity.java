package com.example.picture.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.picture.R;
import com.example.picture.UpImageMainActivity;
import com.example.picture.adapter.FileTypeAdapter;
import com.example.picture.adapter.FileTypeUpImagesAdapter;
import com.example.picture.entity.FileTypeInfo;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.Configure;
import com.example.picture.util.ImageDetailDialog;

import java.util.ArrayList;
public class FileTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private final int UP_REQUEST_CODE = 1;
    private final int IMG_REQUEST_CODE = 2;
    private FileTypeAdapter adapter;
    private Context context;
    private int currentPosition = 0;
    private StringBuilder code = new StringBuilder();
    private LinearLayout fileTypeBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_type);
        context=this;
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.fileTypeRecycle);
        fileTypeBackBtn = findViewById(R.id.fileTypeBackBtn);
        fileTypeBackBtn.setOnClickListener(this);
        Intent intent = getIntent();
        //没有上传的
        ArrayList<FileTypeInfo> FILE_TYPES = intent.getParcelableArrayListExtra(Configure.FILE_TYPES);
        //已上传一部分进来应该恢复显示
        ArrayList<FileTypeInfo> FILE_IS_UP = intent.getParcelableArrayListExtra(Configure.FILE_IS_UP);
        String unicode = intent.getStringExtra(Configure.FILE_UNICODE);
        if(unicode!=null){
            code.replace(0, code.length(), unicode);
        }
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        if(FILE_TYPES!=null){
            setAdapter(FILE_TYPES);
        }else if(FILE_IS_UP!=null){
            setAdapter(FILE_IS_UP);
        }else {
            back();
        }
    }

    private void setAdapter(ArrayList<FileTypeInfo> list) {
        if (list != null) {
            adapter = new FileTypeAdapter(list, context,recyclerView);
            recyclerView.setAdapter(adapter);
            adapter.setOnSampleClick(new FileTypeAdapter.OnSampleClick() {
                @Override
                public void onSampleClick(int position) {
                    Toast.makeText(context, "我是样图" + position, Toast.LENGTH_SHORT).show();
                }
            });
            adapter.setOnAddClick(new FileTypeAdapter.OnAddClick() {
                @Override
                public void addClick(int position) {
                    currentPosition = position;
                    Intent intent1 = new Intent(context, UpImageMainActivity.class);
                    int count=adapter.getItem(position).getPaths()!=null?adapter.getItem(position).getPaths().size():0;
                    intent1.putExtra(Configure.FILE_UP_COUNT,count );
                    intent1.putExtra(Configure.FILE_CODE, adapter.getItem(position).getFileCode());
                    intent1.putExtra(Configure.FILE_NAME, adapter.getItem(position).getName());
                    intent1.putExtra(Configure.FILE_UNICODE, code.toString());
                    startActivityForResult(intent1, UP_REQUEST_CODE);
                }
            });

            adapter.setOnFileTypeClick(new FileTypeAdapter.OnFileTypeItemClick() {
                @Override
                public void onClick(final FileTypeUpImagesAdapter imagesAdapter, final int position, final int subPosition) {
                    FileTypeInfo item = adapter.getItem(position);
                    if(item!=null){
                        ArrayList<ImagesInfo> paths = item.getPaths();
                        if(paths!=null&&paths.size()>0){
                            ImagesInfo info = paths.get(subPosition);
                            if(info!=null){
                                String path = info.getPath();
                                if(path!=null){
                                    final ImageDetailDialog detailDialog=new ImageDetailDialog(context,path);
                                    detailDialog.setOnDeleteClick(new ImageDetailDialog.OnDeleteClick() {
                                        @Override
                                        public void delete() {
                                            adapter.deleteSubItem(imagesAdapter,position,subPosition);
                                            detailDialog.dismiss();
                                        }
                                    });
                                    detailDialog.show();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fileTypeBackBtn:
                back();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case UP_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {
                            String count = data.getStringExtra(Configure.FILE_UP_COUNT);
                            FileTypeInfo item = adapter.getItem(currentPosition);
                            if (count != null) {
                                if (item != null) {
                                    String upCount = item.getUpCount();
                                    if (upCount != null && upCount.length() > 0) {
                                        int c = Integer.valueOf(upCount) + Integer.valueOf(count);
                                        adapter.setUpCount(currentPosition, c + "");
                                    } else {
                                        adapter.setUpCount(currentPosition, count);
                                    }
                                }
                            }

                            ArrayList<ImagesInfo> extra = data.getParcelableArrayListExtra(Configure.FILE_IMAGES);
                            if (extra!= null && extra.size() > 0) {
                                adapter.addImagesLists(currentPosition,extra);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent intent = new Intent();
        intent.putExtra(Configure.FILE_IS_UP_ALL, adapter.isUpAll());
        ArrayList<FileTypeInfo> allData = adapter.getAllData();
        intent.putExtra(Configure.FILE_IS_UP_ALL, adapter.isUpAll());
        intent.putExtra(Configure.FILE_IS_UP, allData);
        setResult(RESULT_OK, intent);
        finish();
    }


}
