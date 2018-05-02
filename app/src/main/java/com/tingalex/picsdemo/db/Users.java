package com.tingalex.picsdemo.db;

import java.util.UUID;

import cn.bmob.v3.BmobObject;

/**
 * Created by oureda on 2018/4/18.
 */

public class Users extends BmobObject {
    private String email;
    private String name;
    private String password;
    private String phone;
    private String uid;
    private String headpic;
    private Double credit;

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid() {
        this.uid = UUID.randomUUID().toString();
    }
}
