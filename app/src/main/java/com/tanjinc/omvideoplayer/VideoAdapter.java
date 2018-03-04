package com.tanjinc.omvideoplayer;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
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
            holder.root.setOutlineProvider(new MyViewOutlineProvider(50));
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
        View playBtn;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view.findViewById(R.id.video_player_container);
            thumb = (ImageView) view.findViewById(R.id.thumb_img);
            playBtn = view.findViewById(R.id.play_btn);
        }
        public int getWidth() {
            return root.getMeasuredWidth();
        }

        public int getHeight() {
            return root.getMeasuredHeight();
        }
    }

    public class MyViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public MyViewOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            int leftMargin = 0;
            int topMargin = 0;
            Rect selfRect = new Rect(leftMargin, topMargin,
                    rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
            outline.setRoundRect(selfRect, mRadius);
        }
    }
}
