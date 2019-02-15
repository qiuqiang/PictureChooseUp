package com.example.picture.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.picture.R;
import com.example.picture.entity.FileTypeInfo;
import com.example.picture.entity.ImagesInfo;

import java.util.ArrayList;
import java.util.List;

/*Created by 邱强 on 2019/1/30.
 * E-Mail 2536555456@qq.com
 */
public class FileTypeAdapter extends RecyclerView.Adapter<FileTypeAdapter.FileTypeHolder> {
    private ArrayList<FileTypeInfo> list;
    private Context context;
    private RecyclerView recyclerView;

    public FileTypeAdapter(ArrayList<FileTypeInfo> list, Context context, RecyclerView recyclerView) {
        this.list = list;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public FileTypeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filetype_adapter, viewGroup, false);
        return new FileTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileTypeHolder holder, int i) {
        holder.fileTypeUpSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSampleClick != null) {
                    onSampleClick.onSampleClick(holder.getAdapterPosition());
                }
            }
        });
        holder.fileType_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddClick != null) {
                    onAddClick.addClick(holder.getAdapterPosition());
                }
            }
        });
        if (list != null) {
            dataBind(holder, i);
        }
    }

    public void setUpCount(int position, String count) {
        if (list != null) {
            list.get(position).setUpCount(count);
            notifyItemChanged(position);
        }
    }


    public int getSubRecycleCount(int firstPosition) {
        if (list != null) {
            FileTypeInfo info = list.get(firstPosition);
            if (info != null) {
                ArrayList<ImagesInfo> paths = info.getPaths();
                return paths != null ? paths.size() : 0;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void dataBind(final FileTypeHolder holder, final int position) {
        FileTypeInfo fileTypeInfo = list.get(position);
        String name = fileTypeInfo.getName();
        if (name != null) {
            holder.fileTypeName.setText(name);
        }
        String upCount = fileTypeInfo.getUpCount();
        holder.fileTypeUpCount.setText(upCount);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.fileType_Recycle.setLayoutManager(linearLayoutManager);
        ArrayList<ImagesInfo> paths = fileTypeInfo.getPaths();
        RecyclerView view = holder.fileType_Recycle;
        final FileTypeUpImagesAdapter adapter = new FileTypeUpImagesAdapter(paths, context, view);
        if (onFileTypeItemClick != null) {
            adapter.setOnItemClick(new FileTypeUpImagesAdapter.OnItemClick() {
                @Override
                public void click(int p) {
                    onFileTypeItemClick.onClick(adapter, position, p);
                }
            });
        }
        if (onSubRecycleDeleteItemClick != null) {
            onSubRecycleDeleteItemClick.onDeleteClick(adapter);
        }
        view.setAdapter(adapter);
    }

    public void setImagesByIndex(int position, ArrayList<ImagesInfo> data) {
        if (data != null && data.size() > 0 && list != null) {
            list.get(position).setPaths(data);
            notifyItemChanged(position);
        }
    }

    /**
     * 是否上传全部类型的文件
     */

    public boolean isUpAll() {
        boolean isUp = false;
        for (int i = 0, len = getItemCount(); i < len; i++) {
            FileTypeInfo item = getItem(i);
            if (Integer.valueOf(item.getUpCount()) > 0) {
                isUp = true;
            } else {
                return false;
            }
        }
        return isUp;
    }

    public void deleteSubItem(FileTypeUpImagesAdapter adapter, int position, int subPosition) {
        if (adapter != null) {
            adapter.deleteItem(subPosition);
            setUpCount(position, adapter.getItemCount() + "");
            notifyItemChanged(position);
        }
    }

    public void deleteSubAllItem(int position) {
        if (list != null) {
            FileTypeInfo info = list.get(position);
            if (info != null) {
                ArrayList<ImagesInfo> paths = info.getPaths();
                if (paths != null) {
                    paths.clear();
                }
                setUpCount(position, "0");
                notifyItemChanged(position);
            }
        }
    }

    public void addImages(int position, ImagesInfo data) {
        if (data != null && list != null) {
            list.get(position).getPaths().add(data);
            notifyItemChanged(position);
        }
    }

    public void addData(FileTypeInfo info) {
        if (info != null && list != null) {
            list.add(info);
            notifyItemChanged(list.size() - 1);
        }
    }

    public void addData(ArrayList<FileTypeInfo> data) {
        if (data != null && list != null) {
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addImagesLists(int position, ArrayList<ImagesInfo> data) {
        if (data != null && list != null) {
            FileTypeInfo info = list.get(position);
            if (info != null) {
                ArrayList<ImagesInfo> paths = info.getPaths();
                if (paths != null && paths.size() > 0) {
                    paths.addAll(data);
                } else {
                    info.setPaths(data);
                }
                notifyItemChanged(position);
            }
        }
    }

    public List<ImagesInfo> getImagesByIndex(int position) {
        return list.get(position).getPaths();
    }

    public ArrayList<FileTypeInfo> getAllData() {
        return list;
    }

    public FileTypeInfo getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private OnSampleClick onSampleClick;

    public void setOnSampleClick(OnSampleClick onSampleClick) {
        this.onSampleClick = onSampleClick;
    }

    public interface OnSampleClick {
        void onSampleClick(int position);
    }

    private OnAddClick onAddClick;

    public void setOnAddClick(OnAddClick onAddClick) {
        this.onAddClick = onAddClick;
    }

    public interface OnAddClick {
        void addClick(int position);
    }

    private OnItemLongClick onItemLongClick;

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    public interface OnItemLongClick {
        void onLongClick(int position);
    }

    private OnFileTypeItemClick onFileTypeItemClick;

    public void setOnFileTypeClick(OnFileTypeItemClick onFileTypeItemClick) {
        this.onFileTypeItemClick = onFileTypeItemClick;
    }

    public interface OnFileTypeItemClick {
        void onClick(FileTypeUpImagesAdapter imagesAdapter, int position, int subPosition);
    }

    private OnSubRecycleDeleteItemClick onSubRecycleDeleteItemClick;

    public void setOnFileDeleteClick(OnSubRecycleDeleteItemClick onSubRecycleDeleteItemClick) {
        this.onSubRecycleDeleteItemClick = onSubRecycleDeleteItemClick;
    }

    //通知子recycle删除item
    public interface OnSubRecycleDeleteItemClick {
        void onDeleteClick(FileTypeUpImagesAdapter imagesAdapter);
    }

    class FileTypeHolder extends RecyclerView.ViewHolder {
        private TextView fileTypeName;
        private TextView fileTypeUpCount;
        private TextView fileTypeUpSample;
        private RecyclerView fileType_Recycle;
        private ImageView fileType_addBtn;

        FileTypeHolder(@NonNull View itemView) {
            super(itemView);
            fileTypeName = itemView.findViewById(R.id.fileType_Name);
            fileTypeUpSample = itemView.findViewById(R.id.fileType_UpSample);
            fileTypeUpCount = itemView.findViewById(R.id.fileType_UpCount);
            fileType_Recycle = itemView.findViewById(R.id.fileType_Recycle);
            fileType_addBtn = itemView.findViewById(R.id.fileType_addBtn);
        }
    }


}
