package com.example.administrator.sanlaiuispeech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.bean.InfoReception;

import java.util.List;

/**
 * 作者：Created by Administrator on 2019/7/16.
 * 邮箱：
 */
public class ReceptionAdapter extends BaseAdapter {
    Context context;
    List<InfoReception> list;

    public ReceptionAdapter(Context context, List<InfoReception> list) {
        this.context = context;
        this.list = list;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.item_grid_reception, null);
        TextView tvExhibition = (TextView) convertView.findViewById(R.id.tv_exhibition);
        TextView tvCode = (TextView) convertView.findViewById(R.id.tv_Code);
        InfoReception reception = list.get(position);

        tvExhibition.setText(reception.getTvExhibition1());
        tvCode.setText(reception.getTvCode());
        return convertView;
    }
}
