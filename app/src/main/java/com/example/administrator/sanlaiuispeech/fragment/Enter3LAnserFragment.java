package com.example.administrator.sanlaiuispeech.fragment;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDto;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.util.VerticalMarqueeTextView;
import com.example.administrator.sanlaiuispeech.work.Constant;
import com.example.administrator.sanlaiuispeech.work.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Enter3LAnserFragment extends BaseFragment {

    @BindView(R.id.vmt_robot_text_answer)
    VerticalMarqueeTextView mTvRobotText;
    @BindView(R.id.tv_robot_text_answer)
    VerticalMarqueeTextView mVmtRobotText;
    @BindView(R.id.ll_dialog_line_answer)
    LinearLayout mLlDialogLine;
    public Enter3LAnserFragment() {
        // Required empty public constructor
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_wshow;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void initViews() {
        Log.d("forest"," EventBus.getDefault(): register");
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
        if(!TextUtils.isEmpty(answer)) {
            //文字排版
            answer = toFormatted(answer);
            //闲聊回答15个字数以内，以及背古诗、算术、唱歌时，文字居中显示
            if ((answer.length() <= 15 ) && answer.length() > 0) {
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
         /*       if (null != mListener) {
                    mListener.stopSpeakUI();
                }*/
            }
        });
    }


    /**
     * 解决文本排列长短不一的问题
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
        Log.d("forest","onEventBusClick:"+message.getMsgType());
        switch (message.msgType) {
            case Constant.SEMANTIC_CODE.ANSWER_UI_CHANGE:

                // mTvRobotText.setText("你多大了？");
                AIUIQaDto qaBean= (AIUIQaDto)message.object;
                Log.d("forest","101:"+qaBean.getQues());
                setChatText(qaBean.getAnswer());
                AIUIHelper.getInstance().speak(qaBean.getAnswer());
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE,qaBean.getQues()));

                break;

        }
    }

}
