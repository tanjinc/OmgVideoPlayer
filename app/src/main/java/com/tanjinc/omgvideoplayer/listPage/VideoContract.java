package com.tanjinc.omgvideoplayer.listPage;

import com.tanjinc.omgvideoplayer.listPage.bean.BaseItem;

import java.util.List;

public class VideoContract {

    public interface View {
        void showVideoList(List<BaseItem> VideoList, boolean isAppend);

        void setPresenter(Presenter presenter);
    }

    public interface Presenter {
        void loadVideo();       //加载数据

        void loadMoreVideo();   //加载更多

        void refresh();         //刷新
    }
}
