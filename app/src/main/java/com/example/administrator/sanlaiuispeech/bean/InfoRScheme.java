package com.example.administrator.sanlaiuispeech.bean;

/**
 * 作者：Created by Administrator on 2019/6/25.
 * 邮箱：
 */
public class InfoRScheme {
    private String schemeID;
    private String time;

    public InfoRScheme(String schemeID, String time) {
        this.schemeID = schemeID;
        this.time = time;
    }

    public String getSchemeID() {
        return schemeID;
    }

    public void setSchemeID(String schemeID) {
        this.schemeID = schemeID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
