package com.example.administrator.sanlaiuispeech.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.btn_chat).setOnClickListener(this);
        view.findViewById(R.id.btn_receive).setOnClickListener(this);
        view.findViewById(R.id.btn_walk_into).setOnClickListener(this);
        view.findViewById(R.id.btn_lead).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        MainActivity mainActivity=(MainActivity)getActivity();
        switch (view.getId()) {
            case R.id.btn_chat:
                AIUIHelper.getInstance().sendText("闲聊");
                break;
            case R.id.btn_walk_into:
                AIUIHelper.getInstance().sendText("走进三联");

                break;
            case R.id.btn_receive:
                mainActivity.replaceFragment(MainActivity.FRAGMENT_SCHEME_lIST);
               // AIUIHelper.getInstance().sendText("接待引导");
                break;
            case R.id.btn_lead:
                break;
        }
    }
}
