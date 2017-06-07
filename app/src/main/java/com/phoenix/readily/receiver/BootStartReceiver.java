package com.phoenix.readily.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phoenix.readily.service.ServiceDatabaseBackup;

/**
 * 开机启动广播接收器
 */
public class BootStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //启动服务
        Intent i = new Intent(context, ServiceDatabaseBackup.class);
        context.startService(i);
    }
}
