package com.tanjinc.omvideoplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.tanjinc.omvideoplayer.utils.ImageLoader;
import com.tanjinc.playermanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanjincheng on 17/7/8.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context mContext;
    private List<MainActivity.VideoItem> mDatas = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public VideoAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public void setDatas(List datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mDatas != null && position < mDatas.size()) {
            ImageLoader.loadImage(mContext, mDatas.get(position).getThumbPath(), holder.thumb);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public MainActivity.VideoItem getItem(int position) {
        return mDatas.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        ViewGroup root;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            thumb = (ImageView) view.findViewById(R.id.thumb_img);
        }
        public int getWidth() {
            return root.getMeasuredWidth();
        }

        public int getHeight() {
            return root.getMeasuredHeight();
        }
    }
}
