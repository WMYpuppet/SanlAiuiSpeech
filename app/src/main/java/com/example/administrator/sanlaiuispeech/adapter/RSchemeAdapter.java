package com.example.administrator.sanlaiuispeech.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.activity.MainActivity;
import com.example.administrator.sanlaiuispeech.bean.InfoRScheme;

import java.util.List;

/**
 * 作者：Created by Administrator on 2019/6/25.
 * 邮箱：
 */
public class RSchemeAdapter extends ArrayAdapter {
    private int resourceIdSet;

    public RSchemeAdapter(Context context, int resource, List<InfoRScheme> objects) {
        super(context, resource, objects);
        resourceIdSet = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InfoRScheme infoRScheme = (InfoRScheme) getItem(position);
//        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceIdSet, null);
            viewHolder = new ViewHolder();
            viewHolder.tvSchemeID = (TextView) convertView.findViewById(R.id.tv_schemeid);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.btnExecute = (Button) convertView.findViewById(R.id.btn_execute);
            convertView.setTag(viewHolder);
        } else {
//            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvSchemeID.setText(infoRScheme.getSchemeID());
        viewHolder.tvTime.setText(infoRScheme.getTime());
        viewHolder.btnExecute.setTag(position);
        viewHolder.btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getContext();
                mainActivity.replaceFragment(MainActivity.FRAGMENT_SCHEME_POINTS);
            }
        });

        return convertView;
    }


    class ViewHolder {
        TextView tvSchemeID;
        TextView tvTime;
        Button btnExecute;
    }
}
