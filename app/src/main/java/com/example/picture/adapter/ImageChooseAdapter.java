package com.example.picture.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.picture.R;
import com.example.picture.entity.ImagesInfo;
import com.example.picture.util.BitmapCacheUtil;
import com.example.picture.util.ImageUtil;
import com.example.picture.util.Md5Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*Created by 邱强 on 2019/1/19.
 * E-Mail 2536555456@qq.com
 */

/*
 * 没有图片和张数小于最多张的时候默认显示 + 图标  达到最多张了删除这个+图标
 * */

public class ImageChooseAdapter extends RecyclerView.Adapter<ImageChooseAdapter.ImageViewHolder> {
    private List<ImagesInfo> list;
    private Context context;
    private boolean isHaveAddIcon = false;
    private final String name = "10_" + System.currentTimeMillis();
    private LruCache<String, Bitmap> mMemoryCache;
    private final int maxProgress = 100;

    public ImageChooseAdapter(List<ImagesInfo> list, Context context) {
        this.list = list;
        this.context = context;
        //设置图片缓存大小为程序最大可用内存的1/8
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        if (this.list != null && this.list.size() == BitmapCacheUtil.MAX_NUM) {
            isHaveAddIcon = false;
        }
        keepOne();
    }

    private void keepOne() {
        if (!isHaveAddIcon) {
            if ( list != null &&  list.size()==0) {
                ImagesInfo imageItem = new ImagesInfo();
                imageItem.setResID(R.drawable.icon_add);
                imageItem.setProgress(0);
                list.add(imageItem);
                notifyItemInserted(0);
                isHaveAddIcon = true;
            }
        }
    }

    public ImagesInfo getItemByPosition(int position) {
        if (getCount() > 1) {
            if (getCount() < BitmapCacheUtil.MAX_NUM) {
                return list != null ? list.get(position) : null;
            } else if (getCount() == BitmapCacheUtil.MAX_NUM && isHaveAddIcon) {
                if (position == list.size() - 1) {
                    return null;
                } else {
                    return list.get(position);
                }
            } else {
                return list.get(position);
            }
        }
        return null;
    }


