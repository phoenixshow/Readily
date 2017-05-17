package com.phoenix.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoenix.readily.R;
import com.phoenix.readily.adapter.base.SimpleBaseAdapter;
import com.phoenix.readily.business.UserBusiness;
import com.phoenix.readily.entity.Users;
import com.phoenix.readily.view.SlideMenuItem;

import java.util.List;

/**
 * Created by flashing on 2017/5/12.
 */

public class UserAdapter extends SimpleBaseAdapter {
    private UserBusiness userBusiness;

    public UserAdapter(Context context) {
        super(context, null);
        userBusiness = new UserBusiness(context);
        setListFromBusiness();
    }

    private void setListFromBusiness() {
        List<Users> list = userBusiness.getNotHideUser();
        setList(list);
    }

    public void updateList(){
        setListFromBusiness();
        updateDisplay();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.user_list_item, null);
            holder = new Holder();
            holder.user_item_icon_iv = (ImageView) convertView.findViewById(R.id.user_item_icon_iv);
            holder.user_item_name_tv = (TextView) convertView.findViewById(R.id.user_item_name_tv);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        Users user = (Users) datas.get(position);
        holder.user_item_icon_iv.setImageResource(R.drawable.grid_user);
        holder.user_item_name_tv.setText(user.getUserName());
        return convertView;
    }

    private class Holder{
        ImageView user_item_icon_iv;
        TextView user_item_name_tv;
    }
}
