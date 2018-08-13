package com.tanjinc.omgvideoplayer.listPage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.utils.ImageUtils;
import com.tanjinc.playermanager.R;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_TYPE_LOADING = 1000;
    private List<VideoBean> mDataArray = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    public VideoAdapter() {
    }

    public void setData(List<VideoBean> items) {
        mDataArray = items;
        notifyDataSetChanged();
    }

    public void addFootItems(List<VideoBean> items) {
        mDataArray.addAll(items);
        notifyDataSetChanged();
    }

    public void addHeaderItems(List<VideoBean> items) {
        mDataArray.addAll(0, items);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item_loading, parent, false);
            return new LoadMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_LOADING) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) viewHolder;
            loadMoreViewHolder.loadMoreView.setVisibility(View.VISIBLE);
        } else {
            ViewHolder vh = (ViewHolder) viewHolder;
            final VideoBean item = mDataArray.get(position);
            ImageUtils.loadImage(item.getPreImageUrl(), vh.mPreViewImg);
            vh.mPreViewImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.OnItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataArray.size() + 1; //add Loading View
    }


    public VideoBean getItem(int position) {
        return mDataArray.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_LOADING;
        }
        return super.getItemViewType(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener  {
        void OnItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup mVideoRoot;
        public ImageView mPreViewImg;
        public ImageView mPlayBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mVideoRoot = (ViewGroup) itemView.findViewById(R.id.test_video_root);
            mPreViewImg = (ImageView) itemView.findViewById(R.id.test_preview_img);
            mPlayBtn = (ImageView) itemView.findViewById(R.id.test_play_btn1);
        }

        public void setActive(boolean active) {
            if (active) {
                mPlayBtn.setVisibility(View.GONE);
                mPreViewImg.setVisibility(View.GONE);
            } else {
                mPlayBtn.setVisibility(View.VISIBLE);
                mPreViewImg.setVisibility(View.VISIBLE);
            }
        }
    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        View loadMoreView;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            loadMoreView = itemView;
        }
    }
}
