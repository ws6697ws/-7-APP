package com.example.model;

/**
 * Created by Jay on 2016/1/3 0003.
 */
public class ResponseObj {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ResponseObj(int code, String data) {
        this.code = code;
        this.data = data;
    }

    private int code;
    private String data;

}
