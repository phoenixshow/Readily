package com.phoenix.readily.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.MainActivity;
import com.phoenix.readily.service.ServiceDatabaseBackup;

/**
 * 数据库备份广播接收器
 */
public class DatabaseBackupReceiver extends BroadcastReceiver{
    //使用通知来做提示
    NotificationManager notificationManager;
    Notification notification;
    Intent i;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取系统服务，强转为通知管理器
        notificationManager = (NotificationManager) context.
                getSystemService(context.NOTIFICATION_SERVICE);
        Log.e("TAG", "广播--------->日期:" + intent.
                getLongExtra("date", 0));
        //当点击通知时显示的内容
        String contentTitle = "随手通知您";
        String contentText = "随手已执行数据备份";
        //点击通知时打开MainActivity
        i = new Intent(context, MainActivity.class);
        //设置标志位，表示如果Intent要启动的Activity在栈顶，
        // 则无须创建新的实例
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //参数三表示要打开MainActivity
        //参数四更新当前的覆盖方式
        pendingIntent = PendingIntent.getActivity(context, 100, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        //设置通知在状态栏显示的图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //设置通知显示的标题
        builder.setContentTitle(contentTitle);
        //设置通知显示的内容
        builder.setContentText(contentText);
        //通知时发出的默认声音
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();

        //开始执行这个通知//参数一为该通知的ID，或者叫唯一标识
        notificationManager.notify(10, notification);
        //再次启动服务
        Intent serviceIntent = new Intent(context,
                ServiceDatabaseBackup.class);
        context.startService(serviceIntent);
    }
}
