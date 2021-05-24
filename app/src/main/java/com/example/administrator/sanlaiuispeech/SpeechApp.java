package com.example.administrator.sanlaiuispeech;



import com.example.administrator.sanlaiuispeech.work.Constant;

import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.mobileXCorebusiness.base.application.BaseBusApplication;

/**
 * 作者：Created by Administrator on 2019/5/23.
 * 邮箱：
 */
public class SpeechApp extends BaseBusApplication {

    /**
     * 单例
     */
    private static SpeechApp instance;
    /**
     * 获取Application单例
     * @return StarRobotApplication
     */
    public static SpeechApp getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SpeechUtility.createUtility(SpeechApp.this, "appid="+"5ce62818" );

        /**
         * 初始化数据库
         */
        db = new DbHelper(getApplicationContext());
        db.init(Constant.DATABASE_NAME, Constant.DATABASE_VERSION);
    }

}
