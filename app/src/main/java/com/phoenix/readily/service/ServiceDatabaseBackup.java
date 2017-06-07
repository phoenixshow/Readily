package com.phoenix.readily.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.phoenix.readily.business.DataBackupBusiness;
import com.phoenix.readily.receiver.DatabaseBackupReceiver;

import java.util.Date;

/**
 * 数据库备份服务
 */

public class ServiceDatabaseBackup extends Service {
    //自动备份的间隔时间
//    private static final long SPACINGIN_TERVAL = 10000;//10秒钟
    private static final long SPACINGIN_TERVAL = 1000*60*60*48;//两天

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //数据备份的业务类
        DataBackupBusiness dataBackupBusiness =
                new DataBackupBusiness(this);
        //首先读取上一次数据备份的日期
        long backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
        Date backupDate = new Date();//备份日期
        if (backupMillise == 0){//如果上一次数据备份的日期为0
            dataBackupBusiness.databaseBackup(backupDate);
            //获取保存的备份日期
            backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
        }else{
            //判断，如果当前日期 - 上次备份的日期 >= 时间间隔
            //说明又到点了，该备份了
            if (backupDate.getTime() - backupMillise
                    >= SPACINGIN_TERVAL){
                //再执行一次备份
                dataBackupBusiness.databaseBackup(backupDate);
                //又得到最新的备份时间
                backupMillise = dataBackupBusiness.loadDatabaseBackupDate();
            }

        }
        //打印一下获取到的备份日期
        Log.e("TAG", "备份日期--------->" + backupMillise);

        Intent i = new Intent(this, DatabaseBackupReceiver.class);
        i.putExtra("date", backupMillise);//传入备份日期
        /**
         * PendingIntent延迟意图
         * getBroadcast获取一个广播
         * 参数一：上下文
         * 参数二：requestCode请求码
         * 参数三：你想要打开哪个类
         * 参数四：覆盖方式，FLAG_ONE_SHOT永远以第一次为准
         */
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                PendingIntent.FLAG_ONE_SHOT);
        //获取AlarmManager报警管理对象，相当于一个闹钟定时器
        AlarmManager am = (AlarmManager) getSystemService(
                ALARM_SERVICE);
        /**
         * 1、唤起的类型————RTC_WAKEUP表示在系统休眠状态下照样备份
         * 2、触发的时间
         * 3、表示闹钟响应动作————传入延迟意图，
         *              实际上启动的是一个广播DatabaseBackupReceiver
         */
        am.set(AlarmManager.RTC_WAKEUP,
                backupMillise+SPACINGIN_TERVAL, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
