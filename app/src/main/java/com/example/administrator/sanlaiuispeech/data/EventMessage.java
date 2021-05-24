package com.example.administrator.sanlaiuispeech.data;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/6/25
 * @emial 732603278@qq.com
 * @function
 */
public class EventMessage {

    public int msgType;
    public Object object;

    public EventMessage(int msgType, Object object) {
        super();
        this.msgType = msgType;
        this.object = object;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
