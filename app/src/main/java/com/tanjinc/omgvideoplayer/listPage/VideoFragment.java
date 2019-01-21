package com.tanjinc.omgvideoplayer.listPage;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanjinc.omgvideoplayer.BaseVideoPlayer;
import com.tanjinc.omgvideoplayer.SampleVideoPlayer;
import com.tanjinc.omgvideoplayer.listPage.bean.BaseItem;
import com.tanjinc.omgvideoplayer.listPage.bean.VideoBean;
import com.tanjinc.playermanager.R;

import java.util.List;


public class VideoFragment extends Fragment implements VideoContract.View {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VideoAdapter mAdapter;
    private VideoContract.Presenter mPresenter;
    private int mCurrentPosition = -1;
    private SampleVideoPlayer mVideoPlayer;



    public VideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_list_layout, container, false);
        mAdapter = new VideoAdapter();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，且是最后一个可见的item时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mPresenter.loadMoreVideo();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //The last visible item
                if (layoutManager != null) {
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });

        mAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                openVideo(position);
            }
        });
        if (mPresenter != null) {
            mPresenter.loadVideo();
        }


        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mVideoPlayer != null) {
            mVideoPlayer.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoPlayer != null) {
            mVideoPlayer.onResume();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mVideoPlayer != null) {
            mVideoPlayer.onDestroy();
        }
    }

    @Override
    public void showVideoList(List<BaseItem> dataList, boolean isAppend) {
        if (isAppend) {
            mAdapter.addFootItems(dataList);
        } else {
            mAdapter.setData(dataList);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setPresenter(VideoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void openVideo(int position) {
        if ( position == mCurrentPosition) {
            return;
        } else {
            if (mCurrentPosition != -1 ) {
                VideoViewHolder preViewHolder = (VideoViewHolder) mRecyclerView.findViewHolderForLayoutPosition(mCurrentPosition);
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
        final VideoViewHolder currentVH = (VideoViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
        final VideoBean item = (VideoBean) mAdapter.getItem(position);
        mVideoPlayer = new SampleVideoPlayer(getActivity(), BaseVideoPlayer.MediaPlayerType.EXO_PLAYER);
        mVideoPlayer.setRootView(currentVH.mVideoRoot);
        mVideoPlayer.setPreviewImage(currentVH.mPreViewImg);
        mVideoPlayer.setVideoUrl(item.getVideoUrl());
        mVideoPlayer.setTitle(item.getVideoTitle());
        mVideoPlayer.start();
        mVideoPlayer.setOnFloatListener(new BaseVideoPlayer.OnFloatListener() {
            @Override
            public void startFloat() {
                currentVH.mPreViewImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void exitFloat() {
                currentVH.setActive(false);
                mCurrentPosition = -1;
                mVideoPlayer = null;
            }
        });
        currentVH.setActive(true);
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpace;

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
            if (viewHolder instanceof HolderA) {

            }
        }
    }
}
