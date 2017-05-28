package com.phoenix.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoenix.readily.R;
import com.phoenix.readily.adapter.base.SimpleBaseAdapter;
import com.phoenix.readily.business.AccountBookBusiness;
import com.phoenix.readily.entity.AccountBook;

import java.util.List;

import static com.phoenix.readily.R.id.account_book_item_total_tv;

/**
 * Created by flashing on 2017/5/12.
 */

public class AccountBookSelectAdapter extends SimpleBaseAdapter {
    private AccountBookBusiness accountBookBusiness;

    public AccountBookSelectAdapter(Context context) {
        super(context, null);
        accountBookBusiness = new AccountBookBusiness(context);
        setListFromBusiness();
    }

    private void setListFromBusiness() {
        List<AccountBook> list = accountBookBusiness.getNotHideAccountBook();
        setList(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.account_book_select_list_item, null);
            holder = new Holder();
            holder.account_book_item_icon_iv = (ImageView) convertView.findViewById(R.id.account_book_item_icon_iv);
            holder.account_book_item_name_tv = (TextView) convertView.findViewById(R.id.account_book_item_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        AccountBook accountBook = (AccountBook) datas.get(position);
        if (accountBook.getIsDefault() == 1) {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_default);
        } else {
            holder.account_book_item_icon_iv.setImageResource(R.drawable.account_book_icon);
        }
        holder.account_book_item_name_tv.setText(accountBook.getAccountBookName());
        return convertView;
    }

    private class Holder{
        ImageView account_book_item_icon_iv;
        TextView account_book_item_name_tv;
    }
}
