package com.example.administrator.sanlaiuispeech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDto;


import java.util.List;

/**
 * Created by Administrator on 2019/4/26.
 */
public class RoomRobotDetailAdapter extends BaseAdapter {
    private List<AIUIQaDto> objects;
    private Context context;
    private LayoutInflater layoutInflater;

    public RoomRobotDetailAdapter(Context context, List<AIUIQaDto> objects) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public void setAdapter(List<AIUIQaDto> list) {
        this.objects = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_grid, null);
            viewholder.tvBedName = (TextView) convertView.findViewById(R.id.tv_bed_name);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        try {
            viewholder.tvBedName.setText(objects.get(position).getQues());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    class ViewHolder {
        TextView tvBedName;
    }
}