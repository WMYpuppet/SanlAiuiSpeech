package com.example.administrator.sanlaiuispeech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.bean.InfoChat;

import java.util.List;

/**
 * 作者：Created by Administrator on 2019/5/31.
 * 邮箱：
 */
public class ChatAdapter extends BaseAdapter {
    Context context;
    List<InfoChat> list;
    LayoutInflater inflater;

    public List<InfoChat> getList() {
        return list;
    }

    public void setList(List<InfoChat> list) {
        this.list = list;
    }

    public ChatAdapter(Context context, List<InfoChat> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;

    @Override
    public int getItemViewType(int position) {
        int type = list.get(position).getType();
        switch (type) {
            case 0:
                return TYPE_LEFT;
            case 1:
                return TYPE_RIGHT;
        }
        return TYPE_LEFT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_LEFT:
                    convertView = inflater.inflate(R.layout.activity_left, null, false);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.textView4);
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_RIGHT:
                    convertView = inflater.inflate(R.layout.activity_right, null, false);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.textView3);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch (type) {
            case TYPE_LEFT:
                viewHolder.textView.setText(list.get(position).getContent());
                break;
            case TYPE_RIGHT:
                viewHolder.textView.setText(list.get(position).getContent());
                break;
        }
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
