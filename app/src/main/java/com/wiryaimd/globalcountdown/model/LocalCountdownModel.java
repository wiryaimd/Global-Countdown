package com.wiryaimd.globalcountdown.model;

public class LocalCountdownModel {

    private String title, desc;
    private DateModel date; // year-month-days-hours-minutes
    private boolean notification;
    private long millisTime;

    public LocalCountdownModel(String title, String desc, DateModel date, boolean notification, long millisTime) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.notification = notification;
        this.millisTime = millisTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public DateModel getDate() {
        return date;
    }

    public boolean isNotification() {
        return notification;
    }

    public long getMillisTime() {
        return millisTime;
    }
}
