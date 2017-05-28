package com.phoenix.readily.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.phoenix.readily.database.base.SQLiteHelper;

/**
 * Created by flashing on 2017/5/25.
 */

public class CreateViewDAO implements SQLiteHelper.SQLiteDateTable {
    private Context context;

    public CreateViewDAO(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append(" Create VIEW v_payout as ");
        sql.append(" select p.*, c.parentId, c.categoryName, c.path, a.accountBookName ");
        sql.append(" from payout p left join category c on p.categoryId=c.categoryId ");
        sql.append(" left join accountBook a on p.accountBookId=a.accountBookId ");
        database.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {

    }
}
