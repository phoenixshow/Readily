package com.phoenix.readily.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AccountBookAdapter;
import com.phoenix.readily.business.AccountBookBusiness;
import com.phoenix.readily.entity.AccountBook;
import com.phoenix.readily.utils.RegexTools;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import static android.R.attr.isDefault;
import static com.phoenix.readily.R.id.account_book_list_lv;

public class AccountBookActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private ListView account_book_list_lv;
    private AccountBookAdapter accountBookAdapter;
    private AccountBookBusiness accountBookBusiness;
    private AccountBook accountBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.account_book_list);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuAccountBook);
    }

    //初始化变量
    private void initVariable() {
        accountBookBusiness = new AccountBookBusiness(this);
    }

    //初始化控件
    private void initView() {
        account_book_list_lv = (ListView) findViewById(R.id.account_book_list_lv);
    }

    //初始化监听
    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(account_book_list_lv);
    }

    //初始化数据
    private void initData() {
        if (accountBookAdapter==null) {
            accountBookAdapter = new AccountBookAdapter(this);
            account_book_list_lv.setAdapter(accountBookAdapter);
        }else {
            accountBookAdapter.clear();
            accountBookAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle(){
        setTopBarTitle(getString(R.string.title_account_book,
                new Object[]{accountBookAdapter.getCount()}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //得到菜单信息
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListAdapter listAdapter = account_book_list_lv.getAdapter();
        accountBook = (AccountBook) listAdapter.getItem(acmi.position);
        menu.setHeaderIcon(R.drawable.account_book_small_icon);
        menu.setHeaderTitle(accountBook.getAccountBookName());
        createContextMenu(menu);
        if (accountBook.getIsDefault() == 1){
            menu.findItem(2).setEnabled(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1://修改
                showAccountBookAddOrEditDialog(accountBook);
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
            showAccountBookAddOrEditDialog(null);
        }
    }

    private void delete() {
        String msg = getString(
                R.string.dialog_message_account_book_delete,
                new Object[]{accountBook.getAccountBookName()});
        showAlertDialog(R.string.dialog_title_delete, msg,
                new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            boolean result = accountBookBusiness.
                    deleteAccountBookByAccountBookId(
                            accountBook.getAccountBookId());
            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    private void showAccountBookAddOrEditDialog(AccountBook accountBook){
        View view = getInflater().inflate(R.layout.account_book_add_or_edit, null);
        EditText account_book_name_et = (EditText) view.findViewById(
                R.id.account_book_name_et);
        CheckBox account_book_check_default_cb = (CheckBox) view.findViewById(
                R.id.account_book_check_default_cb);
        String title;
        if (accountBook == null){
            title = getString(R.string.dialog_title_account_book,
                    new Object[]{getString(R.string.title_add)});
        }else {
            account_book_name_et.setText(accountBook.getAccountBookName());
            title = getString(R.string.dialog_title_account_book,
                    new Object[]{getString(R.string.title_edit)});
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setView(view)
                .setIcon(R.drawable.grid_account_book)
                .setNeutralButton(getString(R.string.button_text_save),
                        new OnAddOrEditAccountBookListener(accountBook,
                                account_book_name_et, account_book_check_default_cb, true))
                .setNegativeButton(getString(R.string.button_text_cancel),
                        new OnAddOrEditAccountBookListener(null, null, null, false))
                .show();
    }

    private class OnAddOrEditAccountBookListener implements DialogInterface.OnClickListener{
        private AccountBook accountBook;
        private EditText accountBookNameET;
        private CheckBox accountBookDefaultCB;
        private boolean isSaveButton;//是否为保存按钮

        public OnAddOrEditAccountBookListener(AccountBook accountBook,
            EditText accountBookNameET, CheckBox accountBookDefaultCB,
                                              boolean isSaveButton) {
            this.accountBook = accountBook;
            this.accountBookNameET = accountBookNameET;
            this.accountBookDefaultCB = accountBookDefaultCB;
            this.isSaveButton = isSaveButton;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (!isSaveButton){//不是保存按钮，可以关闭
                setAlertDialogIsClose(dialog, true);
                return;
            }
            int isDefault = 0;
            if (accountBook == null){//新建账本
                accountBook = new AccountBook();
            }else {
                isDefault = accountBook.getIsDefault();
            }
            String accountBookName = accountBookNameET.getText().toString().trim();
            boolean checkResult = RegexTools.isChineseEnglishNum(accountBookName);
            if (!checkResult){
                showMsg(getString(R.string.check_text_chinese_english_num,
                        new Object[]{accountBookNameET.getHint()}));
                setAlertDialogIsClose(dialog, false);
                return;
            }else {
                setAlertDialogIsClose(dialog, true);
            }

            checkResult = accountBookBusiness.isExistAccountBookByAccountBookName(accountBookName, accountBook.getAccountBookId());
            if (checkResult){
                showMsg(getString(R.string.check_text_account_book_exist));
                setAlertDialogIsClose(dialog, false);
                return;
            }else {
                setAlertDialogIsClose(dialog, true);
            }

            accountBook.setAccountBookName(accountBookName);
            //是否为默认账本的判断
            if (accountBookDefaultCB.isChecked()){
                accountBook.setIsDefault(1);
            }else {
                accountBook.setIsDefault(0);
            }
            if (accountBook.getAccountBookId() > 0 && isDefault == 1){
                accountBook.setIsDefault(1);
            }
            boolean result = false;
            if (accountBook.getAccountBookId() == 0){
                result = accountBookBusiness.insertAccountBook(accountBook);
            }else {
                result = accountBookBusiness.updateAccountBook(accountBook);
            }

            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_add_fail));
            }
        }
    }
}
