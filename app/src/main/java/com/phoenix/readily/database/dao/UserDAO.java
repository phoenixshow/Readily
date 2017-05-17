package com.phoenix.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.phoenix.readily.R;
import com.phoenix.readily.database.base.SQLiteDAOBase;
import com.phoenix.readily.entity.Users;
import com.phoenix.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by flashing on 2017/5/15.
 */

public class UserDAO extends SQLiteDAOBase {
    public UserDAO(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNameAndPK() {
        return new String[]{"users", "userId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Users user = new Users();
        user.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
        user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
        Date createDate = DateUtil.getDate(cursor.getString(
                cursor.getColumnIndex("createDate")), "yyyy-MM-dd HH:mm:ss");
        user.setCreateDate(createDate);
        user.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return user;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [users](");
        sql.append("[userId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[userName] varchar(20) NOT NULL");
        sql.append(",[createDate] datetime NOT NULL");
        sql.append(",[state] int NOT NULL");

        sql.append(")");
        database.execSQL(sql.toString());

        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {
    }

    public boolean insertUser(Users users){
        ContentValues contentValues = createParms(users);
        long newid = getDatabase().insert(getTableNameAndPK()[0], null, contentValues);
        return newid > 0;
    }

    public boolean deleteUser(String condition){
        return delete(getTableNameAndPK()[0], condition);
    }

    public boolean updateUser(String condition, Users user){
        ContentValues contentValues = createParms(user);
        return getDatabase().update(getTableNameAndPK()[0], contentValues,
                condition, null) >= 0;
    }

    public List<Users> getUsers(String condition){
            String sql = "select * from "+getTableNameAndPK()[0]+" where 1=1 "+ condition;
            return getList(sql);
    }

    private ContentValues createParms(Users info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", info.getUserName());
        contentValues.put("createDate", DateUtil.getFormatDateTime(
                info.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database){
        Users user = new Users();
        String[] username = getContext().getResources().getStringArray(
                R.array.InitDefaultUserName);
        for (int i = 0; i < username.length; i++) {
            user.setUserName(username[i]);
            ContentValues contentValues = createParms(user);
            database.insert(getTableNameAndPK()[0], null, contentValues);
        }
    }
}
