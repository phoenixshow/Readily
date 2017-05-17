package com.phoenix.readily.database.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phoenix.readily.utils.Reflection;

import java.util.List;

import static android.R.attr.version;

/**
 * Created by flashing on 2017/5/15.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteDateBaseConfig CONFIG;
    private static SQLiteHelper INSTANCE;
    private Reflection reflection;
    private Context CONTEXT;

    private SQLiteHelper(Context context) {
        super(context, CONFIG.getDatabaseName(), null, CONFIG.getVersion());
        CONTEXT = context;
    }

    public static SQLiteHelper getInstance(Context context){
        if (INSTANCE == null){
            CONFIG = SQLiteDateBaseConfig.getInstance(context);
            INSTANCE = new SQLiteHelper(context);
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> list = CONFIG.getTables();
        reflection = new Reflection();
        for (int i = 0; i < list.size(); i++) {
            try {
                /**
                 * 用反射的方式创建具体DAO（如UserDAO）的类的对象，
                 * 由于实现SQLiteDateTable接口，所以可以强转
                 * 参数1：全类名；2：（UserDAO的）构造方法要传什么参数；3：构造方法中参数的类型
                 */
                SQLiteDateTable sqLiteDateTable = (SQLiteDateTable) reflection.newInstance(list.get(i),
                        new Object[]{CONTEXT}, new Class[]{Context.class});
                //事实上是调用具体DAO中重写的onCreate方法，里面有建表语句
                sqLiteDateTable.onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface SQLiteDateTable{
        void onCreate(SQLiteDatabase database);
        void onUpgrade(SQLiteDatabase database);
    }
}
