package com.phoenix.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phoenix.readily.R;
import com.phoenix.readily.adapter.base.SimpleBaseAdapter;
import com.phoenix.readily.view.SlideMenuItem;

import java.util.List;

/**
 * Created by flashing on 2017/5/12.
 */

public class SlideMenuAdapter extends SimpleBaseAdapter {

    public SlideMenuAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.slide_menu_list_item, null);
            holder = new Holder();
            holder.menu_name_tv = (TextView) convertView.findViewById(R.id.slide_menu_list_item_tv);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        SlideMenuItem item = (SlideMenuItem) datas.get(position);
        holder.menu_name_tv.setText(item.getTitle());
        return convertView;
    }

    private class Holder{
        TextView menu_name_tv;
    }
}
