package com.tingalex.picsdemo.db;

import java.util.List;
import java.util.UUID;

import cn.bmob.v3.BmobObject;

/**
 * Created by oureda on 2018/4/18.
 */

public class Good extends BmobObject {
    private String title;
    private String uid;
    private List<String> picurls;

    public List<String> getPicurls() {
        return picurls;
    }

    public void setPicurls(List<String> picurls) {
        this.picurls = picurls;
    }

    public String getTitle() {
        return title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid() {
        this.uid = UUID.randomUUID().toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
