package com.example.administrator.sanlaiuispeech.ability;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.SpeechApp;
import com.example.administrator.sanlaiuispeech.Vocality;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDto;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.work.Constant;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/10
 * @emial 732603278@qq.com
 * @function
 */
public class AIUIHelper {

    //TAG
    public static final String TAG = "AIUIHelper";

    private static AIUIHelper sInstance = null;

    /**
     * AIUI语义解析接口
     */
    public interface ParseListener {

        void onAiuiResponse(AIUIEvent event);

        void onAiuiWakeUp();

        void onAiuiSleep();

        void onError(int errorCode);
    }

    public ParseListener mParseListener;

    private boolean mIsStarted = false;

    private AIUIAgent mAIUIAgent = null;
    //交互状态
    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private SpeechSynthesizer mTts;
    //合成
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;


    public static AIUIHelper getInstance() {
        if (null == sInstance) {
            sInstance = new AIUIHelper();
        }
        return sInstance;
    }

    private AIUIHelper() {
        if (null == mAIUIAgent) {
            //创建AIUIAgent
            mAIUIAgent = AIUIAgent.createAgent(SpeechApp.getInstance().getApplicationContext(), getAIUIParams(), mAIUIListener);

        }
        if (null == mAIUIAgent) {
            final String strErrorTip = "创建 AIUI Agent 失败！";
            //   showTip(strErrorTip);
            //  this.mNlpText.setText(strErrorTip);
        }
        //得到SpeechSynthesizer操作类进行设置
        mTts = SpeechSynthesizer.createSynthesizer(SpeechApp.getInstance().getApplicationContext(), null);
        // 云端发音人名称列表
        mCloudVoicersEntries = SpeechApp.getInstance().getApplicationContext().getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = SpeechApp.getInstance().getApplicationContext().getResources().getStringArray(R.array.voicer_cloud_values);
    }

    //开始录音
    public void startVoiceNlp() {
        Log.i(TAG, "start voice nlp");
        //mNlpText.setText("");
        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        // 默认为oneshot 模式，即一次唤醒后就进入休眠，如果语音唤醒后，需要进行文本语义，请将改段逻辑copy至startTextNlp()开头处
        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }
        // 打开AIUI内部录音机，开始录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(writeMsg);
    }

    //停止录音
    private void stopVoiceNlp() {
        if (null == mAIUIAgent) {
            // showTip("AIUIAgent 为空，请先创建");
            return;
        }
        Log.i(TAG, "stop voice nlp");
        // 停止录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage stopRecord = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(stopRecord);
    }

    /**
     * 开启语音识别
     */
    public void start() {
        if (!mIsStarted) {
            //  mAdapter.start();
            mIsStarted = true;
        }
    }

    public void stop() {
        if (mIsStarted) {
            // mAdapter.stop();
            mIsStarted = false;
        }
    }

    public void speakPaused() {
        mTts.pauseSpeaking();
    }

    public void speakResumed() {
        mTts.resumeSpeaking();
    }

    public void stopSpeak() {
        Vocality.getvocality().stopSpeak(mTts);
    }

    public void speak(String bean) {
        Vocality.getvocality().combining(bean, "xiaoyan", mTts, mSynListener, SpeechApp.getInstance().getApplicationContext());
    }

    //发送文本到AIUI
    public void sendText(String text) {
        String params = "data_type=text,debug=true";
        try {
            byte[] data = text.getBytes("utf-8");
            AIUIMessage msg = new AIUIMessage(
                    AIUIConstant.CMD_WRITE, 0, 0, params, data);
            mAIUIAgent.sendMessage(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //科大讯飞监听合成状态
    SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            //   showTip("开始播放");
            stopVoiceNlp();
            EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE, "PLAYING"));
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {
            //   showTip("暂停播放");

        }

        @Override
        public void onSpeakResumed() {
            //showTip("继续播放");

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                //   showTip("播放完成");
                startVoiceNlp();
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE, "PLAYED"));
            } else if (error != null) {
                //Toast.makeText(getApplication(), error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
                //  showTip(error.getPlainDescription(true));
                //合成出错
            }

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    //AIUI事件监听器
    private AIUIListener mAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //唤醒事件
                    Log.d(TAG, "on event: " + event.eventType);
                    break;
                case AIUIConstant.EVENT_RESULT: {
                    //结果事件
                    Log.d(TAG, "on event: " + event.eventType);
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);
                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));
                            String sub = params.optString("sub");
                            JSONObject result = cntJson.optJSONObject("intent");
                            if ("nlp".equals(sub) && result.length() > 2) {
                                // 解析得到语义结果
                                String str = "";
                                String ques = "";
                                //在线语义结果
                                Log.d("forest", "aiui result:" + result.toString());
                                if (result.optInt("rc") == 0) {
                                    JSONObject answer = result.optJSONObject("answer");
                                    JSONObject question = answer.optJSONObject("question");
                                    if (question != null) {
                                        //自定义、兜底问题解析
                                        ques = question.optString("question");
                                    } else {
                                        //商店技能问题解析
                                        ques = result.optString("text");
                                    }
                                    //回答
                                    if (answer != null) {
                                        str = answer.optString("text");
                                    }
                                } else {
                                    str = "你在说什么";
                                }
                                // mNlpText.setText(str);
                                AIUIQaDto qaDto = new AIUIQaDto();
                                qaDto.setQues(ques);
                                qaDto.setAnswer(str);
                                EventBus.getDefault().post(
                                        new EventMessage(Constant.SEMANTIC_CODE.AIUI_RESOURCE, qaDto));
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;
                case AIUIConstant.EVENT_ERROR: {
                    //错误事件
                    Log.i(TAG, "on event: " + event.eventType);
                    //  mNlpText.append("\n");
                    // mNlpText.append("错误: " + event.arg1 + "\n" + event.info);
                }
                break;

                case AIUIConstant.EVENT_VAD: {
                    //vad事件
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        //找到语音前端点
                        //    showTip("找到vad_bos");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //找到语音后端点
                        //  showTip("找到vad_eos");
                    } else {
                        //     showTip("" + event.arg2);
                    }
                }
                break;

                case AIUIConstant.EVENT_START_RECORD: {
                    //开始录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                    //   showTip("开始录音");
                }
                break;

                case AIUIConstant.EVENT_STOP_RECORD: {
                    //停止录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                    //  showTip("停止录音");
                }
                break;

                case AIUIConstant.EVENT_STATE: {    // 状态事件
                    mAIUIState = event.arg1;
                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                        //  showTip("STATE_IDLE");
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                        //showTip("STATE_READY");
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                        //   showTip("STATE_WORKING");
                    }
                }
                break;

                default:
                    break;
            }
        }

    };

    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = SpeechApp.getInstance().getApplicationContext().getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }


    public void release() {
        // 退出时释放连接
        if (null != this.mAIUIAgent) {
            AIUIMessage stopMsg = new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null);
            mAIUIAgent.sendMessage(stopMsg);
            this.mAIUIAgent.destroy();
            this.mAIUIAgent = null;
        }
        if (null != mTts) {
            mTts.stopSpeaking();
            mTts.destroy();
        }
    }
}
