package com.example.administrator.sanlaiuispeech.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.adapter.RoomRobotDetailAdapter;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDao;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDto;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Enter3LQuestionFragment extends Fragment {
    private ImageView imageView;
    private GridView gridView;
    private RoomRobotDetailAdapter roomRobotDetailAdapter;
    private List<AIUIQaDto> mList=new ArrayList<>();
    public Enter3LQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final AIUIQaDao qaDao=new AIUIQaDao(getActivity());
        View view = inflater.inflate(R.layout.fragment_wsanl, container, false);
        gridView = (GridView) view.findViewById(R.id.health_center_gv);
        imageView= (ImageView) view.findViewById(R.id.refresh_iv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList= qaDao.getRandomList();
                roomRobotDetailAdapter.setAdapter(mList);
            }
        });
        mList= qaDao.getRandomList();
        roomRobotDetailAdapter = new RoomRobotDetailAdapter(getActivity(),mList);
        gridView.setAdapter(roomRobotDetailAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AIUIHelper.getInstance().sendText(mList.get(position).getQues());
            }
        });
        return view;
    }

}
