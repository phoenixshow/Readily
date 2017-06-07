package com.phoenix.readily.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AppGridAdapter;
import com.phoenix.readily.business.DataBackupBusiness;
import com.phoenix.readily.service.ServiceDatabaseBackup;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import java.util.Date;

public class MainActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private GridView main_body_gv;
    private AppGridAdapter gridAdapter;
    private DataBackupBusiness dataBackupBusiness;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.main_body);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuActivityMain);
        hideTitleBackButton();
        startMyService();
    }

    private void startMyService() {
        Intent intent = new Intent(this, ServiceDatabaseBackup.class);
        startService(intent);
    }

    //初始化变量
    private void initVariable() {
        gridAdapter = new AppGridAdapter(this);
        dataBackupBusiness = new DataBackupBusiness(this);
    }

    //初始化控件
    private void initView() {
        main_body_gv = (GridView) findViewById(R.id.main_body_gv);
    }

    //初始化监听
    private void initListeners() {
        main_body_gv.setOnItemClickListener(new OnGridItemClickListener());
    }

    //初始化数据
    private void initData() {
        main_body_gv.setAdapter(gridAdapter);
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        slideMenuToggle();//先关闭菜单
        if (item.getItemId() == 0){//数据备份
            databaseBackup();
        }
        if (item.getItemId() == 1){//数据还原
            databaseRestore();
        }
    }

    //数据备份
    private void databaseBackup() {
        if (dataBackupBusiness.databaseBackup(new Date())){
            //提示用户数据备份成功
            showMsg(R.string.dialog_message_backup_success);
        }else {
            //提示用户数据备份失败
            showMsg(R.string.dialog_message_backup_fail);
        }
    }

    //数据还原
    private void databaseRestore() {
        if (dataBackupBusiness.databaseRestore()){
            //提示用户数据还原成功
            showMsg(R.string.dialog_message_restore_success);
        }else {
            //提示用户数据还原失败
            showMsg(R.string.dialog_message_restore_fail);
        }
    }

    private class OnGridItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String menuName = (String) parent.getAdapter().getItem(position);
            if (menuName.equals(getString(R.string.grid_user_manage))){//人员管理
                openActivity(UserActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_account_manage))){//账本管理
                openActivity(AccountBookActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_category_manage))){//类别管理
                openActivity(CategoryActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_payout_add))){//记录消费
                openActivity(PayoutAddOrEditActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_payout_manage))){//记录消费
                openActivity(PayoutActivity.class);
                return;
            }
            if (menuName.equals(getString(R.string.grid_statistics_manage))){//统计管理
                openActivity(StatisticsActivity.class);
                return;
            }
        }
    }
}
