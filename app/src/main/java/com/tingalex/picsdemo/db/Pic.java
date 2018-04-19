package com.tingalex.picsdemo.db;

import java.util.UUID;

import cn.bmob.v3.BmobObject;

/**
 * Created by oureda on 2018/4/18.
 */

public class Pic extends BmobObject {
    private String belongto;
    private String weburl;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid() {
        this.uid = UUID.randomUUID().toString();
    }

    public String getBelongto() {
        return belongto;
    }

    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