    public List<ImagesInfo> getMaxProgressList() {
        List<ImagesInfo> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ImagesInfo info = list.get(i);
            if (info.getProgress() == maxProgress) {
                data.add(info);
            }
        }
        return data;
    }

    public List<ImagesInfo> getAllData() {
        List<ImagesInfo> data = new ArrayList<>(list);
        if (list != null && list.size() < BitmapCacheUtil.MAX_NUM) {
            data.remove(data.size() - 1);
            return data;
        } else if (list != null && list.size() == BitmapCacheUtil.MAX_NUM && isHaveAddIcon) {
            data.remove(data.size() - 1);
            return data;
        } else {
            return data;
        }
    }

    public void addItem(ImagesInfo imageItem) {
        //加到10张 删除最后一张+ 图标
        if (getTotalCount() == BitmapCacheUtil.MAX_NUM && !isHaveAddIcon) {
            Toast.makeText(context, "不能再添加了!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (this.list != null && imageItem != null) {
                if (getTotalCount() <= BitmapCacheUtil.MAX_NUM) {
                    if (getTotalCount() <= 1) {
                        list.add(0, imageItem);
                        notifyItemInserted(0);
                    } else {
                        int index = list.size() - 1;
                        list.add(index, imageItem);
                        notifyItemInserted(index);
                    }
                    isHaveAddIcon = true;
                }
                if (getTotalCount() == BitmapCacheUtil.MAX_NUM + 1 & isHaveAddIcon) {
                    list.remove(list.size() - 1);
                    //list的size已经减了1 所以提醒的index不要再减1
                    notifyItemRemoved(list.size());
                    isHaveAddIcon = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getTotalCount() {
        return this.list != null ? this.list.size() : 0;
    }

    public int getCount() {
        if (isHaveAddIcon) {
            return this.list != null ? this.list.size() - 1 : 0;
        } else {
            return this.list != null ? this.list.size() : 0;
        }
    }

    public boolean isCanAddPicture() {
        return isHaveAddIcon;
    }


    public void deleteItem(int position) {
        try {
            if (list != null) {
                if (list.size() <= BitmapCacheUtil.MAX_NUM) {
                    if (position != list.size() - 1) {
                        list.remove(position);
                        System.gc();
                        notifyItemRemoved(position);
                        keepOne();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImagesInfo deleteItemBack(int position) {
        ImagesInfo info = null;
        ;
        try {
            if (list != null) {
                if (list.size() <= BitmapCacheUtil.MAX_NUM) {
                    if (position != list.size() - 1) {
                        info = getItemByPosition(position);
                        list.remove(position);
                        System.gc();
                        notifyItemRemoved(position);
                        keepOne();
                    }
                }
            }
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteItem(ImagesInfo info) {
        try {
            if (list != null) {
                if (list.size() <= BitmapCacheUtil.MAX_NUM) {
                    for(int i=0,len=list.size();i<len;i++){
                        ImagesInfo info1 = list.get(i);
                        if(info1.getPath().equals(info.getPath())){
                            list.remove(i);
                            System.gc();
                            notifyItemRemoved(i);
                            keepOne();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItemWithMaxProgress() {
        if (list != null) {
            if (list.size() <= BitmapCacheUtil.MAX_NUM) {
                for (int i = 0; i < list.size(); i++) {
                    ImagesInfo info = list.get(i);
                    if (info.getProgress() == maxProgress) {
                        deleteItem(info);
                    }
                }
            }
        }
    }

    public void deleteAllData() {
        if (list != null) {
            try {
                list.clear();
                isHaveAddIcon = false;
                notifyDataSetChanged();
                keepOne();
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setItemProgress(int position, int progress) {
        try {
            if (list != null) {
                ImagesInfo info = list.get(position);
                info.setProgress(progress);
                notifyItemChanged(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_choose_adapter, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        if (onItemClickListener != null) {
            holder.item_choose_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
        }
        if (list != null) {
            ImagesInfo imageItem = list.get(position);
            dataBind(holder, imageItem);
        }
    }

    private void dataBind(final ImageViewHolder holder, ImagesInfo imageItem) {
        int windowWidth = getWindowWidth(context);
        int width = windowWidth / 3;
        final String imagePath = imageItem.getPath();

        if (imagePath != null && !imagePath.equals("")) {
            File file = new File(imagePath);
            if (file.exists() && file.isFile()) {
                try {
                    // TODO: 2019/1/24
                    //待修改为线程操
                    Bitmap bitmap = mMemoryCache.get(Md5Util.getKey(imagePath));
                    if (bitmap != null) {
                        holder.item_choose_img.setImageBitmap(bitmap);
                    } else {
                        Bitmap compressImage = ImageUtil.getInstance().compressByScale(imagePath, getScale(imagePath));
                        Bitmap bitmap1 = ImageUtil.getInstance().compressBitmapBySize(compressImage, width, width);
                        mMemoryCache.put(Md5Util.getKey(imagePath), bitmap1);
                        if (bitmap1 != null) {
                            holder.item_choose_img.setImageBitmap(bitmap1);
                        }
                        if (compressImage != null) {
                            compressImage.recycle();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (imageItem.getResID() != -1) {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageItem.getResID());
                    Bitmap compressBitmapBySize = ImageUtil.getInstance().compressBitmapBySize(bitmap, width, width);
                    holder.item_choose_img.setImageBitmap(compressBitmapBySize);
                }
            }
        } else {
            if (imageItem.getResID() != -1) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageItem.getResID());
                Bitmap compressBitmapBySize = ImageUtil.getInstance().compressBitmapBySize(bitmap, width, width);
                holder.item_choose_img.setImageBitmap(compressBitmapBySize);
            }
        }

        if (imageItem.getProgress() != 0) {
            holder.item_choose_progress.setVisibility(View.VISIBLE);
            holder.item_choose_progress.setProgress(imageItem.getProgress());
        } else {
            holder.item_choose_progress.setVisibility(View.GONE);
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
        } else if (width <= 800) {
            return 2;
        } else if (width <= 1200) {
            return 4;
        } else {
            return 8;
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_choose_img;
        private ProgressBar item_choose_progress;

        ImageViewHolder(View itemView) {
            super(itemView);
            item_choose_img = itemView.findViewById(R.id.item_choose_img);
            item_choose_progress = itemView.findViewById(R.id.item_choose_progress);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        deleteAllData();
        System.gc();
    }
}
