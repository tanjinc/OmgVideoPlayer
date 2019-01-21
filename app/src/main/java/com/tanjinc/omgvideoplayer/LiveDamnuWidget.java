package com.tanjinc.omgvideoplayer;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.tanjinc.omgvideoplayer.detailPage.bean.DanmuContentBean;
import com.tanjinc.omgvideoplayer.detailPage.bean.DanmuResultBean;
import com.tanjinc.omgvideoplayer.widget.BaseWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.ui.widget.DanmakuView;
import com.tanjinc.playermanager.R;

public class LiveDamnuWidget extends BaseWidget {

    private DanmakuView mDanmakuView;
    private DanmakuContext mContext;
    private CMDanmakuParser mParser;

    private String jsonStr = "{\n" +
            "   \"code\" : 0,\n" +
            "   \"message\" : \"OK\",\n" +
            "   \"msg_list\" : [\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"level\" : 1,\n" +
            "            \"location\" : \"\",\n" +
            "            \"logourl\" : \"http://huyaimg.msstatic.com/avatar/1036/52/9d9e4ac6a57165da3032baf813098f_180_135.jpg\",\n" +
            "            \"pet\" : 1,\n" +
            "            \"rank\" : 32587,\n" +
            "            \"type\" : 1,\n" +
            "            \"type_name\" : \"剑士\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065127,\n" +
            "         \"msg_type\" : 3,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"﹋执念゛\",\n" +
            "         \"sender_uid\" : 1617312895,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"level\" : 1,\n" +
            "            \"location\" : \"\",\n" +
            "            \"logourl\" : \"http://huyaimg.msstatic.com/avatar/1061/2d/772057d35a3d7683aa13c81078ae56_180_135.jpg?1498277264\",\n" +
            "            \"pet\" : 1,\n" +
            "            \"rank\" : 32587,\n" +
            "            \"type\" : 1,\n" +
            "            \"type_name\" : \"剑士\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065128,\n" +
            "         \"msg_type\" : 3,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"小白♛\",\n" +
            "         \"sender_uid\" : 451308117,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"msg\" : \"充VIP会员给你加速玩\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065129,\n" +
            "         \"msg_type\" : 1,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"『唯爱』\",\n" +
            "         \"sender_uid\" : 1649909357,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"msg\" : \"被三号发现了\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065130,\n" +
            "         \"msg_type\" : 1,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"2312883838yy莫然\",\n" +
            "         \"sender_uid\" : 2312793802,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"msg\" : \"越南的我老乡\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065131,\n" +
            "         \"msg_type\" : 1,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"光哥\",\n" +
            "         \"sender_uid\" : 2249466462,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"level\" : 1,\n" +
            "            \"location\" : \"\",\n" +
            "            \"logourl\" : \"http://huyaimg.msstatic.com/avatar/1040/b8/a34b881594a5405234c715250479b4_180_135.jpg?1497284104\",\n" +
            "            \"pet\" : 1,\n" +
            "            \"rank\" : 32587,\n" +
            "            \"type\" : 1,\n" +
            "            \"type_name\" : \"剑士\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065131,\n" +
            "         \"msg_type\" : 3,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"沿途与你、\",\n" +
            "         \"sender_uid\" : 1785835158,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"msg\" : \"我就听懂了三个字不求人\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065132,\n" +
            "         \"msg_type\" : 1,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"几鱼\",\n" +
            "         \"sender_uid\" : 2338494932,\n" +
            "         \"user_type_id\" : 1\n" +
            "      },\n" +
            "      {\n" +
            "         \"msg_content\" : {\n" +
            "            \"msg\" : \"今天是虎牙活动连麦的软件，不能抠图[感动]\"\n" +
            "         },\n" +
            "         \"msg_send_time\" : 1541065135,\n" +
            "         \"msg_type\" : 1,\n" +
            "         \"presenter_uid\" : 2227875499,\n" +
            "         \"sender_nick\" : \"【求求部落】小秘书\",\n" +
            "         \"sender_uid\" : 2299022222,\n" +
            "         \"user_type_id\" : 1\n" +
            "      }\n" +
            "   ]\n" +
            "}\n";

    public LiveDamnuWidget() {
        super(R.layout.danmu_layout);

    }

    @Override
    public void attachTo(@NonNull ViewGroup parent) {
        super.attachTo(parent);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmuView);
        init();
        generateSomeDanmaku();
        show();
    }

    @Override
    public void detach() {
        super.detach();
        if (mDanmakuView != null) {
            mDanmakuView.release();
        }
    }

    private void init() {
        mContext = DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 8); // 滚动弹幕最大显示5行

        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 10) //描边的厚度
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //弹幕的速度。注意！此值越小，速度越快！值越大，速度越慢。// by phil
                .setScaleTextSize(1.2f)  //缩放的值
                //.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        mParser = new CMDanmakuParser(mContext);
        mParser.setDisplayer(new AndroidDisplayer());
        mDanmakuView.prepare(mParser, mContext);

        mDanmakuView.showFPS(true);
        mDanmakuView.enableDanmakuDrawingCache(true);

        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.d("弹幕文本", "danmakuShown text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
        }
    }

    public DanmakuView getDanmakuView() {
        return mDanmakuView;
    }

    public void onPause() {
        if (mDanmakuView != null) {
            mDanmakuView.pause();
        }
    }

    public void onResume() {
        if (mDanmakuView != null) {
            mDanmakuView.resume();
        }
    }


    private void addDanmaku(String textContent) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL, mContext);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        danmaku.text = textContent;
        danmaku.padding = 15;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 14f * (mParser.getDisplayer().getDensity() - 0.6f); //文本弹幕字体大小
        danmaku.textColor = Color.WHITE; //文本的颜色
        danmaku.textShadowColor = getRandomColor(); //文本弹幕描边的颜色
//        danmaku.underlineColor = Color.DKGRAY; //文本弹幕下划线的颜色
//        danmaku.borderColor = getRandomColor(); //边框的颜色

        mDanmakuView.addDanmaku(danmaku);
    }

    public void addDanmaku(boolean islive) {
        DanmuResultBean danmuResultBean = new Gson().fromJson(jsonStr, DanmuResultBean.class);
        if (danmuResultBean != null && danmuResultBean.getMsglist() != null) {
            List<DanmuContentBean> danmuContentBeans = danmuResultBean.getMsglist();
            for (DanmuContentBean bean :
                    danmuContentBeans) {
                if (bean.getMsg_type() == 1 && bean.getMsg_content() != null) {
                    addDanmaku(bean.getMsg_content().getMsg());
                }
            }
        }

    }

    //随机生成一些弹幕内容以供测试
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            @Override
            public void run() {
                while (mDanmakuView.isShown()){
                    int time = new Random().nextInt(100);
                    String content = time +"" ;
                    addDanmaku(true);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int getRandomColor() {
        int[] colors = {Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.CYAN, Color.BLACK, Color.DKGRAY};
        int i = ((int) (Math.random() * 10)) % colors.length;
        return colors[i];
    }
    public class CMDanmakuParser extends BaseDanmakuParser {

        public CMDanmakuParser(DanmakuContext context) {
            setConfig(context);
        }

        @Override
        public Danmakus parse() {
            if (this.mDataSource != null &&  this.mDataSource instanceof JSONSource) {
                JSONSource jsonSource = (JSONSource) this.mDataSource;
                return new Danmakus();
            } else {
                return new Danmakus();
            }
        }

    }
}
