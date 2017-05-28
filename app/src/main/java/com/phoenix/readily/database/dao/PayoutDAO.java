package com.phoenix.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.phoenix.readily.R;
import com.phoenix.readily.database.base.SQLiteDAOBase;
import com.phoenix.readily.entity.Payout;
import com.phoenix.readily.utils.DateUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by flashing on 2017/5/15.
 */

public class PayoutDAO extends SQLiteDAOBase {
    public PayoutDAO(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNameAndPK() {
        return new String[]{"payout", "payoutId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Payout payout = new Payout();
        payout.setPayoutId(cursor.getInt(cursor.getColumnIndex(
                "payoutId")));
        payout.setAccountBookId(cursor.getInt(cursor.getColumnIndex(
                "accountBookId")));
        payout.setAccountBookName(cursor.getString(cursor.getColumnIndex(
                "accountBookName")));
        payout.setCategoryId(cursor.getInt(cursor.getColumnIndex(
                "categoryId")));
        payout.setCategoryName(cursor.getString(cursor.getColumnIndex(
                "categoryName")));
        payout.setPath(cursor.getString(cursor.getColumnIndex(
                "path")));
        payout.setAmount(new BigDecimal(cursor.getString(
                cursor.getColumnIndex("amount"))));
        Date payoutDate = DateUtil.getDate(cursor.getString(
                cursor.getColumnIndex("payoutDate")), "yyyy-MM-dd HH:mm:ss");
        payout.setPayoutDate(payoutDate);
        payout.setPayoutType(cursor.getString(cursor.getColumnIndex(
                "payoutType")));
        payout.setPayoutUserId(cursor.getString(cursor.getColumnIndex(
                "payoutUserId")));
        payout.setComment(cursor.getString(cursor.getColumnIndex(
                "comment")));
        Date createDate = DateUtil.getDate(cursor.getString(
                cursor.getColumnIndex("createDate")), "yyyy-MM-dd HH:mm:ss");
        payout.setCreateDate(createDate);
        payout.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return payout;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [payout](");
        sql.append("[payoutId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[accountBookId] int NOT NULL");
        sql.append(",[categoryId] int NOT NULL");
        sql.append(",[amount] decimal NOT NULL");
        sql.append(",[payoutDate] datetime NOT NULL");
        sql.append(",[payoutType] varchar(20) NOT NULL");
        sql.append(",[payoutUserId] text NOT NULL");//1,2,3
        sql.append(",[comment] text NOT NULL");
        sql.append(",[createDate] datetime NOT NULL");
        sql.append(",[state] int NOT NULL");

        sql.append(")");
        database.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {
    }

    public boolean insertPayout(Payout payout){
        ContentValues contentValues = createParms(payout);
        long newid = getDatabase().insert(getTableNameAndPK()[0], null, contentValues);
        payout.setPayoutId((int) newid);
        return newid > 0;
    }

    public boolean deletePayout(String condition){
        return delete(getTableNameAndPK()[0], condition);
    }

    public boolean updatePayout(String condition, Payout payout){
        ContentValues contentValues = createParms(payout);
        return updatePayout(condition, contentValues);
    }

    public boolean updatePayout(String condition, ContentValues contentValues){
        return getDatabase().update(getTableNameAndPK()[0], contentValues,
                condition, null) >= 0;
    }

    public List<Payout> getPayouts(String condition){
            String sql = "select * from v_payout where 1=1 "+ condition;
            return getList(sql);
    }

    private ContentValues createParms(Payout info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountBookId", info.getAccountBookId());
        contentValues.put("categoryId", info.getCategoryId());
        contentValues.put("amount", info.getAmount().toString());
        contentValues.put("payoutDate", DateUtil.getFormatDateTime(
                info.getPayoutDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("payoutType", info.getPayoutType());
        contentValues.put("payoutUserId", info.getPayoutUserId());
        contentValues.put("comment", info.getComment());
        contentValues.put("createDate", DateUtil.getFormatDateTime(
                info.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        return contentValues;
    }
}
