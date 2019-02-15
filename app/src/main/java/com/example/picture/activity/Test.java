package com.example.picture.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.picture.R;
import com.example.picture.entity.FileTypeInfo;
import com.example.picture.util.Configure;
import com.example.picture.util.ImageDetailDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class Test extends AppCompatActivity implements View.OnClickListener {
    private final int UP_CODE = 1;
    private Button testBtn;
    private Context context;
    ArrayList<FileTypeInfo> arrayList = new ArrayList<>();
    ArrayList<FileTypeInfo> isUpList = null;
    private String path = " /storage/emulated/0/图片上传/拍照材料/10_test1548313319442.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;
        initView();
    }

    private void initView() {
        testBtn = findViewById(R.id.testBtn);
        testBtn.setOnClickListener(this);

        FileTypeInfo info = new FileTypeInfo();
        info.setName("土地房屋权证");
        info.setFileCode("10");

        FileTypeInfo info1 = new FileTypeInfo();
        info1.setName("区民政部门出具的低收入家庭认定证明");
        info1.setFileCode("20");

        FileTypeInfo info2 = new FileTypeInfo();
        info2.setName("保障性住房租赁合同");
        info2.setFileCode("20");

        arrayList.add(info);
        arrayList.add(info1);
        arrayList.add(info2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testBtn:
                jumpUp();
                break;
        }

    }

    private void jumpUp() {
        Intent intent = new Intent(context, FileTypeActivity.class);
        intent.putExtra(Configure.FILE_UNICODE, getTenRandomDigit());
        if (arrayList.size() > 0) {
            intent.putExtra(Configure.FILE_TYPES, arrayList);
        }
        if (isUpList != null && isUpList.size() > 0) {
            intent.putExtra(Configure.FILE_IS_UP, isUpList);
        }
        startActivityForResult(intent, UP_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case UP_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Configure.FILE_IS_UP_ALL, false);
                        if (!booleanExtra) {
                            Toast.makeText(context, "未上传全部材料", Toast.LENGTH_SHORT).show();
                        } else {
                            testBtn.setText("已上传");
                        }
                        arrayList.clear();
                        isUpList = data.getParcelableArrayListExtra(Configure.FILE_IS_UP);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //生成业务编码
    public static synchronized String getTenRandomDigit() {
        SimpleDateFormat sif = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String date = sif.format(new java.util.Date());
        Random random = new Random();
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            target.append(random.nextInt(10));
        }
        return date + target;
    }

}
