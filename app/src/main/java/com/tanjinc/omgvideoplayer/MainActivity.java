package com.tanjinc.omgvideoplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanjinc.omgvideoplayer.utils.ImageUtils;

import java.util.ArrayList;

import com.tanjinc.playermanager.R;



/**
 * Created by tanjinc on 17-4-5.
 */

public class MainActivity extends Activity {
    private static final String TAG = "TestActivity";


    private String mVideoUrl = "http://video.mp.sj.360.cn/vod_zhushou/vod-shouzhu-bj/e604948bb5c58e88b95e25fb54846d6e.mp4";
    private String mImageUrl = "http://p8.qhimg.com/t019283d762742416df.jpg";

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private ArrayList<VideoItem> mItemArrayList;
    private int mCurrentPosition = -1;
    SampleVideoPlayer mVideoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mMyAdapter = new MyAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mMyAdapter);

        setVideoData();
        mMyAdapter.setData(mItemArrayList);
        mMyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                openVideo(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
            mVideoPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayer != null) {
            mVideoPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    private void setVideoData() {
        mItemArrayList = new ArrayList<>();
        for(int i=0; i< 6; i++) {
            VideoItem item = new VideoItem();
            item.setPreImageUrl(mImageUrl);
            item.setVideoUrl(mVideoUrl);
            item.setVideoTitle("");
            mItemArrayList.add(item);
        }
    }


    public void openVideo(int position) {
        if ( position == mCurrentPosition) {
            return;
        } else {
            if (mCurrentPosition != -1 ) {
                ViewHolder preViewHolder = (ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(mCurrentPosition);
                if (preViewHolder != null) {
                    preViewHolder.setActive(false);
                }
                if (mVideoPlayer != null) {
                    mVideoPlayer.onDestroy();
                    mVideoPlayer = null;
                }
            }
        }

        mCurrentPosition = position;
        ViewHolder currentVH = (ViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
        final VideoItem item = mItemArrayList.get(position);
        BaseVideoPlayer.MediaPlayerType mediaPlayerType = BaseVideoPlayer.MediaPlayerType.EXO_PLAYER;
        if (position == 0)
            mediaPlayerType = BaseVideoPlayer.MediaPlayerType.EXO_PLAYER;
        if (position == 1)
            mediaPlayerType = BaseVideoPlayer.MediaPlayerType.MEDIA_PLAYER;

        mVideoPlayer = new SampleVideoPlayer(this, mediaPlayerType);
        mVideoPlayer.setRootView(currentVH.mVideoRoot);
        mVideoPlayer.setPreviewImage(currentVH.mPreViewImg.getDrawable());
        mVideoPlayer.setVideoUrl(item.getVideoUrl());
        mVideoPlayer.setTitle(item.getVideoTitle());
        mVideoPlayer.start();
        currentVH.setActive(true);
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<VideoItem> mItems;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_layout, parent ,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final VideoItem item = mItems.get(position);
            ImageUtils.loadImage(item.preImageUrl, holder.mPreViewImg);
            holder.mPreViewImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.OnItemClick(position);
                    }
                }
            });
            if (position == 0) {
                holder.mMediaType.setText("EXOPlayer");
            }
            if (position == 1) {
                holder.mMediaType.setText("NMDPlayer");
            }
            if (position == 2) {
                holder.mMediaType.setText("MediaPlayer");
            }
        }


        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setData(ArrayList<VideoItem> list) {
            mItems = list;
            notifyDataSetChanged();
        }

        private OnItemClickListener mOnItemClickListener;
        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }
    }

    interface OnItemClickListener  {
        void OnItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup mVideoRoot;
        private ImageView mPreViewImg;
        private ImageView mPlayBtn;
        private TextView mMediaType;

        public ViewHolder(View itemView) {
            super(itemView);
            mVideoRoot = (ViewGroup) itemView.findViewById(R.id.test_video_root);
            mPreViewImg = (ImageView) itemView.findViewById(R.id.test_preview_img);
            mPlayBtn = (ImageView) itemView.findViewById(R.id.test_play_btn1);
            mMediaType = (TextView) itemView.findViewById(R.id.text_video_type);
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

    class VideoItem{
        String preImageUrl;
        String videoUrl;
        String videoTitle;

        public String getPreImageUrl() {
            return preImageUrl;
        }

        public void setPreImageUrl(String preImageUrl) {
            this.preImageUrl = preImageUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

    }

}
