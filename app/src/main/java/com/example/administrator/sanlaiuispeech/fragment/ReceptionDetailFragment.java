package com.example.administrator.sanlaiuispeech.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.work.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceptionDetailFragment extends Fragment {

    View view;
    TextView tvReceptionDetail;

    public ReceptionDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reception_detail, container, false);
        tvReceptionDetail = (TextView) view.findViewById(R.id.tv_reception_detail);
        EventBus.getDefault().register(this);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEventBusClick(EventMessage message) {
        Log.d("forest", "onEventBusClick:" + message.getMsgType());
        switch (message.msgType) {
            case Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE:
                String strDetail = message.object.toString();
                tvReceptionDetail.setText(strDetail);
                AIUIHelper.getInstance().speak(strDetail);
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
