package com.phoenix.readily.business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.phoenix.readily.R;
import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.dao.PayoutDAO;
import com.phoenix.readily.entity.Payout;

import java.util.List;

/**
 * Created by flashing on 2017/5/25.
 */

public class PayoutBusiness extends BaseBusiness {
    private PayoutDAO payoutDAO;

    public PayoutBusiness(Context context) {
        super(context);
        payoutDAO = new PayoutDAO(context);
    }

    public boolean insertPayout(Payout payout){
        return payoutDAO.insertPayout(payout);
    }

    public boolean updatePayout(Payout payout){
        String condition = " payoutId="+payout.getPayoutId();
        boolean result = payoutDAO.updatePayout(condition, payout);
        return result;
    }

    public String getPayoutTotalMessage(String payoutDate,
                                        int accountBookId) {
        String condition = " and payoutDate='"+payoutDate+"' " +
                "and accountBookId="+accountBookId+" " +
                "and state=1";
        //SQLite自带函数
        //  sum(amount)是合计金额，count(amount)是统计数量多少笔
        String sql = " select ifnull(sum(amount),0) as sumAmount, " +
                "count(amount) as count from payout where 1=1 " +
                condition;
        String[] total = new String[2];
        Cursor cursor = payoutDAO.execSql(sql);
        if (cursor.getCount() == 1){
            while (cursor.moveToNext()){
                //共？笔
                total[0] = cursor.getString(
                        cursor.getColumnIndex("count"));
                //合计消费？元
                total[1] = cursor.getString(
                        cursor.getColumnIndex("sumAmount"));
            }
        }

        return context.getString(R.string.textview_text_payout_total,
                new Object[]{total[0], total[1]});
    }

    //根据账本ID查询消费记录
    public List<Payout> getPayoutListByAccountBookId(int accountBookId){
        String condition = " and accountBookId="+accountBookId+" and state=1 order by payoutDate desc, payoutId desc";
        return payoutDAO.getPayouts(condition);
    }

    public boolean deletePayoutByPayoutId(int payoutId){
        String condition = " payoutId=" + payoutId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return payoutDAO.updatePayout(condition, contentValues);
    }

    public boolean deletePayoutByAccountBookId(int accountBookId){
        String condition = " accountBookId=" + accountBookId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return payoutDAO.updatePayout(condition, contentValues);
    }

    public List<Payout> getPayouOrderByPayoutUserId(String condition) {
        condition += " order by payoutUserId";
        List<Payout> list = payoutDAO.getPayouts(condition);
        if (list != null && list.size() > 0){
            return list;
        }
        return null;
    }
}
