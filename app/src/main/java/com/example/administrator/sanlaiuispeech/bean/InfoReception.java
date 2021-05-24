package com.example.administrator.sanlaiuispeech.bean;

/**
 * 作者：Created by Administrator on 2019/7/16.
 * 邮箱：
 */
public class InfoReception {
    private  String tvExhibition1;
    private  String tvCode;

    public InfoReception(String tvExhibition1, String tvCode) {
        this.tvExhibition1 = tvExhibition1;
        this.tvCode = tvCode;
    }

    public String getTvExhibition1() {
        return tvExhibition1;
    }

    public void setTvExhibition1(String tvExhibition1) {
        this.tvExhibition1 = tvExhibition1;
    }

    public String getTvCode() {
        return tvCode;
    }

    public void setTvCode(String tvCode) {
        this.tvCode = tvCode;
    }
}
