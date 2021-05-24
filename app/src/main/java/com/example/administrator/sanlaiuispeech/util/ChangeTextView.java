package com.example.administrator.sanlaiuispeech.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.administrator.sanlaiuispeech.R;

import java.util.List;

/**
 * Created by admin on 2016/11/16.
 */
public class ChangeTextView extends RelativeLayout {

    public interface OnTextChangedListener {
        void onTextChange(int index);
    }

    private TextView mContainerView;
    //    private Spanned[] mSpanneds;
    private List<CharSequence> mContainers;
    private long[] mDelayTimes;
    private Handler mHandler;
    private int mIndex = 0;
    private Runnable mChangeRunnable;
    private OnTextChangedListener mOnTextChangedListener;

    public ChangeTextView(Context context) {
        this(context, null);
    }

    public ChangeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mChangeRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != mContainers && mContainers.size() > 0) {
                        mIndex++;
                        if (mIndex > mContainers.size() - 1) {
                            mIndex = 0;
                        }
                        if (null != mOnTextChangedListener) {
                            mOnTextChangedListener.onTextChange(mIndex);
                        }
                        if (mContainers.size() - 1 >= mIndex) {
                            mContainerView.setText(mContainers.get(mIndex));
                            mHandler.postDelayed(this, mDelayTimes[mIndex]);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        mContainerView = (TextView) LayoutInflater.from(context).inflate(R.layout.view_change_text, null);
        setGravity(Gravity.CENTER);
        addView(mContainerView);
    }

    public ChangeTextView setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.mOnTextChangedListener = onTextChangedListener;
        return this;
    }

    public void setText(String text) {
        mContainerView.setText(text);
    }

//    public ChangeTextView setTextList(Spanned ...spanneds) {
//        mSpanneds = spanneds;
//        mContainerView.setText(mSpanneds[0]);
//        return this;
//    }

    public ChangeTextView setTextList(List<CharSequence> strs) {
        mContainers = strs;
        if (null != mContainers && mContainers.size() > 0) {
            mContainerView.setText(mContainers.get(0));
        }
        return this;
    }

    public ChangeTextView setTextSize(int size) {
        mContainerView.setText(size);
        return this;
    }

    public ChangeTextView setChangeDelays(long[] times) {
        mDelayTimes = times;
        return this;
    }

    public void startBanner() {
        mHandler = new Handler();
        if (null != mContainers && mContainers.size() > 0) {
            mContainerView.setText(mContainers.get(0));

            if (null != mOnTextChangedListener) {
                mOnTextChangedListener.onTextChange(mIndex);
            }
            mHandler.postDelayed(mChangeRunnable, mDelayTimes[0]);
        }
    }

    public void stopBanner() {
        if (null != mHandler) {
            mHandler.removeCallbacks(mChangeRunnable);
            mHandler = null;
        }
    }
}
