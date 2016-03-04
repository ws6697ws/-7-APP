package com.example.model;

import com.orm.SugarRecord;

/**
 * Created by Jay on 2016/1/3 0003.
 */
public class Syllabus extends SugarRecord {

    private String name;
    private String place;
    private int fromTime;
    private int toTime;
    private String week;
    private int day;
    private String year;
    private String term;

    public Syllabus() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getFromTime() {
        return fromTime;
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public int getToTime() {
        return toTime;
    }

    public void setToTime(int toTime) {
        this.toTime = toTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
