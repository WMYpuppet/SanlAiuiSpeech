package com.example.administrator.sanlaiuispeech.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.activity.MainActivity;
import com.example.administrator.sanlaiuispeech.adapter.ReceptionAdapter;
import com.example.administrator.sanlaiuispeech.bean.InfoReception;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.work.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceptionFragment extends Fragment {
    View view;
    List<InfoReception> receptionList = new ArrayList<InfoReception>();
    GridView gridView;

    public ReceptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rshow, container, false);
        init();
        ReceptionAdapter adapter = new ReceptionAdapter(getContext(), receptionList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.exhibitions=position;
                EventBus.getDefault().postSticky(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, receptionList.get(position).getTvCode()));
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragment(MainActivity.FRAGMENT_RECEPTION_DETAIL);
            }
        });

        return view;
    }

    public void init() {
        gridView = (GridView) view.findViewById(R.id.grid_reception);
        InfoReception infoReception1 = new InfoReception("1号展台", Constant.exhibition1);
        InfoReception infoReception2 = new InfoReception("2号展台", Constant.exhibition2);
        InfoReception infoReception3 = new InfoReception("3号展台", Constant.exhibition3);
        InfoReception infoReception4 = new InfoReception("4号展台", Constant.exhibition4);
        InfoReception infoReception5 = new InfoReception("5号展台", Constant.exhibition5);
        receptionList.add(infoReception1);
        receptionList.add(infoReception2);
        receptionList.add(infoReception3);
        receptionList.add(infoReception4);
        receptionList.add(infoReception5);

        int size = receptionList.size();
        int length = 150;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density+120);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(40); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }


}
