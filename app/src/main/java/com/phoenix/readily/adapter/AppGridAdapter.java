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

/**
 * Created by flashing on 2017/5/12.
 */

public class AppGridAdapter extends BaseAdapter {
    private Context context;
    private Integer[] imgInteger = {
            R.drawable.grid_payout,
            R.drawable.grid_bill,
            R.drawable.grid_report,
            R.drawable.grid_account_book,
            R.drawable.grid_category,
            R.drawable.grid_user
    };
    private String[] imgString = new String[6];

    public AppGridAdapter(Context context) {
        this.context = context;
        imgString[0] = context.getString(R.string.grid_payout_add);//记录消费
        imgString[1] = context.getString(R.string.grid_payout_manage);//查询消费
        imgString[2] = context.getString(R.string.grid_account_manage);//账本管理
        imgString[3] = context.getString(R.string.grid_statistics_manage);//统计管理
        imgString[4] = context.getString(R.string.grid_category_manage);//类别管理
        imgString[5] = context.getString(R.string.grid_user_manage);//人员管理
    }

    @Override
    public int getCount() {
        return imgString.length;
    }

    @Override
    public Object getItem(int position) {
        return imgString[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.main_body_item, null);
            holder = new Holder();
            holder.icon_iv = (ImageView) convertView.findViewById(R.id.main_body_item_icon_iv);
            holder.name_tv = (TextView) convertView.findViewById(R.id.main_body_item_name_tv);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        holder.icon_iv.setImageResource(imgInteger[position]);
        //动态设置图片的宽高
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
        holder.icon_iv.setLayoutParams(layoutParams);
        //动态设置图片的比例
        holder.icon_iv.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.name_tv.setText(imgString[position]);
        return convertView;
    }

    private class Holder{
        ImageView icon_iv;
        TextView name_tv;
    }
}
