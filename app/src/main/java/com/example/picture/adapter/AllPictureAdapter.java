package com.example.picture.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.picture.R;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.ImageUtil;
import com.example.picture.util.Md5Util;

import java.util.ArrayList;
import java.util.List;


/*Created by 邱强 on 2019/1/24.
 * E-Mail 2536555456@qq.com
 */
//显示相册全部图片或者所有图片

public class AllPictureAdapter extends RecyclerView.Adapter<AllPictureAdapter.PictureHolder> {
    private List<ImagesInfo> list;
    private Context context;
    private RecyclerView recyclerView;
    private LruCache<String, Bitmap> mMemoryCache;
    // 第一张可见图片的下标
    private int mFirstVisibleItem;
    //一屏有多少张图片可见
    private int mVisibleItemCount;
    private boolean isFirstEnter = true;
    private RecyclerView.LayoutManager manager;
    private List<ImagesInfo> selectPath = new ArrayList<>();
    private boolean isClick = false;
    private int mCheckCount = 0;


    public AllPictureAdapter(Context context, List<ImagesInfo> list, RecyclerView recyclerView) {
        this.list = list;
        this.context = context;

        if (recyclerView == null) {
            throw new NullPointerException("recyclerView is null");
        }
        manager = recyclerView.getLayoutManager();
        if (manager == null) {
            throw new NullPointerException("please set LayoutManager of RecyclerView  before call this method");
        }
        this.recyclerView = recyclerView;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        //设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (manager != null) {
                        loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (manager instanceof LinearLayoutManager) {
                    LinearLayoutManager m = (LinearLayoutManager) manager;
                    mFirstVisibleItem = m.findFirstVisibleItemPosition();
                    mVisibleItemCount = m.getChildCount();//可见数 获取不对？
                } else if (manager instanceof GridLayoutManager) {
                    GridLayoutManager m1 = (GridLayoutManager) manager;
                    mFirstVisibleItem = m1.findFirstVisibleItemPosition();
                    mVisibleItemCount = m1.getChildCount();
                }
                if (isFirstEnter) {
                    if (manager != null) {
                        if (mVisibleItemCount > 0) {
                            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
                            isFirstEnter = false;
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public List<ImagesInfo> getAllData() {
        return list;
    }

    public void addItem(ImagesInfo imageItem) {
        if (list != null && imageItem != null) {
            list.add(imageItem);
            notifyItemInserted(list.size() - 1);
        }
    }

    public ImagesInfo getItemByPosition(int position) {
        return list != null ? list.get(position) : null;
    }

    public int getCheckCount() {
        return mCheckCount;
    }

    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public void addItem(ImagesInfo imageItem, int position) {
        if (list != null && imageItem != null) {
            list.add(position, imageItem);
            notifyItemChanged(position);
        }
    }

    public void setChecked(int position, ImagesInfo info, boolean isCheck) {
        if (list != null) {
            info.setChecked(isCheck);
            notifyItemChanged(position);
        }
    }

    public void cancelAllChecked() {
        for (int i = 0, len = selectPath.size(); i < len; i++) {
            ImagesInfo info = selectPath.get(i);
            int index = list.indexOf(info);
            list.get(index).setChecked(false);
            notifyItemChanged(index);
        }
        selectPath.clear();
    }


    public void deleteItem(int position) {
        if (list != null) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void deleteAllData() {
        if (list != null) {
            list.clear();
            notifyAll();
        }
    }

    public List<ImagesInfo> getCheckData() {
        List<ImagesInfo> data = new ArrayList<>();
        for (int i = 0, len = getCount(); i < len; i++) {
            ImagesInfo info = list.get(i);
            if (info.isChecked()) {
                data.add(info);
            }
        }
        return data;
    }


    public void setOnItemClickListener(AllImageItemClickListener allImageItemClickListener) {
        this.allImageItemClickListener = allImageItemClickListener;
    }

    private AllImageItemClickListener allImageItemClickListener;


    public interface AllImageItemClickListener {
        void onItemClick(int position);
    }


    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_adapter, null, false);
        return new PictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureHolder holder, int position) {

        holder.all_adapter_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;//避免再次触发checkBox的事件
                int adapterPosition = holder.getAdapterPosition();
                ImagesInfo itemByPosition = getItemByPosition(adapterPosition);
                boolean checked = getItemByPosition(adapterPosition).isChecked();
                if (!checked) {
                    mCheckCount++;
                    setChecked(adapterPosition, itemByPosition, true);
                    selectPath.add(itemByPosition);
                } else {
                    mCheckCount--;
                    setChecked(adapterPosition, itemByPosition, false);
                    selectPath.remove(itemByPosition);
                    deleteImageInfoFromCache(itemByPosition);
                }
                if (allImageItemClickListener != null) {
                    allImageItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        holder.all_adapter_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isClick) {
                    int adapterPosition = holder.getAdapterPosition();

                    ImagesInfo itemByPosition = getItemByPosition(adapterPosition);
                    itemByPosition.setChecked(isChecked);
                    if (!isChecked) {
                        mCheckCount--;
                        selectPath.remove(itemByPosition);
                        deleteImageInfoFromCache(itemByPosition);
                    } else {
                        mCheckCount++;
                        selectPath.add(itemByPosition);
                    }
                }
                isClick = false;
            }
        });

        if (list != null) {
            dataBind(holder, position);
        }
    }

