package com.phoenix.readily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoenix.readily.R;
import com.phoenix.readily.adapter.base.SimpleBaseAdapter;
import com.phoenix.readily.business.PayoutBusiness;
import com.phoenix.readily.business.UserBusiness;
import com.phoenix.readily.entity.Payout;
import com.phoenix.readily.utils.DateUtil;

import java.util.List;

/**
 * Created by flashing on 2017/6/1.
 */

public class PayoutAdapter extends SimpleBaseAdapter {
    private PayoutBusiness payoutBusiness;
    private UserBusiness userBusiness;
    private int accountBookId;

    public PayoutAdapter(Context context, int accountBookId) {
        super(context, null);
        payoutBusiness = new PayoutBusiness(context);
        userBusiness = new UserBusiness(context);
        this.accountBookId = accountBookId;
        //按账本ID查询，未隐藏的消费记录，按消费日期、消费ID排序（倒序）
        //String condition="";
        //dao.getPayouts(condition);
        setListFromBusiness();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(
                    R.layout.payout_list_item, null);
            holder = new Holder();
            holder.payout_item_icon_iv = (ImageView) convertView.findViewById(R.id.payout_item_icon_iv);
            holder.payout_item_name_tv = (TextView) convertView.findViewById(R.id.payout_item_name_tv);
            holder.payout_item_amount_tv = (TextView) convertView.findViewById(R.id.payout_item_amount_tv);
            holder.payout_item_user_and_type_tv = (TextView) convertView.findViewById(R.id.payout_item_user_and_type_tv);
            holder.payout_item_date_rl = (View) convertView.findViewById(R.id.payout_item_date_rl);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        //隐藏日期分组条
        holder.payout_item_date_rl.setVisibility(View.GONE);
        Payout payout = (Payout) getItem(position);
        String payoutDate = DateUtil.getFormatDateTime(
                payout.getPayoutDate(), "yyyy-MM-dd");
        boolean isShow = false;//是否显示日期分组条，默认不显示
        if (position > 0){//说明肯定不是第一条
            //获取它上一个实体
            Payout payoutLast = (Payout) getItem(position-1);
            //获取上一个实体的日期
            String payoutDateLast = DateUtil.getFormatDateTime(
                    payoutLast.getPayoutDate(), "yyyy-MM-dd");
            //如果当前日期与上一个实体的日期不等，就显示
            isShow = !payoutDate.equals(payoutDateLast);
        }
        if (isShow || position == 0){
            holder.payout_item_date_rl.setVisibility(View.VISIBLE);
            //共?笔，合计消费?元
            String msg = payoutBusiness.getPayoutTotalMessage(
                    payoutDate+" 00:00:00", accountBookId);
            ((TextView)holder.payout_item_date_rl.findViewById(
                    R.id.payout_item_date_tv)).setText(payoutDate);
            ((TextView)holder.payout_item_date_rl.findViewById(
                    R.id.payout_item_total_tv)).setText(msg);
        }
        holder.payout_item_icon_iv.setImageResource(R.drawable.grid_payout);
        holder.payout_item_name_tv.setText(payout.getCategoryName());
        holder.payout_item_amount_tv.setText(context.getString(
                R.string.textview_text_payout_amount,
                new Object[]{payout.getAmount().toString()}));
        String userName = userBusiness.getUserNameByUserId(
                payout.getPayoutUserId());//1,2,-->王小强,小李,
        holder.payout_item_user_and_type_tv.setText(userName+" "+
                payout.getPayoutType());
        return convertView;
    }

    public void updateList(int accountBookId) {
        this.accountBookId = accountBookId;
        setListFromBusiness();
        updateDisplay();
    }

    private void setListFromBusiness(){
        //根据账本ID查询消费记录，注意排序
        List<Payout> list = payoutBusiness.getPayoutListByAccountBookId(accountBookId);
        setList(list);
    }

    private class Holder{
        ImageView payout_item_icon_iv;
        TextView payout_item_name_tv;
        TextView payout_item_amount_tv;
        TextView payout_item_user_and_type_tv;
        View payout_item_date_rl;
    }
}
