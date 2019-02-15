package com.example.picture.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.picture.R;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.ImageUtil;
import com.example.picture.util.Md5Util;

import java.util.ArrayList;
import java.util.List;

/*Created by 邱强 on 2019/1/30.
 * E-Mail 2536555456@qq.com
 */
//显示已上传的image
public class FileTypeUpImagesAdapter extends RecyclerView.Adapter<FileTypeUpImagesAdapter.FileTypeUpViewHolder> {
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

    public FileTypeUpImagesAdapter(List<ImagesInfo> list, Context context, RecyclerView recyclerView) {
        this.list = list;
        this.context = context;
        this.recyclerView = recyclerView;
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
                    mVisibleItemCount = m.getChildCount();
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
            }
        });
    }

    @NonNull
    @Override
    public FileTypeUpImagesAdapter.FileTypeUpViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upimages_adapter, null, false);
        return new FileTypeUpImagesAdapter.FileTypeUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileTypeUpImagesAdapter.FileTypeUpViewHolder fileTypeHolder, int i) {
        if (list != null) {
            dataBind(fileTypeHolder, i);
        }
    }

    private void dataBind(final FileTypeUpImagesAdapter.FileTypeUpViewHolder holder, final int position) {
        ImagesInfo imageItem = list.get(position);
        if (imageItem != null) {
            int windowWidth = getWindowWidth(context);
            int width = windowWidth / 5;
            String path = imageItem.getPath();
            if (path != null && !path.equals("")) {
                holder.fileUpImages.setTag(Md5Util.getKey(path));
               // Bitmap bitmapFromMemoryCache = getBitmapFromMemoryCache(Md5Util.getKey(path));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
                holder.fileUpImages.setLayoutParams(params);
              /*  if (bitmapFromMemoryCache != null) {
                    holder.fileUpImages.setImageBitmap(bitmapFromMemoryCache);
                } else {
                    holder.fileUpImages.setImageResource(R.drawable.plugin_camera_no_pictures);
                }*/
                holder.fileUpImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClick != null) {
                            onItemClick.click(holder.getAdapterPosition());
                        }
                    }
                });

                holder.fileUpImages.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onItemLongClick != null) {
                            onItemLongClick.onLongClick(holder.getAdapterPosition());
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void click(int position);
    }


    private OnItemLongClick onItemLongClick;

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }


    public interface OnItemLongClick {
        void onLongClick(int position);
    }

    private OnItemDeleteClick onItemDeleteClick;

    public void setOnItemDeleteClick(OnItemDeleteClick onItemDeleteClick) {
        this.onItemDeleteClick = onItemDeleteClick;
    }

    public interface OnItemDeleteClick {
        void onDelete(int position);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class FileTypeUpViewHolder extends RecyclerView.ViewHolder {
        private ImageView fileUpImages;

        FileTypeUpViewHolder(@NonNull View itemView) {
            super(itemView);
            fileUpImages = itemView.findViewById(R.id.fileUpImages);
        }
    }

    public void addItem(ImagesInfo info) {
        if (info != null) {
            list.add(info);
            notifyItemInserted(list.size() - 1);
        }
    }

    public void addItem(ArrayList<ImagesInfo> data) {
        if (list != null) {
            list.addAll(data);
            notifyDataSetChanged();
        }
    }


    public void deleteItem(int position) {
        if (list != null) {
            ImagesInfo info = list.get(position);
            deleteImageInfoFromCache(info);
            list.remove(position);
            notifyItemRemoved(list.size() - 1);
        }
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

    public void deleteAllItem() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    public List<ImagesInfo> getAllData() {
        return list;
    }

    /**
     * @param firstVisibleItem  第一个可见的ImageView的下标
     * @param mVisibleItemCount 屏幕中总共可见的元素数
     */

    private void loadBitmaps(int firstVisibleItem, int mVisibleItemCount) {
        int windowWidth = getWindowWidth(context);
        int width = windowWidth / 6;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + mVisibleItemCount; i++) {
                String path = list.get(i).getPath();
                ImageView imageView = recyclerView.findViewWithTag(Md5Util.getKey(path));
                imageView.setLayoutParams(params);
                if(path!=null&&!path.equals("")){
                    Bitmap bitmap = getBitmapFromMemoryCache(Md5Util.getKey(path));
                    if (bitmap == null) {
                        Bitmap compressImage = ImageUtil.getInstance().compressByScale(path, getScale(path));
                        Bitmap bitmap1 = ImageUtil.getInstance().compressBitmapBySize(compressImage, width, width);
                        if (bitmap1 != null) {
                            imageView.setImageBitmap(compressImage);
                            if (getBitmapFromMemoryCache(Md5Util.getKey(path)) == null) {
                                addBitmapToMemoryCache(Md5Util.getKey(path), bitmap1);
                            }
                        } else {
                            imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
                        }
                    } else {
                        imageView.setImageBitmap(bitmap);
                    }
                }else {
                    imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入Bitmap对象。
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return key != null ? mMemoryCache.get(key) : null;
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
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
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
}
