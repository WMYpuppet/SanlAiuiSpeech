package com.example.administrator.sanlaiuispeech;

import android.content.Context;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 作者：Created by Administrator on 2019/5/23.
 * 邮箱：
 */
public class Vocality {

    private static Vocality vocality;

    public static Vocality getvocality() {
        if (vocality == null) {
            vocality = new Vocality();
        }
        return vocality;
    }

    public void stopSpeak(SpeechSynthesizer mTts){
        mTts.stopSpeaking();
    }

    public void combining(String nvarchar, String playman, SpeechSynthesizer mTts, SynthesizerListener mSynListener, Context context) {
        mTts.setParameter(SpeechConstant.VOICE_NAME, playman);

        mTts.setParameter(SpeechConstant.SPEED, "40");//设置语速
        mTts.setParameter(SpeechConstant.PITCH, "50"); //设置合成音调
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        mTts.stopSpeaking();
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");
//        Toast.makeText(MainActivity.this, "语音合成 保存音频到本地：\n" + isSuccess, Toast.LENGTH_LONG).show();
        //3.开始合成
        int code = mTts.startSpeaking(nvarchar, mSynListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //上面的语音配置对象为初始化时：
                Toast.makeText(context, "语音组件未安装", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "语音合成失败,错误码: " + code, Toast.LENGTH_LONG).show();
            }
        }

    }
}
