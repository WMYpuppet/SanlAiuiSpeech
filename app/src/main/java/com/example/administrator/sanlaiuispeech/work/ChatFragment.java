package com.example.administrator.sanlaiuispeech.work;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.util.VerticalMarqueeTextView;
import com.example.administrator.sanlaiuispeech.work.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/7/5
 * @emial 732603278@qq.com
 * @function
 */
public class ChatFragment extends BaseFragment {

    @BindView(R.id.vmt_robot_text)
    VerticalMarqueeTextView mTvRobotText;
    @BindView(R.id.tv_robot_text)
    VerticalMarqueeTextView mVmtRobotText;
    @BindView(R.id.ll_dialog_line)
    LinearLayout mLlDialogLine;

    public ChatFragment() {
        // Required empty public constructor
    }

    public interface UIListener {
        void stopSpeakUI();
    }

    private UIListener mListener;

    public void setUIListener(UIListener listener) {
        this.mListener = listener;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_chat;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void lazyLoad() {

    }

    /**
     * 显示闲聊文字
     */
    public void setChatText(String answer) {

        mLlDialogLine.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(answer)) {
            //文字排版
            answer = toFormatted(answer);
            //闲聊回答15个字数以内，以及背古诗、算术、唱歌时，文字居中显示
            if ((answer.length() <= 15) && answer.length() > 0) {
                mVmtRobotText.setVisibility(View.VISIBLE);
                mTvRobotText.setVisibility(View.GONE);
                setRobotText(mVmtRobotText.getTextView(), answer);
            } else {
                mVmtRobotText.setVisibility(View.GONE);
                mTvRobotText.setVisibility(View.VISIBLE);
                setRobotText(mTvRobotText.getTextView(), answer);
            }
        }
    }

    /**
     * 设置文本，不处理空文本
     */
    private void setRobotText(TextView textView, String answer) {
        if (textView == null || TextUtils.isEmpty(answer)) {
            return;
        }
        textView.setText(answer);
        //设置行间距与字间距
        textView.setLineSpacing(0.5f, 1f);
        //设置手动点击停止说话
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.stopSpeakUI();
                }
            }
        });
    }


    /**
     * 解决文本排列长短不一的问题
     *
     * @param input
     * @return
     */
    private String toFormatted(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusClick(EventMessage message) {
        switch (message.msgType) {
            case Constant.SEMANTIC_CODE.CHAT_UI_CHANGE:
                // mTvRobotText.setText("你多大了？");
                String answer = filterSpeciaStr(message.object.toString());
                setChatText(answer);
                AIUIHelper.getInstance().speak(answer);
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE, "闲聊对话"));
                // Vocality.getvocality().combining(message.object.toString(), "jiajia", mTts, mSynListener, getActivity());
                break;
        }
    }

    public static String filterSpeciaStr(String args) {
        Pattern p = Pattern.compile("\\[[A-Za-z][0-9]\\]");
        Matcher m = p.matcher(args);
        return m.replaceAll("").trim();
    }
}
