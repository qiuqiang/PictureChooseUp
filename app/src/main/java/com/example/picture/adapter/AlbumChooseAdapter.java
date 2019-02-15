package com.example.picture.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.picture.R;
import com.example.picture.entity.AlbumInfo;
import com.example.picture.util.ImageUtil;

import java.util.List;

/*Created by 邱强 on 2019/1/21.
 * E-Mail 2536555456@qq.com
 */
public class AlbumChooseAdapter extends RecyclerView.Adapter<AlbumChooseAdapter.AlbumHolder> {
    private List<AlbumInfo> list;
    private Context context;
    private RecyclerView recyclerView;
    private LruCache<String, Bitmap> mMemoryCache;
    // 第一张可见图片的下标
    private int mFirstVisibleItem;
    //一屏有多少张图片可见
    private int mVisibleItemCount;
    private boolean isFirstEnter = true;
    private RecyclerView.LayoutManager manager;


    //recyclerView 设置了LayoutManager之后初始化
    public AlbumChooseAdapter(List<AlbumInfo> list, Context context, RecyclerView recyclerView) {
        this.list = list;
        this.context = context;
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
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (manager != null) {
                        loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
                    }
                } else {
                    initImage(mFirstVisibleItem, mVisibleItemCount);
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


    public List<AlbumInfo> getAllData() {
        return list;
    }

    public void addItem(AlbumInfo imageItem) {
        if (list != null && imageItem != null) {
            list.add(imageItem);
            notifyItemChanged(list.size() - 1);
        }
    }

    public AlbumInfo getItemByPosition(int position) {
        return list != null ? list.get(position) : null;
    }

    public int getCount() {
        return list != null ? list.size() : 0;
    }

    public void addItem(AlbumInfo imageItem, int position) {
        if (list != null && imageItem != null) {
            list.add(position, imageItem);
            notifyItemChanged(position);
        }
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


    public void setOnItemClickListener(onAlbumItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private onAlbumItemClickListener onItemClickListener;


    public interface onAlbumItemClickListener {
        void onItemClick(int position);
    }


    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album_adapter, null, false);
        return new AlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumHolder holder, int position) {
        if (onItemClickListener != null) {
            holder.album_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
        }
        if (list != null) {
            AlbumInfo info = list.get(position);
            dataBind(holder, info, position);
        }
    }

    private void dataBind(AlbumHolder holder, AlbumInfo imageItem, int position) {
        String name = imageItem.getBucket_display_name();
        if (name != null) {
            holder.albumName.setText(name);
        }
        holder.albumCount.setText(imageItem.getCount());
        String headImgPath = imageItem.getFirstImagePath();
        if (headImgPath != null && !headImgPath.equals("")) {
            holder.albumHeadImg.setTag(headImgPath);
            Bitmap bitmap = mMemoryCache.get(headImgPath);
            if (bitmap != null) {
                holder.albumHeadImg.setImageBitmap(bitmap);
            } else {
                holder.albumHeadImg.setImageResource(R.drawable.plugin_camera_no_pictures);
            }
        } else {
            holder.albumHeadImg.setImageResource(R.drawable.plugin_camera_no_pictures);
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class AlbumHolder extends RecyclerView.ViewHolder {
        private LinearLayout album_root;
        private ImageView albumHeadImg;
        private TextView albumName;
        private TextView albumCount;


        AlbumHolder(View itemView) {
            super(itemView);
            album_root = itemView.findViewById(R.id.album_root);
            albumHeadImg = itemView.findViewById(R.id.albumHeadImg);
            albumName = itemView.findViewById(R.id.albumName);
            albumCount = itemView.findViewById(R.id.albumCount);

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
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * @param firstVisibleItem  第一个可见的ImageView的下标
     * @param mVisibleItemCount 屏幕中总共可见的元素数
     */
    private void loadBitmaps(int firstVisibleItem, int mVisibleItemCount) {
        int windowWidth = getWindowWidth(context);
        int width = windowWidth / 5;
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + mVisibleItemCount; i++) {
                String headImgPath = list.get(i).getFirstImagePath();
                Bitmap bitmap = getBitmapFromMemoryCache(headImgPath);
                ImageView imageView = recyclerView.findViewWithTag(headImgPath);
                if (bitmap == null) {
                    Bitmap compressImage = ImageUtil.getInstance().compressByScale(headImgPath, getScale(headImgPath));
                    Bitmap bitmap1 = ImageUtil.getInstance().compressBitmapBySize(compressImage, width, width);
                    if (imageView != null && bitmap1 != null) {
                        imageView.setImageBitmap(compressImage);
                        if (getBitmapFromMemoryCache(headImgPath) == null) {
                            addBitmapToMemoryCache(headImgPath, bitmap1);
                        }
                    }
                } else {
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        int height = newOpts.outHeight;
        if (width <= 400) {
            return 1;
        } else if (width <=800) {
            return 2;
        } else if (width <= 1200) {
            return 4;
        } else {
            return 8;
        }
    }

    private void initImage(int firstVisibleItem, int mVisibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + mVisibleItemCount; i++) {
                String headImgPath = list.get(i).getFirstImagePath();
                ImageView imageView = recyclerView.findViewWithTag(headImgPath);
                if (imageView != null) {
                    imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
