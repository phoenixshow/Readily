package com.phoenix.readily.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.base.SQLiteDateBaseConfig;
import com.phoenix.readily.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 数据备份业务层
 */
public class DataBackupBusiness extends BaseBusiness{
    private static final String SDCARD_PATH = Environment.
            getExternalStorageDirectory().getPath()+
            "/Readily/DatabaseBak/";
    //数据库所在路径————"/data/data/包名/databases/"
    private String DATA_PATH = Environment.getDataDirectory()+
            "/data/"+context.getPackageName()+"/databases/";

    public DataBackupBusiness(Context context) {
        super(context);
    }

    //读取上一次数据备份的日期
    public long loadDatabaseBackupDate() {
        long databaseBackupDate = 0;
        //获取指定Key的SP对象
        SharedPreferences sp = context.getSharedPreferences(
                "databaseBackupDate", Context.MODE_PRIVATE);
        //数据为空证明还不存在
        if (sp != null){//如果存在就获取指定Key的数据
            //设置默认值为0
            databaseBackupDate = sp.getLong("databaseBackupDate", 0);
        }
        return databaseBackupDate;
    }

    //数据备份，返回成功或者失败
    public boolean databaseBackup(Date backup) {
        boolean result = false;
        try {
            //判断是否有外存储设备
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                //获取当前应用路径下数据库文件
                File sourceFile = new File(DATA_PATH +
                        SQLiteDateBaseConfig.DATABASE_NAME);
                //如果数据库文件存在的话才进行备份操作
                if (sourceFile.exists()) {
                    //把文件保存到SD卡的指定目录下
                    File fileDir = new File(SDCARD_PATH);
                    //如果目录不存在 就创建目录
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    //调用工具类执行拷贝
                    //参数一：源路径+文件名；二：目标路径+文件名
                    FileUtil.cp(DATA_PATH + SQLiteDateBaseConfig.DATABASE_NAME,
                            SDCARD_PATH + SQLiteDateBaseConfig.DATABASE_NAME);
                }
                //保存备份的日期
                saveDatabaseBackupDate(backup.getTime());
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //保存备份日期
    private void saveDatabaseBackupDate(long millise) {
        //获取指定Key的SP对象
        SharedPreferences sp = context.
                getSharedPreferences("databaseBackupDate",
                        Context.MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor = sp.edit();
        //按照指定Key放入数据
        editor.putLong("databaseBackupDate", millise);
        //提交保存数据
        editor.commit();
    }

    //数据还原
    public boolean databaseRestore() {
        boolean result = false;
        try {
            //获取上一次备份的日期
            long databaseBackupDate = loadDatabaseBackupDate();
            //备份日期不为0，说明之前备份过
            if(databaseBackupDate != 0){
                //“/data/data/包名/databases”
                File fileDir = new File(DATA_PATH);
                //如果路径不存在就创建
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }
                //把数据库从SD卡拷贝回来
                FileUtil.cp(
                        SDCARD_PATH+SQLiteDateBaseConfig.DATABASE_NAME,
                        DATA_PATH+SQLiteDateBaseConfig.DATABASE_NAME);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
