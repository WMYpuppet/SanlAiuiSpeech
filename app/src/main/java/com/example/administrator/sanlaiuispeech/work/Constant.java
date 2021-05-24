package com.example.administrator.sanlaiuispeech.work;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/10
 * @emial 732603278@qq.com
 * @function
 */
public class Constant {
    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "LOVEROBOT";

    /**
     * 数据库版本
     */
    public static final int DATABASE_VERSION = 5;

    /**
     * 场景切换请求响应结果
     */
    public interface SWITCH_RESPONSE {
        int IGNORE = 0; //忽略
        int SWITCH = 1; //直接切换
        int INQUERY = 2;//询问
    }

    public interface SEMANTIC_CODE {
        int AIUI_RESOURCE = 101; //忽略
        int HOME_UI_CHANGE = 102; //直接切换
        int CHAT_UI_CHANGE = 103;//询问
        int QUES_UI_CHANGE = 104;//询问
        int ANSWER_UI_CHANGE = 105;//询问
        int RECEPTION_DETAIL_UI_CHANGE = 301;
    }

    public static String exhibition1 = "1号展台已到达";
    public static String exhibition2 = "2号展台已到达";
    public static String exhibition3 = "3号展台已到达";
    public static String exhibition4 = "4号展台已到达";
    public static String exhibition5 = "5号展台已到达";
    public static int exhibitions=100;



}