    private void deleteImageInfoFromCache(ImagesInfo info) {
        try {
            if (info != null) {
                for (int i = 0; i < BitmapCacheUtil.tempSelectBitmap.size(); i++) {
                    ImagesInfo info1 = BitmapCacheUtil.tempSelectBitmap.get(i);
                    if (info.getPath().equals(info1.getPath())) {
                        BitmapCacheUtil.tempSelectBitmap.remove(info1);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //把选中的数据存储起来 ，避免数据过多时还要去遍历找
    public List<ImagesInfo> getSelectPathList() {
        return selectPath;
    }

    private void dataBind(final PictureHolder holder, final int position) {
        ImagesInfo imageItem = list.get(position);
        int windowWidth = getWindowWidth(context);
        int width = windowWidth / 3;
        String path = imageItem.getPath();
        holder.all_adapter_img.setTag(Md5Util.getKey(path));
        Bitmap bitmapFromMemoryCache = getBitmapFromMemoryCache(Md5Util.getKey(path));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        holder.all_adapter_img.setLayoutParams(params);
        if (bitmapFromMemoryCache != null) {
            holder.all_adapter_img.setImageBitmap(bitmapFromMemoryCache);
        } else {
            holder.all_adapter_img.setImageResource(R.drawable.plugin_camera_no_pictures);
        }
        holder.all_adapter_checkBox.setChecked(imageItem.isChecked());
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

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class PictureHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView all_adapter_img;
        private CheckBox all_adapter_checkBox;

        PictureHolder(View itemView) {
            super(itemView);
            all_adapter_img = itemView.findViewById(R.id.all_adapter_img);
            all_adapter_checkBox = itemView.findViewById(R.id.all_adapter_checkBox);
        }
    }

    private boolean isCanCheck() {
        return selectPath.size() < BitmapCacheUtil.MAX_NUM;
    }

    /**
     * @param firstVisibleItem  第一个可见的ImageView的下标
     * @param mVisibleItemCount 屏幕中总共可见的元素数
     */
    private void loadBitmaps(int firstVisibleItem, int mVisibleItemCount) {
        int windowWidth = getWindowWidth(context);
        int width = windowWidth / 3;
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + mVisibleItemCount; i++) {
                String path = getItemByPosition(i).getPath();
                ImageView imageView = recyclerView.findViewWithTag(Md5Util.getKey(path));
                if (imageView != null) {
                    Bitmap bitmap = mMemoryCache.get(Md5Util.getKey(path));
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Bitmap compressImage = ImageUtil.getInstance().compressByScale(path, getScale(path));
                        Bitmap bitmap1 = ImageUtil.getInstance().compressBitmapBySize(compressImage, width, width);
                        if (compressImage != null) {
                            compressImage.recycle();
                        }
                        if (bitmap1 != null) {
                            imageView.setImageBitmap(bitmap1);
                            if (getBitmapFromMemoryCache(Md5Util.getKey(path)) == null) {
                                addBitmapToMemoryCache(Md5Util.getKey(path), bitmap1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据图片大小调整尺寸缩小的比例
     */
    private int getScale(String path) {
        if (path == null) {
            return 0;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, newOpts);
        int width = newOpts.outWidth;
        if (width <= 400) {
            return 1;
        } else if (width <= 800) {
            return 2;
        } else if (width <= 1200) {
            return 4;
        } else {
            return 8;
        }
    }


    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里Bitmap对象。
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }


}
