package com.tingalex.picsdemo.global;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by oureda on 2018/5/2.
 */

public class MyApplication extends Application {
    private String uid;
    private String email;
    private String password;
    private Double credit;
    private String name;
    private String headpic;
    private String bmobId;
    private Context myContext;

    public Context getMyContext() {
        return myContext;
    }

    public String getBmobId() {
        return bmobId;
    }

    private String getBmobIdFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString("bmobId", "");
    }

    public void setBmobId(String bmobId) {
        this.bmobId = bmobId;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("bmobId", bmobId);
        editor.apply();
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        email = getEmailFromShared();
        password = getPasswordFromShared();
        uid = getUidFromShared();
        bmobId = getBmobIdFromShared();
        myContext=getApplicationContext();
        Log.i("bmob", "onCreate: stored email is " + email);
        Log.i("bmob", "onCreate: stored password is " + password);
        Log.i("bmob", "onCreate: stored uid is " + uid);
    }

    public void clearAllInfo() {
        uid = "";
        email = "";
        password = "";
        credit = 0.0;
        name = "";
        headpic = "";
        bmobId = "";
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public String getName() {
        return name;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    private String getNameFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString("name", "");
    }

    public void setName(String name) {
        this.name = name;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.apply();
    }

    private String getUidFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString("uid", "");
    }

//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
//        editor.putString("uid", uid);
//        editor.apply();
//    }

    public String getEmail() {
        return email;
    }

    private String getEmailFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString("email", "");
    }

    public void setEmail(String email) {
        this.email = email;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getPassword() {
        return password;
    }

    private String getPasswordFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return preferences.getString("password", "");
    }

    public void setPassword(String password) {
        this.password = password;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("password", password);
        editor.apply();
    }

    public Double getCredit() {
        return credit;
    }

    private Double getCreditFromShared() {
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        return Double.valueOf(preferences.getString("credit", ""));
    }

    public void setCredit(Double credit) {
        this.credit = credit;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("credit", credit.toString());
        editor.apply();

    }


}
