package com.tingalex.picsdemo.db;

import java.util.List;
import java.util.UUID;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by oureda on 2018/4/18.
 */

public class Good extends BmobObject {
    private String title;
    private String description;
    private Double price;
    private Boolean containPackageCost;
    private String categroy;
    private String tradeState;
    private String belongto;
    private String sellto;
    private List<String> picurls;
    private Users belongs;
    private Users sells;
    private BmobRelation likes;

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    //
    public Users getBelongs() {
        return belongs;
    }

    public void setBelongs(Users belongs) {
        this.belongs = belongs;
    }

    public Users getSells() {
        return sells;
    }

    public void setSells(Users sells) {
        this.sells = sells;
    }

    public String getBelongto() {
        return belongto;
    }

    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    public String getSellto() {
        return sellto;
    }

    public void setSellto(String sellto) {
        this.sellto = sellto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getContainPackageCost() {
        return containPackageCost;
    }

    public void setContainPackageCost(Boolean containPackageCost) {
        this.containPackageCost = containPackageCost;
    }

    public String getCategroy() {
        return categroy;
    }

    public void setCategroy(String categroy) {
        this.categroy = categroy;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public List<String> getPicurls() {
        return picurls;
    }

    public void setPicurls(List<String> picurls) {
        this.picurls = picurls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
