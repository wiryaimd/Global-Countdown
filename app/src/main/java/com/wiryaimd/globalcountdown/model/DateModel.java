package com.wiryaimd.globalcountdown.model;

public class DateModel {

    private String year, month, day, hours, minute;

    public DateModel(){}

    public DateModel(String year, String month, String day, String hours, String minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minute = minute;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getHours() {
        return hours;
    }

    public String getMinute() {
        return minute;
    }
}
