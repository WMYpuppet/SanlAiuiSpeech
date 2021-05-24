package com.example.administrator.sanlaiuispeech.data;

import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/10
 * @emial 732603278@qq.com
 * @function
 */
@Entity(table = "T_AIUIQA_DATA")
public class AIUIQaDto {
    @Column
    private String ques;
    @Column
    private String answer;

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getAnswer() {
        return answer;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
