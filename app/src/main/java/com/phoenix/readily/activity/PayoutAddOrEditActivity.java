package com.phoenix.readily.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AccountBookSelectAdapter;
import com.phoenix.readily.adapter.CategoryAdapter;
import com.phoenix.readily.adapter.UserAdapter;
import com.phoenix.readily.business.AccountBookBusiness;
import com.phoenix.readily.business.CategoryBusiness;
import com.phoenix.readily.business.PayoutBusiness;
import com.phoenix.readily.business.UserBusiness;
import com.phoenix.readily.entity.AccountBook;
import com.phoenix.readily.entity.Category;
import com.phoenix.readily.entity.Payout;
import com.phoenix.readily.entity.Users;
import com.phoenix.readily.utils.DateUtil;
import com.phoenix.readily.utils.RegexTools;
import com.phoenix.readily.view.NumberDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.R.attr.category;
import static android.R.attr.name;
import static com.phoenix.readily.R.id.category_cancel_btn;
import static com.phoenix.readily.R.id.category_name_et;
import static com.phoenix.readily.R.id.category_parentid_sp;
import static com.phoenix.readily.R.id.category_save_btn;
import static com.phoenix.readily.R.id.payout_enter_amount_et;
import static com.phoenix.readily.R.id.payout_select_date_et;

public class PayoutAddOrEditActivity extends FrameActivity implements View.OnClickListener, NumberDialog.OnNumberDialogListener {
    private Button payout_save_btn;
    private Button payout_cancel_btn;
    private EditText payout_select_account_book_et;
    private EditText payout_enter_amount_et;
    private AutoCompleteTextView payout_select_category_actv;
    private EditText payout_select_date_et;
    private EditText payout_select_type_et;
    private EditText payout_select_user_et;
    private EditText payout_comment_et;
    private Button payout_select_account_book_btn;
    private Button payout_enter_amount_btn;
    private Button payout_select_category_btn;
    private Button payout_select_date_btn;
    private Button payout_select_type_btn;
    private Button payout_select_user_btn;

    private Payout payout;
    private AccountBook accountBook;

    private PayoutBusiness payoutBusiness;
    private AccountBookBusiness accountBookBusiness;
    private CategoryBusiness categoryBusiness;
    private UserBusiness userBusiness;

