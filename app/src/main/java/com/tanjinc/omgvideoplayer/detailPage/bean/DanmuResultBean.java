package com.tanjinc.omgvideoplayer.detailPage.bean;


import java.util.List;

/**
 * Author by tanjincheng, Date on 18-11-1.
 */
public class DanmuResultBean {

    private int code;
    private String message;

    private List<DanmuContentBean> msg_list;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DanmuContentBean> getMsglist() {
        return msg_list;
    }

    public void setMsgList(List<DanmuContentBean> msg_list) {
        this.msg_list = msg_list;
    }

}
