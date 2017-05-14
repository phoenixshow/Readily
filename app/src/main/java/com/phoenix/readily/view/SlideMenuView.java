package com.phoenix.readily.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.phoenix.readily.R;
import com.phoenix.readily.adapter.SlideMenuAdapter;
import com.phoenix.readily.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动菜单
 */

public class SlideMenuView {
    private Activity activity;
    private List<SlideMenuItem> menuList;
    private boolean isClosed;
    private RelativeLayout bottomBoxLayout;

    public SlideMenuView(Activity activity) {
        this.activity = activity;
        initVariable();
        initView();
        initListeners();
    }

    private void initVariable() {
        menuList = new ArrayList<>();
        isClosed = true;
    }

    private void initView() {
        bottomBoxLayout = (RelativeLayout) activity.findViewById(R.id.include_bottom);
    }

    private void initListeners() {
        bottomBoxLayout.setOnClickListener(new OnSlideMenuClick());
    }

    //监听菜单的点击事件
    private class OnSlideMenuClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            toggle();
        }
    }

    //打开菜单
    private void open(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.addRule(RelativeLayout.BELOW, R.id.include_title);
        bottomBoxLayout.setLayoutParams(layoutParams);
        isClosed = false;
    }

    //关闭菜单
    private void close(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(activity, 68));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomBoxLayout.setLayoutParams(layoutParams);
        isClosed = true;
    }

    //开关方法控制菜单打开/关闭
    private void toggle(){
        if (isClosed){
            open();
        }else {
            close();
        }
    }

    //添加菜单项
    public void add(SlideMenuItem slideMenuItem){
        menuList.add(slideMenuItem);
    }

    //绑定数据源
    public void bindList(){
        SlideMenuAdapter adapter = new SlideMenuAdapter(activity, menuList);
        ListView listView = (ListView) activity.findViewById(R.id.slide_list_lv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnSlideMenuItemClick());
    }

    private class OnSlideMenuItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
