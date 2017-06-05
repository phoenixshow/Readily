package com.phoenix.readily.activity.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phoenix.readily.R;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import java.util.ArrayList;

/**
 * Created by flashing on 2017/5/12.
 */

public class FrameActivity extends BaseActivity {
    private final int SDK_PERMISSION_REQUEST = 127;
    private SlideMenuView slideMenuView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题栏
        setContentView(R.layout.activity_main);

        getPersimmions();
    }

    protected void appendMainBody(int resId){
        LinearLayout mainBody = (LinearLayout) findViewById(R.id.main_body_ll);
        View view = LayoutInflater.from(this).inflate(resId, null);
        //用代码来动态的设置宽高
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mainBody.addView(view,layoutParams);
    }

    //创建滑动菜单
    protected void createSlideMenu(int resId){
        slideMenuView = new SlideMenuView(this);
        String[] menuItemArray = getResources().getStringArray(resId);//得到资源数组
        for (int i = 0; i < menuItemArray.length; i++) {
            SlideMenuItem item = new SlideMenuItem(i, menuItemArray[i]);
            slideMenuView.add(item);
        }
        slideMenuView.bindList();
    }

    //切换菜单开闭
    protected void slideMenuToggle(){
        slideMenuView.toggle();
    }

    protected void createContextMenu(Menu menu){
        menu.add(0, 1, 0, R.string.menu_text_edit);
        menu.add(0, 2, 0, R.string.menu_text_delete);
    }

    protected void setTopBarTitle(String title){
        TextView top_title_tv = (TextView) findViewById(R.id.top_title_tv);
        top_title_tv.setText(title);
    }

    protected void removeBottomBox(){
        slideMenuView = new SlideMenuView(this);
        slideMenuView.removeBottomBox();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case SDK_PERMISSION_REQUEST:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 允许
                }else{
                    // 不允许
                    showMsg("您已拒绝授权，无法保证您的数据安全，程序已退出");
                    finish();
                }
                break;
        }
    }
}
