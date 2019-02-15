package com.example.picture;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.picture.activity.AlbumChooseActivity;
import com.example.picture.adapter.ImageChooseAdapter;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.entity.UpTempInfo;
import com.example.picture.okhttp.ProgressRequestBody;
import com.example.picture.util.ActivityUtils;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.Configure;
import com.example.picture.util.FileUtil;
import com.example.picture.util.NetWorkUtils;
import com.example.picture.util.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * 上传文件首页
 * */
public class UpImageMainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int UP_FILE_CODE = 4545;
    private final int UP_PROGRESS_CODE = 11;
    private static final int TAKE_PICTURE = 23432;
    private static final int REQUEST_EXTERNAL_STORAGE = 2121;
    private static final int REQUEST_CAMERA = 12;
    private static final int CHOOSE_IMAGE_CODE = 1;
    private ImageChooseAdapter adapter;
    private Context context;
    private List<ImagesInfo> list = new ArrayList<>();
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private StringBuilder finalPath = new StringBuilder("");//拍照的最终路径
    private ImageView fileTypeLoading;
    private int upCount = 0;
    private StringBuilder isUp = new StringBuilder("0");
    private TextView up_main_up_num;
    private View parentView;
    private NetWorkUtils https = new NetWorkUtils();
    private ArrayList<ImagesInfo> success = new ArrayList<>();
    private StringBuilder fileCode = new StringBuilder();
    private StringBuilder fileName = new StringBuilder();
    private StringBuilder uniCode = new StringBuilder();
    private AnimationDrawable drawable;
    private ThreadPoolExecutor executor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_up_main);
        ActivityUtils.newInstance().addActivity(this);
        context = this;
        up_main_up_num = findViewById(R.id.up_main_up_num);
        parentView = getLayoutInflater().inflate(R.layout.activity_up_main, null);
        initUtil();
        saveData();
        if (!PermissionUtil.lacksPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !PermissionUtil.lacksPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            initView();
        } else {
            ActivityCompat.requestPermissions(UpImageMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void initUtil() {
        LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>(10);
        executor = new ThreadPoolExecutor(10, 10, 20, TimeUnit.SECONDS, queue);
    }


    public void saveData() {
        Intent data = getIntent();
        if (data != null) {
            String code = data.getStringExtra(Configure.FILE_CODE);
            String name = data.getStringExtra(Configure.FILE_NAME);
            String uCode = data.getStringExtra(Configure.FILE_UNICODE);

            int count = data.getIntExtra(Configure.FILE_UP_COUNT, 0);
            if (count > 0) {
                up_main_up_num.setVisibility(View.VISIBLE);
                String countText = "已上传" + count + "张";
                up_main_up_num.setText(countText);
            }
            if ((code != null)) {
                fileCode.replace(0, fileCode.length(), code);
            }
            if ((name != null)) {
                fileName.replace(0, fileName.length(), name);
            }
            if ((uCode != null)) {
                uniCode.replace(0, uniCode.length(), uCode);
            }
        }
    }

    public void initView() {
        LinearLayout up_main_backBtn = findViewById(R.id.up_main_backBtn);
        up_main_backBtn.setOnClickListener(this);
        Button clear_all = findViewById(R.id.up_main_clear_btn);
        clear_all.setOnClickListener(this);
        pop = new PopupWindow(context);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows_photo, null);
        ll_popup = view.findViewById(R.id.ll_popup);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        fileTypeLoading = findViewById(R.id.fileTypeLoading);

        drawable = (AnimationDrawable) getResources().getDrawable(R.drawable.loading);
        fileTypeLoading.setImageDrawable(drawable);

        Button pop_camera_btn = view.findViewById(R.id.pop_camera_btn);
        Button pop_choose_btn = view.findViewById(R.id.pop_choose_btn);
        Button pop_cancel_btn = view.findViewById(R.id.pop_cancel_btn);
        pop_camera_btn.setOnClickListener(this);
        pop_choose_btn.setOnClickListener(this);
        pop_cancel_btn.setOnClickListener(this);
        RecyclerView up_main_recycleView = findViewById(R.id.up_main_recycleView);
        adapter = new ImageChooseAdapter(list, context);
        GridLayoutManager manager = new GridLayoutManager(context, 3);
        up_main_recycleView.setLayoutManager(manager);
        up_main_recycleView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImageChooseAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    if (list.size() < BitmapCacheUtil.MAX_NUM) {
                        if (position == list.size() - 1) {
                            ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.activity_translate_in));
                            pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                        }
                    } else if (adapter.getCount() == BitmapCacheUtil.MAX_NUM && adapter.isCanAddPicture()) {
                        ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.activity_translate_in));
                        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button upBtn = findViewById(R.id.up_main_upBtn);
        upBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up_main_backBtn:
                back();
                break;
            case R.id.up_main_upBtn:
                up();
                break;
            case R.id.pop_camera_btn:
                pop.dismiss();
                ll_popup.clearAnimation();
                if (!PermissionUtil.lacksPermission(context, Manifest.permission.CAMERA)) {
                    takePhoto();
                } else {
                    ActivityCompat.requestPermissions(UpImageMainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                break;
            case R.id.pop_choose_btn:
                Intent intent = new Intent(context, AlbumChooseActivity.class);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                startActivityForResult(intent, CHOOSE_IMAGE_CODE);
                ll_popup.clearAnimation();
                break;
            case R.id.pop_cancel_btn:
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.up_main_clear_btn:
                adapter.deleteAllData();
                //在外部操作BitmapCacheUtil
                BitmapCacheUtil.tempSelectBitmap.clear();
                break;
        }
    }

    private void up() {
        fileTypeLoading.setVisibility(View.VISIBLE);
        drawable.start();
        final List<File> listFile = new ArrayList<>();
        final List<ImagesInfo> allData = adapter.getAllData();
        for (int i = 0, len = allData.size(); i < len; i++) {
            ImagesInfo itemByPosition = allData.get(i);
            String path = itemByPosition.getPath();
            File file = new File(path);
            listFile.add(file);
        }
        if (listFile.size() > 0) {
            for (int i = 0, len = listFile.size(); i < len; i++) {
                final int finalI = i;
                final String path = listFile.get(i).getAbsolutePath();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String s = https.uploadFileGetProgress(listFile.get(finalI), uniCode.toString(), fileCode.toString(), new ProgressRequestBody.OnFileUpClick() {
                            @Override
                            public void fileUpLoading(int progress) {
                                Message msg1 = new Message();
                                msg1.what = UP_PROGRESS_CODE;
                                msg1.arg1 = finalI;
                                msg1.arg2 = progress;
                                handler.sendMessage(msg1);
                            }
                        });
                        //String s = "{\"resCode\":\"000\",\"resMsg\":\"上传文件成功！\"}";
                        Message msg = Message.obtain();
                        msg.arg1 = finalI;
                        msg.what = UP_FILE_CODE;
                        UpTempInfo info = new UpTempInfo();
                        ImagesInfo imagesInfo = new ImagesInfo();
                        try {
                            imagesInfo.setPath(path);
                            imagesInfo.setName(listFile.get(finalI).getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        info.setInfo(imagesInfo);
                        info.setMsg(s);
                        msg.obj = info;
                        handler.sendMessage(msg);
                    }
                };
                executor.execute(runnable);
            }
        } else {
            Toast.makeText(context, "没有图片", Toast.LENGTH_SHORT).show();
        }
    }


    private void back() {
        Intent back = new Intent();
        back.putExtra(Configure.FILE_UP_COUNT, success.size() + "");
        back.putExtra(Configure.FILE_IMAGES, success);
        setResult(RESULT_OK, back);
        finish();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UP_FILE_CODE:
                    UpTempInfo upTempInfo = (UpTempInfo) msg.obj;
                    String upRes = upTempInfo.getMsg();
                    try {
                        JSONObject obj = new JSONObject(upRes);
                        if (obj.optString("resCode").equals("000")) {
                            ImagesInfo info = upTempInfo.getInfo();
                            if (info != null) {
                                adapter.deleteItem(info);
                                success.add(info);
                                deleteImageInfoFromCache(info);
                            }
                            if (adapter.getCount() == 0 || adapter.getCount() == 1) {
                                fileTypeLoading.clearAnimation();
                                fileTypeLoading.setVisibility(View.GONE);
                            }
                            isUp.replace(0, isUp.toString().length(), "1");
                            String count = "已上传" + success.size() + "张";
                            up_main_up_num.setText(count);
                            up_main_up_num.setVisibility(View.VISIBLE);
                        } else {
                            fileTypeLoading.clearAnimation();
                            fileTypeLoading.setVisibility(View.GONE);
                            isUp.replace(0, isUp.toString().length(), "0");
                            Toast.makeText(context, "上传失败,请重新上传", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "上传失败,请重新上传", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case UP_PROGRESS_CODE:
                    try {
                        adapter.setItemProgress(msg.arg1, msg.arg2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void takePhoto() {
        try {
            String sdState = Environment.getExternalStorageState();
            if (sdState.equals(Environment.MEDIA_MOUNTED)) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.addCategory(Intent.CATEGORY_DEFAULT);
                String path = FileUtil.newInstance().getTakePhotoPath(context);
                finalPath.replace(0, finalPath.toString().length(), path + File.separator + "10_test" + System.currentTimeMillis() + ".jpg");
                File image = new File(finalPath.toString());
                boolean newFile = false;
                if (!image.exists()) {
                    newFile = image.createNewFile();
                }
                if (image.exists() || newFile) {
                    Uri imageUri = Uri.fromFile(image);
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(camera, TAKE_PICTURE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        ImagesInfo takePhoto = new ImagesInfo();
                        takePhoto.setPath(finalPath.toString());
                        takePhoto.setCamera(true);
                        adapter.addItem(takePhoto);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "请确认是否给予应用拍照权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case CHOOSE_IMAGE_CODE:
                if (resultCode == RESULT_OK) {
                    adapter.deleteAllData();
                    int len = BitmapCacheUtil.tempSelectBitmap.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            ImagesInfo info = BitmapCacheUtil.tempSelectBitmap.get(i);
                            adapter.addItem(info);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(context, "请给予拍照的权限", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void deleteImageInfoFromCache(ImagesInfo info) {
        try {
            if (info != null) {
                for (int i = 0; i < BitmapCacheUtil.tempSelectBitmap.size(); i++) {
                    ImagesInfo info1 = BitmapCacheUtil.tempSelectBitmap.get(i);
                    if (info1 != null) {
                        if (info.getPath().equals(info1.getPath())) {
                            BitmapCacheUtil.tempSelectBitmap.remove(info1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        System.gc();
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

