package com.example.administrator.sanlaiuispeech.bean;

/**
 * 作者：Created by Administrator on 2019/5/31.
 * 邮箱：
 */
public class InfoChat {
    String content;
    int type;

    public InfoChat(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
