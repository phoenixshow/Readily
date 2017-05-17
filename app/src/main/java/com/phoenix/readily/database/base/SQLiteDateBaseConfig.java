package com.phoenix.readily.database.base;

import android.content.Context;

import com.phoenix.readily.R;

import java.util.ArrayList;

/**
 * Created by flashing on 2017/5/15.
 */

public class SQLiteDateBaseConfig {
    private static final String DATABASE_NAME = "readily.db";//数据库名
    private static final int VERSION = 1;//数据库版本

    private static SQLiteDateBaseConfig INSTANCE;
    private static Context CONTEXT;

    private SQLiteDateBaseConfig(){
    }

    public static SQLiteDateBaseConfig getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new SQLiteDateBaseConfig();
            CONTEXT = context;
        }
        return INSTANCE;
    }

    //返回数据库名称
    public String getDatabaseName(){
        return DATABASE_NAME;
    }

    //返回数据库版本
    public int getVersion(){
        return VERSION;
    }

    public ArrayList<String> getTables(){
        ArrayList<String> list = new ArrayList<>();
        String[] sqliteDAOClassName = CONTEXT.getResources().getStringArray(
                R.array.SQLiteDAOClassName);
        String packagePath = CONTEXT.getPackageName() + ".database.dao.";
        for (int i = 0; i < sqliteDAOClassName.length; i++) {
            //com.phoenix.readily.database.dao.UserDAO
            list.add(packagePath + sqliteDAOClassName[i]);
        }
        return list;
    }
}
