package com.phoenix.readily.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AppGridAdapter;
import com.phoenix.readily.adapter.UserAdapter;
import com.phoenix.readily.business.UserBusiness;
import com.phoenix.readily.entity.Users;
import com.phoenix.readily.utils.RegexTools;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import static com.phoenix.readily.R.id.main_body_gv;

public class UserActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private ListView user_list_lv;
    private UserAdapter userAdapter;
    private UserBusiness userBusiness;
    private Users user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.user);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuUser);
    }

    //初始化变量
    private void initVariable() {
        userBusiness = new UserBusiness(this);
    }

    //初始化控件
    private void initView() {
        user_list_lv = (ListView) findViewById(R.id.user_list_lv);
    }

    //初始化监听
    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(user_list_lv);
    }

    //初始化数据
    private void initData() {
        if (userAdapter==null) {
            userAdapter = new UserAdapter(this);
            user_list_lv.setAdapter(userAdapter);
        }else {
            userAdapter.clear();
            userAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle(){
        setTopBarTitle(getString(R.string.title_user,
                new Object[]{userAdapter.getCount()}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //得到菜单信息
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListAdapter listAdapter = user_list_lv.getAdapter();
        user = (Users) listAdapter.getItem(acmi.position);
        menu.setHeaderIcon(R.drawable.user_small_icon);
        menu.setHeaderTitle(user.getUserName());
        createContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1://修改
                showUserAddOrEditDialog(user);
                break;
            case 2://删除
                delete();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        slideMenuToggle();
        if (item.getItemId() == 0){
            showUserAddOrEditDialog(null);
        }
    }

    private void delete() {
        String msg = getString(R.string.dialog_message_user_delete,
                new Object[]{user.getUserName()});
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            boolean result = userBusiness.hideUserByUserId(user.getUserId());
            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    private void showUserAddOrEditDialog(Users user){
        View view = getInflater().inflate(R.layout.user_add_or_edit, null);
        EditText user_name_et = (EditText) view.findViewById(R.id.user_name_et);
        String title;
        if (user == null){
            title = getString(R.string.dialog_title_user,
                    new Object[]{getString(R.string.title_add)});
        }else {
            user_name_et.setText(user.getUserName());
            title = getString(R.string.dialog_title_user,
                    new Object[]{getString(R.string.title_edit)});
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setView(view)
                .setIcon(R.drawable.grid_user)
                .setNeutralButton(getString(R.string.button_text_save),
                        new OnAddOrEditUserListener(user, user_name_et, true))
                .setNegativeButton(getString(R.string.button_text_cancel),
                        new OnAddOrEditUserListener(null, null, false))
                .show();
    }

    private class OnAddOrEditUserListener implements DialogInterface.OnClickListener{
        private Users user;
        private EditText userNameET;
        private boolean isSaveButton;//是否为保存按钮

        public OnAddOrEditUserListener(Users user, EditText userNameET,
                                       boolean isSaveButton) {
            this.user = user;
            this.userNameET = userNameET;
            this.isSaveButton = isSaveButton;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (!isSaveButton){//不是保存按钮，可以关闭
                setAlertDialogIsClose(dialog, true);
                return;
            }
            if (user == null){//新建用户
                user = new Users();
            }
            String userName = userNameET.getText().toString().trim();
            boolean checkResult = RegexTools.isChineseEnglishNum(userName);
            if (!checkResult){
                showMsg(getString(R.string.check_text_chinese_english_num,
                        new Object[]{userNameET.getHint()}));
                setAlertDialogIsClose(dialog, false);
                return;
            }else {
                setAlertDialogIsClose(dialog, true);
            }

            checkResult = userBusiness.isExistUserByUserName(userName, user.getUserId());
            if (checkResult){
                showMsg(getString(R.string.check_text_user_exist));
                setAlertDialogIsClose(dialog, false);
                return;
            }else {
                setAlertDialogIsClose(dialog, true);
            }

            user.setUserName(userName);
            boolean result = false;
            if (user.getUserId() == 0){
                result = userBusiness.insertUser(user);
            }else {
                result = userBusiness.updateUser(user);
            }

            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_add_fail));
            }
        }
    }
}
