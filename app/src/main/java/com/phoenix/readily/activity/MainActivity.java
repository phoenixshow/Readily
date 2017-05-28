package com.phoenix.readily.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AppGridAdapter;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

public class MainActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private GridView main_body_gv;
    private AppGridAdapter gridAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.main_body);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuActivityMain);
    }

    //初始化变量
    private void initVariable() {
        gridAdapter = new AppGridAdapter(this);
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
        showMsg(item.getTitle());
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
        }
    }
}
