package com.example.administrator.sanlaiuispeech.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.administrator.sanlaiuispeech.SpeechApp;
import com.iflytek.android.framework.db.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/10
 * @emial 732603278@qq.com
 * @function
 */
public class AIUIQaDao {

    private DbHelper db;

    public AIUIQaDao(Context context) {
        db = ((SpeechApp) context.getApplicationContext()).db;
        db.checkOrCreateTable(AIUIQaDto.class);
    }

    /**
     * 批量插入配置数据到数据表中
     *
     * @param list
     */
    public boolean insertSettingList(List<AIUIQaDto> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        db.checkOrCreateTable(AIUIQaDto.class);
        SQLiteDatabase db1 = db.getDb();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("insert into T_AIUIQA_DATA");
            sql.append("(ques,answer)");
            sql.append("values(?,?)");

            SQLiteStatement stat = db1.compileStatement(sql.toString());
            db1.beginTransaction();
            for (AIUIQaDto info : list) {
                stat.bindString(1, info.getQues());
                stat.bindString(2, info.getAnswer());
                long result = stat.executeInsert();
                if (result < 0) {
                    return false;
                }
            }
            db1.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != db) {
                    db1.endTransaction();
                    // db1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 添加配置项数据
     *
     * @param settingDto
     * @return
     */
    public boolean addSettingData(AIUIQaDto settingDto) {
        Boolean result = false;
        try {
            db.getDb().execSQL("INSERT INTO T_SETTING(" + settingDto.getQues() + "," + settingDto.getAnswer() + ") ");
            result = true;
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * 获取配置项数据list
     *
     * @return
     */
    public List<AIUIQaDto> getDicList(String keyword) {
        List<AIUIQaDto> list = db.queryList(AIUIQaDto.class, ":ques = ? ", keyword);
        return list;
    }

    /**
     * 获取配置项数据list
     *
     * @return
     */
    public List<AIUIQaDto> getRandomList() {
        Random random = new Random();
        List<AIUIQaDto> lists = new ArrayList<>();
        List<AIUIQaDto> list = getAttachmentList();
        for (int i = 0; i < 6; i++) {
            lists.add(list.get(random.nextInt(100)));
        }
        return lists;
    }


    /**
     * 获取配置项数据
     *
     * @param key
     * @return SettingDto
     */
    public AIUIQaDto getSettingData(String key) {
        AIUIQaDto setting = db.queryFrist(AIUIQaDto.class, ":key = ?", key);
        return setting;
    }

    /**
     * 获取所有社区多媒体材料
     */
    public List<AIUIQaDto> getAttachmentList() {
        List<AIUIQaDto> list = db.queryList(AIUIQaDto.class, ":ques <> ? order by ques asc", "");
        return list;
    }

    /**
     * 删除配置信息
     *
     * @return
     */
    public boolean deleteAllData() {
        Boolean result = false;
        try {
            db.getDb().execSQL("delete from T_AIUIQA_DATA");
            result = true;

        } catch (Exception e) {
            return result;
        }
        return result;
    }
}
