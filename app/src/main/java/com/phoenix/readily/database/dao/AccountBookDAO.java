package com.phoenix.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.phoenix.readily.R;
import com.phoenix.readily.database.base.SQLiteDAOBase;
import com.phoenix.readily.entity.AccountBook;
import com.phoenix.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by flashing on 2017/5/15.
 */

public class AccountBookDAO extends SQLiteDAOBase {
    public AccountBookDAO(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNameAndPK() {
        return new String[]{"accountBook", "accountBookId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        AccountBook accountBook = new AccountBook();
        accountBook.setAccountBookId(cursor.getInt(cursor.getColumnIndex("accountBookId")));
        accountBook.setAccountBookName(cursor.getString(cursor.getColumnIndex("accountBookName")));
        Date createDate = DateUtil.getDate(cursor.getString(
                cursor.getColumnIndex("createDate")), "yyyy-MM-dd HH:mm:ss");
        accountBook.setCreateDate(createDate);
        accountBook.setState(cursor.getInt(cursor.getColumnIndex("state")));
        accountBook.setIsDefault(cursor.getInt(cursor.getColumnIndex("isDefault")));
        return accountBook;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [accountBook](");
        sql.append("[accountBookId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[accountBookName] varchar(20) NOT NULL");
        sql.append(",[createDate] datetime NOT NULL");
        sql.append(",[state] int NOT NULL");
        sql.append(",[isDefault] int NOT NULL");

        sql.append(")");
        database.execSQL(sql.toString());

        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {
    }

    public boolean insertAccountBook(AccountBook accountBook){
        ContentValues contentValues = createParms(accountBook);
        long newid = getDatabase().insert(getTableNameAndPK()[0], null, contentValues);
        accountBook.setAccountBookId((int) newid);
        return newid > 0;
    }

    public boolean deleteAccountBook(String condition){
        return delete(getTableNameAndPK()[0], condition);
    }

    public boolean updateAccountBook(String condition, AccountBook accountBook){
        ContentValues contentValues = createParms(accountBook);
        return updateAccountBook(condition, contentValues);
    }

    public boolean updateAccountBook(String condition, ContentValues contentValues){
        return getDatabase().update(getTableNameAndPK()[0], contentValues,
                condition, null) >= 0;
    }

    public List<AccountBook> getAccountBook(String condition){
            String sql = "select * from "+getTableNameAndPK()[0]+" where 1=1 "+ condition;
            return getList(sql);
    }

    private ContentValues createParms(AccountBook info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountBookName", info.getAccountBookName());
        contentValues.put("createDate", DateUtil.getFormatDateTime(
                info.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        contentValues.put("isDefault", info.getIsDefault());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database){
        AccountBook accountBook = new AccountBook();
        String[] accountBookname = getContext().getResources().getStringArray(
                R.array.InitDefaultAccountBookName);
//        for (int i = 0; i < accountBookname.length; i++) {
            accountBook.setAccountBookName(accountBookname[0]);
            accountBook.setIsDefault(1);
            ContentValues contentValues = createParms(accountBook);
            database.insert(getTableNameAndPK()[0], null, contentValues);
//        }
    }
}
