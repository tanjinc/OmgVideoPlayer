package com.tanjinc.omgvideoplayer.listPage;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.listPage.bean.BaseItem;
import com.tanjinc.omgvideoplayer.utils.ImageUtils;
import com.tanjinc.playermanager.R;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;

import static com.tanjinc.omgvideoplayer.listPage.bean.BaseItem.TYPE_A;
import static com.tanjinc.omgvideoplayer.listPage.bean.BaseItem.TYPE_B;
import static com.tanjinc.omgvideoplayer.listPage.bean.BaseItem.TYPE_C;
import static com.tanjinc.omgvideoplayer.listPage.bean.BaseItem.TYPE_VIDEO;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BaseItem> mDataArray = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    public VideoAdapter() {
    }

    public void setData(List<BaseItem> items) {
        mDataArray = items;
        notifyDataSetChanged();
    }

    public void addFootItems(List<BaseItem> items) {
        mDataArray.addAll(items);
        notifyDataSetChanged();
    }

    public void addHeaderItems(List<VideoBean> items) {
        mDataArray.addAll(0, items);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_A:
                TextView viewA = new TextView(parent.getContext());
                viewA.setText("AA");
                viewA.setBackgroundColor(Color.RED);
                return new HolderA(viewA);
            case TYPE_B:
                TextView viewB = new TextView(parent.getContext());
                viewB.setText("BB");
                viewB.setBackgroundColor(Color.BLUE);
                return new HolderB(viewB);
            case TYPE_C:
                TextView viewC = new TextView(parent.getContext());
                viewC.setText("CC");
                viewC.setBackgroundColor(Color.YELLOW);
                return new HolderA(viewC);
            case TYPE_VIDEO:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
                return new VideoViewHolder(view);
                default:
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_A:
                bindTypeA(viewHolder, position);
                break;
            case TYPE_B:
                bindTypeB(viewHolder, position);
                break;
            case TYPE_C:
                bindTypeC(viewHolder, position);
                break;
            case TYPE_VIDEO:
                bindTypeVideo(viewHolder, position);
                break;
        }
    }

    private void bindTypeA(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private void bindTypeB(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private void bindTypeC(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private void bindTypeVideo(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder == null || !(viewHolder instanceof VideoViewHolder)) {
            return;
        }
        VideoViewHolder vh = (VideoViewHolder) viewHolder;

        final VideoBean item = (VideoBean) mDataArray.get(position);

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

    @Override
    public int getItemCount() {
        return mDataArray.size() + 1; //add Loading View
    }


    public BaseItem getItem(int position) {
        return mDataArray.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mDataArray.size()) {
            return 0;
        }
        return mDataArray.get(position).getType();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener  {
        void OnItemClick(int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
        if (layout != null && layout instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) layout;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_A ) {
                        return manager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }
}
