package com.tanjinc.omgvideoplayer.detailPage.bean;

/**
 * Author by tanjincheng, Date on 18-11-1.
 */
public class DanmuContentBean {
    private MsgContentBean msg_content;
    private int msg_send_time;
    private int msg_type;
    private long presenter_uid;
    private String sender_nick;
    private long sender_uid;
    private int user_type_id;

    public MsgContentBean getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(MsgContentBean msg_content) {
        this.msg_content = msg_content;
    }

    public int getMsg_send_time() {
        return msg_send_time;
    }

    public void setMsg_send_time(int msg_send_time) {
        this.msg_send_time = msg_send_time;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public long getPresenter_uid() {
        return presenter_uid;
    }

    public void setPresenter_uid(long presenter_uid) {
        this.presenter_uid = presenter_uid;
    }

    public String getSender_nick() {
        return sender_nick;
    }

    public void setSender_nick(String sender_nick) {
        this.sender_nick = sender_nick;
    }

    public long getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(long sender_uid) {
        this.sender_uid = sender_uid;
    }

    public int getUser_type_id() {
        return user_type_id;
    }

    public void setUser_type_id(int user_type_id) {
        this.user_type_id = user_type_id;
    }

    public static class MsgContentBean {
        private int level;
        private String location;
        private String logourl;
        private int pet;
        private int rank;
        private int type;
        private String type_name;
        private String msg;


        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLogourl() {
            return logourl;
        }

        public void setLogourl(String logourl) {
            this.logourl = logourl;
        }

        public int getPet() {
            return pet;
        }

        public void setPet(int pet) {
            this.pet = pet;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }
    }
}
