package com.phoenix.readily.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AccountBookAdapter;
import com.phoenix.readily.adapter.AccountBookSelectAdapter;
import com.phoenix.readily.adapter.PayoutAdapter;
import com.phoenix.readily.business.AccountBookBusiness;
import com.phoenix.readily.business.PayoutBusiness;
import com.phoenix.readily.entity.AccountBook;
import com.phoenix.readily.entity.Payout;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import static com.phoenix.readily.R.id.account_book_list_lv;

public class PayoutActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private ListView payout_list_lv;
    private PayoutAdapter payoutAdapter;
    private PayoutBusiness payoutBusiness;
    private AccountBookBusiness accountBookBusiness;
    private Payout payout;
    private AccountBook accountBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.payout_list);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuPayout);
    }

    private void setTitle(){
        //查询消费-账本名称（消费记录总条数）
        setTopBarTitle(getString(R.string.title_payout,
                new Object[]{accountBook.getAccountBookName(),
                        payoutAdapter.getCount()}));
    }

    //初始化变量
    private void initVariable() {
        payoutBusiness = new PayoutBusiness(this);
        accountBookBusiness = new AccountBookBusiness(this);
        accountBook = accountBookBusiness.getDefaultAccountBook();
    }

    //初始化控件
    private void initView() {
        payout_list_lv = (ListView) findViewById(
                R.id.payout_list_lv);
    }

    //初始化监听
    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(payout_list_lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //得到菜单信息
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListAdapter listAdapter = payout_list_lv.getAdapter();
        payout = (Payout) listAdapter.getItem(acmi.position);
        menu.setHeaderIcon(R.drawable.payout_small_icon);
        menu.setHeaderTitle(payout.getCategoryName());
        createContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1://修改
                Intent intent = new Intent(this, PayoutAddOrEditActivity.class);
                intent.putExtra("payout", payout);
                startActivityForResult(intent, 1);
                break;
            case 2://删除
                delete(payout);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //初始化数据
    private void initData() {
        if (payoutAdapter==null) {
            payoutAdapter = new PayoutAdapter(this,
                    accountBook.getAccountBookId());
            payout_list_lv.setAdapter(payoutAdapter);
        }else {
            payoutAdapter.clear();
            payoutAdapter.updateList(accountBook.getAccountBookId());
        }
        setTitle();
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        //关闭滑动菜单
        slideMenuToggle();
        //如果点击了第一项菜单
        if(item.getItemId() == 0){
            //弹出选择账本对话框
            showAccountBookSelectDialog();
        }
    }

    private void showAccountBookSelectDialog(){
        //使用AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //加载ListView并设置Adapter
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_list, null);
        ListView select_lv = (ListView)view.findViewById(R.id.select_lv);
        AccountBookSelectAdapter accountBookSelectAdapter = new AccountBookSelectAdapter(this);
        select_lv.setAdapter(accountBookSelectAdapter);
        //设置对话框的标题和返回按钮等
        builder.setTitle(R.string.button_text_select_account_book)
                .setNegativeButton(R.string.button_text_back, null)
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        //ListView的项点击事件监听（传入对话框用以关闭对话框）
        select_lv.setOnItemClickListener(new OnAccountBookItemClickListener(dialog));
    }

    private class OnAccountBookItemClickListener implements AdapterView.OnItemClickListener {
        private AlertDialog dialog;

        public OnAccountBookItemClickListener(AlertDialog dialog){
            this.dialog = dialog;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //拿到条目实体，绑定适配器数据、关闭对话框
                accountBook = (AccountBook)parent.getAdapter().getItem(position);
        initData();
            dialog.dismiss();
    }
    }

    private void delete(Payout payout){
        //提示信息：你确定要删除%s消费记录吗
        String msg = getString(R.string.dialog_message_payout_delete, new Object[]{payout.getCategoryName()});
        //显示对话框并监听是、否按钮
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //根据消费记录ID隐藏消费记录
            boolean result = payoutBusiness.deletePayoutByPayoutId(payout.getPayoutId());
            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
