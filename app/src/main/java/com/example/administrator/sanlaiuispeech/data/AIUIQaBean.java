package com.example.administrator.sanlaiuispeech.data;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/10
 * @emial 732603278@qq.com
 * @function aiui问答返回数据bean
 */
public class AIUIQaBean {
    public AIUIQaBean(String ques, String answer) {
        super();
        this.ques = ques;
        this.answer = answer;
    }

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

    String ques;
    String answer;
}
