package com.wiryaimd.globalcountdown.model;

import java.util.ArrayList;

public class GlobalCountdownModel {

    private String username, title, desc, uuid, imgurl;
    private DateModel dateModel;
    private boolean notification;
    private ArrayList<String> voter, notifier;
    private long millisTime;

    public GlobalCountdownModel(){}

    public GlobalCountdownModel(String username, String title, String desc, String uuid, String imgurl, DateModel dateModel, boolean notification, ArrayList<String> voter, ArrayList<String> notifier, long millisTime) {
        this.username = username;
        this.title = title;
        this.desc = desc;
        this.uuid = uuid;
        this.imgurl = imgurl;
        this.dateModel = dateModel;
        this.notification = notification;
        this.voter = voter;
        this.notifier = notifier;
        this.millisTime = millisTime;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUuid() {
        return uuid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public DateModel getDateModel() {
        return dateModel;
    }

    public boolean isNotification() {
        return notification;
    }

    public ArrayList<String> getVoter() {
        return voter;
    }

    public ArrayList<String> getNotifier() {
        return notifier;
    }

    public long getMillisTime() {
        return millisTime;
    }
}
