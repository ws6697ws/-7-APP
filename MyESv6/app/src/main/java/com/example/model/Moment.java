package com.example.model;

import com.orm.SugarRecord;

/**
 * Created by Jay on 2016/1/3 0003.
 */
public class Moment extends SugarRecord {

    private int momentID;
    private String content;
    private String time;
    private int likeCount;
    private int unlikeCount;
    private boolean isLike;
    private boolean isUnlike;
    private Student student;

    public Moment() {
    }


    public int getMomentID() {
        return momentID;
    }

    public void setMomentID(int momentID) {
        this.momentID = momentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getUnlikeCount() {
        return unlikeCount;
    }

    public void setUnlikeCount(int unlikeCount) {
        this.unlikeCount = unlikeCount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public boolean isUnlike() {
        return isUnlike;
    }

    public void setIsUnlike(boolean isUnlike) {
        this.isUnlike = isUnlike;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }


}