    private Integer accountBookId;
    private Integer categoryId;
    private String payoutUserId;//要保存的消费人ID，用,分隔
    private String payoutTypeArray[];//计算方式数组
    private List<LinearLayout> itemColor;
    private List<Users> userSelectedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.payout_add_or_edit);
        removeBottomBox();
        initVariable();
        initView();
        initListeners();
        initData();
        setTitle();
    }

    private void setTitle(){
        String title;
        if (payout == null){
            title = getString(R.string.title_payout_add_or_edit,
                    new Object[]{getString(R.string.title_add)});
        }else {
            title = getString(R.string.title_payout_add_or_edit,
                    new Object[]{getString(R.string.title_edit)});
            bindData(payout);
        }
        setTopBarTitle(title);
    }

    //初始化变量
    private void initVariable() {
        payoutBusiness = new PayoutBusiness(this);
        accountBookBusiness = new AccountBookBusiness(this);
        categoryBusiness = new CategoryBusiness(this);
        userBusiness = new UserBusiness(this);

        payout = (Payout) getIntent().getSerializableExtra(
                "payout");
        accountBook = accountBookBusiness.getDefaultAccountBook();
    }

    //初始化控件
    private void initView() {
        payout_save_btn = (Button)findViewById(R.id.payout_save_btn);
        payout_cancel_btn = (Button)findViewById(R.id.payout_cancel_btn);
        payout_select_account_book_btn = (Button)findViewById(R.id.payout_select_account_book_btn);
        payout_enter_amount_btn = (Button)findViewById(R.id.payout_enter_amount_btn);
        payout_select_category_btn = (Button)findViewById(R.id.payout_select_category_btn);
        payout_select_date_btn = (Button)findViewById(R.id.payout_select_date_btn);
        payout_select_type_btn = (Button)findViewById(R.id.payout_select_type_btn);
        payout_select_user_btn = (Button)findViewById(R.id.payout_select_user_btn);
        payout_select_account_book_et = (EditText)findViewById(R.id.payout_select_account_book_et);
        payout_enter_amount_et = (EditText)findViewById(R.id.payout_enter_amount_et);
        payout_select_category_actv = (AutoCompleteTextView)findViewById(R.id.payout_select_category_actv);
        payout_select_date_et = (EditText)findViewById(R.id.payout_select_date_et);
        payout_select_type_et = (EditText)findViewById(R.id.payout_select_type_et);
        payout_select_user_et = (EditText)findViewById(R.id.payout_select_user_et);
        payout_comment_et = (EditText)findViewById(R.id.payout_comment_et);
    }

    //初始化监听
    private void initListeners() {
        payout_save_btn.setOnClickListener(this);
        payout_cancel_btn.setOnClickListener(this);
        payout_select_account_book_btn.setOnClickListener(this);
        payout_enter_amount_btn.setOnClickListener(this);
        payout_select_category_btn.setOnClickListener(this);
        payout_select_date_btn.setOnClickListener(this);
        payout_select_type_btn.setOnClickListener(this);
        payout_select_user_btn.setOnClickListener(this);
        payout_select_category_actv.setOnItemClickListener(
                new OnAutoCompleteTextViewItemClickListener());
    }

    //初始化数据
    private void initData() {
        accountBookId = accountBook.getAccountBookId();
        payout_select_account_book_et.setText(
                accountBook.getAccountBookName());

        payout_select_category_actv.setAdapter(categoryBusiness.
                getAllCategoryArrayAdapter());

        payout_select_date_et.setText(DateUtil.getFormatDateTime(
                new Date(), "yyyy-MM-dd"));

        payoutTypeArray = getResources().getStringArray(
                R.array.PayoutType);
        payout_select_type_et.setText(payoutTypeArray[0]);
    }

    private void bindData(Payout payout){
        payout_select_account_book_et.setText(
                payout.getAccountBookName());
        accountBookId = payout.getAccountBookId();
        payout_enter_amount_et.setText(payout.getAmount().toString());
        payout_select_category_actv.setText(payout.getCategoryName());
        categoryId = payout.getCategoryId();
        payout_select_date_et.setText(DateUtil.getFormatDateTime(
                payout.getPayoutDate(), "yyyy-MM-dd"));
        payout_select_type_et.setText(payout.getPayoutType());
        //1,2,3,-->王小强,小李,小张,
        String userName = userBusiness.getUserNameByUserId(
                payout.getPayoutUserId());
        payout_select_user_et.setText(userName);
        payoutUserId = payout.getPayoutUserId();
        payout_comment_et.setText(payout.getComment());
    }

    private void addOrEditPayout(){
        boolean checkResult = checkData();
        if (!checkResult){
            return;
        }
        if (payout == null){
            payout = new Payout();
        }
        payout.setAccountBookId(accountBookId);
        payout.setCategoryId(categoryId);
        payout.setAmount(new BigDecimal(payout_enter_amount_et.
                getText().toString().trim()));
        payout.setPayoutDate(DateUtil.getDate(payout_select_date_et.
                getText().toString().trim(), "yyyy-MM-dd"));
        payout.setPayoutType(payout_select_type_et.getText().
                toString().trim());
        payout.setPayoutUserId(payoutUserId);
        payout.setComment(payout_comment_et.getText().toString()
                .trim());

        boolean result = false;
        if (payout.getPayoutId() == 0){
            result = payoutBusiness.insertPayout(payout);
        }else {
            result = payoutBusiness.updatePayout(payout);
        }
        if (result){
            showMsg(getString(R.string.tips_add_success));
            finish();
        }else {
            showMsg(getString(R.string.tips_add_fail));
        }
    }

    private boolean checkData(){
        //金额必须是数字，不超过小数点后两位，可以是整数、一位/两位小数
        boolean checkResult = RegexTools.isMoney(
                payout_enter_amount_et.getText().toString().trim());
        if (!checkResult){
            //获取焦点让用户重填
            payout_enter_amount_et.requestFocus();
            showMsg(getString(R.string.check_text_money));
            return false;
        }
        //验证类别不允许为空
        checkResult = RegexTools.isNull(categoryId);
        if (checkResult){
            //是否能获取焦点
            payout_select_category_btn.setFocusable(true);
            //使控件在TouchMode模式下仍然可以获得焦点
            payout_select_category_btn.setFocusableInTouchMode(true);
            payout_select_category_btn.requestFocus();
            showMsg(getString(R.string.check_text_category_is_null));
            return false;
        }
        //日期验证，不许向未来穿越
        Date date = DateUtil.getDate(payout_select_date_et.
                getText().toString().trim(), "yyyy-MM-dd");
        checkResult = DateUtil.isAfter(date);
        if (checkResult){
            payout_select_date_btn.setFocusable(true);
            payout_select_date_btn.setFocusableInTouchMode(true);
            payout_select_date_btn.requestFocus();
            showMsg(getString(R.string.check_text_date_is_after));
            return false;
        }
        //验证消费人不允许为空
        if (payoutUserId == null){
            payout_select_user_btn.setFocusable(true);
            payout_select_user_btn.setFocusableInTouchMode(true);
            payout_select_user_btn.requestFocus();
            showMsg(getString(R.string.check_text_payout_user_is_null));
            return false;
        }
        //均分、借贷必须是多人，个人必须是单人
        String payoutType = payout_select_type_et.getText().toString();
        if (payoutType.equals(payoutTypeArray[0]) ||
                payoutType.equals(payoutTypeArray[1])){
            if (payoutUserId.split(",").length <= 1){
                payout_select_user_btn.setFocusable(true);
                payout_select_user_btn.setFocusableInTouchMode(true);
                payout_select_user_btn.requestFocus();
                showMsg(getString(R.string.check_text_payout_user));
                return false;
            }
        }else {
            if ("".equals(payoutUserId)){
                payout_select_user_btn.setFocusable(true);
                payout_select_user_btn.setFocusableInTouchMode(true);
                payout_select_user_btn.requestFocus();
                showMsg(getString(R.string.check_text_payout_user2));
                return false;
            }
        }
        return true;
    }

    private class OnAutoCompleteTextViewItemClickListener
            implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category category = (Category) parent.getAdapter().
                    getItem(position);
            categoryId = category.getCategoryId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.payout_select_account_book_btn://选择账本
                showAccountBookSelectDialog();
                break;
            case R.id.payout_enter_amount_btn://输入金额
                (new NumberDialog(this)).show();
                break;
            case R.id.payout_select_category_btn://选择类别
                showCategorySelectDialog();
                break;
            case R.id.payout_select_date_btn://选择日期
                Calendar calendar = Calendar.getInstance();
                showCategorySelectDialog(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE));
                break;
            case R.id.payout_select_type_btn://计算方式
                showPayoutTypeSelectDialog();
                break;
            case R.id.payout_select_user_btn://选择消费人
                showUserSelectDialog(payout_select_type_et.getText().toString());
                break;
            case R.id.payout_save_btn://保存
                addOrEditPayout();
                break;
            case R.id.payout_cancel_btn://取消
                finish();
                break;
        }
    }

    @Override
    public void setNumberFinish(BigDecimal number) {
        payout_enter_amount_et.setText(number.toString());
    }

    private void showAccountBookSelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_list, null);
        ListView select_lv = (ListView) view.findViewById(R.id.select_lv);
        AccountBookSelectAdapter accountBookAdapter = new AccountBookSelectAdapter(this);
        select_lv.setAdapter(accountBookAdapter);

        builder.setTitle(R.string.button_text_select_account_book)
                .setNegativeButton(R.string.button_text_back, null)//返回
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        select_lv.setOnItemClickListener(new
                OnAccountBookItemClickListener(dialog));
    }

    private class OnAccountBookItemClickListener implements
            AdapterView.OnItemClickListener{
        private AlertDialog dialog;

        public OnAccountBookItemClickListener(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AccountBook accountBook = (AccountBook) parent.getAdapter().
                    getItem(position);
            accountBookId = accountBook.getAccountBookId();
            dialog.dismiss();
        }
    }

    private void showCategorySelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.category_select_list, null);
        ExpandableListView category_list_elv = (ExpandableListView) view.
                findViewById(R.id.category_list_elv);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this);
        category_list_elv.setAdapter(categoryAdapter);

        builder.setIcon(R.drawable.category_small_icon)
                .setTitle(R.string.button_text_select_category)
                .setNegativeButton(R.string.button_text_back, null)//返回
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        category_list_elv.setOnGroupClickListener(new
                OnCategoryGroupItemClickListener(dialog, categoryAdapter));
        category_list_elv.setOnChildClickListener(new
                OnCategoryChildItemClickListener(dialog, categoryAdapter));
    }

    private class OnCategoryGroupItemClickListener implements
            ExpandableListView.OnGroupClickListener{
        private AlertDialog dialog;
        private CategoryAdapter categoryAdapter;

        public OnCategoryGroupItemClickListener(AlertDialog dialog,
                    CategoryAdapter categoryAdapter) {
            this.dialog = dialog;
            this.categoryAdapter = categoryAdapter;
        }

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            int count = categoryAdapter.getChildrenCount(groupPosition);
            if (count == 0){
                Category category = (Category) categoryAdapter.getGroup(
                        groupPosition);
                payout_select_category_actv.setText(category.getCategoryName());
                categoryId = category.getCategoryId();
                dialog.dismiss();
            }
            return false;
        }
    }

    private class OnCategoryChildItemClickListener implements
            ExpandableListView.OnChildClickListener{
        private AlertDialog dialog;
        private CategoryAdapter categoryAdapter;

        public OnCategoryChildItemClickListener(AlertDialog dialog,
                    CategoryAdapter categoryAdapter) {
            this.dialog = dialog;
            this.categoryAdapter = categoryAdapter;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Category category = (Category) categoryAdapter.getChild(
                    groupPosition, childPosition);
            payout_select_category_actv.setText(category.getCategoryName());
            categoryId = category.getCategoryId();
            dialog.dismiss();
            return false;
        }
    }

    private void showCategorySelectDialog(int year, int month, int day){
        new DatePickerDialog(this, new OnDateSelectedListener(),
                year, month, day).show();
    }

    private class OnDateSelectedListener implements
            DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Date date = new Date(year-1900, month, dayOfMonth);
            payout_select_date_et.setText(DateUtil.
                    getFormatDateTime(date, "yyyy-MM-dd"));
        }
    }

    private void showPayoutTypeSelectDialog(){
        //TODO
    }

    private void showUserSelectDialog(String payoutType){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.user, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(
                R.id.user_list_ll);
        linearLayout.setBackgroundResource(R.drawable.blue);
        ListView select_lv = (ListView) view.findViewById(
                R.id.user_list_lv);
        UserAdapter userAdapter = new UserAdapter(this);
        select_lv.setAdapter(userAdapter);

        builder.setIcon(R.drawable.user_small_icon)
                .setTitle(R.string.button_text_select_user)
                .setNegativeButton(R.string.button_text_back,
                        new OnSelectUserBack())
                .setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        select_lv.setOnItemClickListener(new OnUserItemClickListener(
                dialog, payoutType));
    }

    private class OnUserItemClickListener implements
            AdapterView.OnItemClickListener{
        private AlertDialog dialog;
        private String payoutType;

        public OnUserItemClickListener(AlertDialog dialog, String payoutType) {
            this.dialog = dialog;
            this.payoutType = payoutType;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] payoutTypeArray = getResources().getStringArray(
                    R.array.PayoutType);
            Users user = (Users) parent.getAdapter().getItem(position);
            //均分或借贷时可选择多人
            if (payoutType.equals(payoutTypeArray[0]) ||
                    payoutType.equals(payoutTypeArray[1])){
                LinearLayout linearLayout = (LinearLayout) view.
                        findViewById(R.id.user_item_ll);
                if (itemColor == null && userSelectedList == null){
                    itemColor = new ArrayList<>();
                    userSelectedList = new ArrayList<>();
                }
                //如果颜色集合中已经包含了这个条目的颜色，就取消这个条目的选择
                if (itemColor.contains(linearLayout)){
                    linearLayout.setBackgroundResource(R.drawable.blue);
                    itemColor.remove(linearLayout);
                    userSelectedList.remove(user);
                }
                //添加这个条目的选择
                else {
                    linearLayout.setBackgroundResource(R.drawable.red);
                    itemColor.add(linearLayout);
                    userSelectedList.add(user);
                }
                return;
            }

            //个人消费只能选择一个人
            if (payoutType.equals(payoutTypeArray[2])){
//                userSelectedList = new ArrayList<>();
//                userSelectedList.add(user);
                String name = user.getUserName()+",";
                payoutUserId = user.getUserId()+",";
                payout_select_user_et.setText(name);
                dialog.dismiss();
            }
        }
    }

    private class OnSelectUserBack implements
            DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //先把原来的数据清空
            payout_select_user_et.setText("");
            String name = "";
            payoutUserId = "";
            if (userSelectedList != null){
                for (int i = 0; i < userSelectedList.size(); i++) {
                    name += userSelectedList.get(i).getUserName()+",";
                    payoutUserId += userSelectedList.get(i).
                            getUserId()+",";
                }
                payout_select_user_et.setText(name);
            }
            itemColor = null;
            userSelectedList = null;
        }
    }
}
