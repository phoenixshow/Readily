package com.phoenix.readily.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.CategoryAdapter;
import com.phoenix.readily.business.CategoryBusiness;
import com.phoenix.readily.entity.Category;
import com.phoenix.readily.utils.RegexTools;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

public class CategoryActivity extends FrameActivity implements SlideMenuView.OnSlideMenuListener {
    private ExpandableListView category_list_elv;
    private CategoryAdapter categoryAdapter;
    private CategoryBusiness categoryBusiness;
    private Category category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.category_list);
        initVariable();
        initView();
        initListeners();
        initData();
        createSlideMenu(R.array.SlideMenuCategory);
    }

    //初始化变量
    private void initVariable() {
        categoryBusiness = new CategoryBusiness(this);
    }

    //初始化控件
    private void initView() {
        category_list_elv = (ExpandableListView) findViewById(R.id.category_list_elv);
    }

    //初始化监听
    private void initListeners() {
        //注册上下文菜单
        registerForContextMenu(category_list_elv);
    }

    //初始化数据
    private void initData() {
        if (categoryAdapter==null) {
            categoryAdapter = new CategoryAdapter(this);
            category_list_elv.setAdapter(categoryAdapter);
        }else {
            categoryAdapter.clear();
            categoryAdapter.updateList();
        }
        setTitle();
    }

    private void setTitle(){
        int count = categoryBusiness.getNotHideCount();//包括主类和子类的总数
        setTopBarTitle(getString(R.string.title_category,
                new Object[]{count}));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                        ContextMenu.ContextMenuInfo menuInfo) {
        //得到菜单信息
        ExpandableListView.ExpandableListContextMenuInfo elcm =
                (ExpandableListView.
                        ExpandableListContextMenuInfo) menuInfo;
        //获取菜单的位置信息
        long position = elcm.packedPosition;
        //得到类型，知道它是组还是子
        int type = ExpandableListView.getPackedPositionType(
                position);
        //通过位置信息得到组位置
        int groupPosition = ExpandableListView.
                getPackedPositionGroup(position);
        switch (type){
            //是组
            case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                //根据组位置取得实体
                category = (Category) categoryAdapter.getGroup(
                        groupPosition);
                break;
            //是子
            case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                //获取子位置
                int childPosition = ExpandableListView.
                        getPackedPositionChild(position);
                //获取某组下的某子位置的实体
                category = (Category) categoryAdapter.getChild(
                        groupPosition, childPosition);
                break;
        }

        menu.setHeaderIcon(R.drawable.category_small_icon);
        if(category!=null) {
            menu.setHeaderTitle(category.getCategoryName());
        }
        createContextMenu(menu);
        menu.add(0, 3, 0, R.string.category_total);
        if (categoryAdapter.getChildrenCount(groupPosition)!=0 &&
                category.getParentId()==0){
            menu.findItem(2).setEnabled(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case 1://修改
                intent = new Intent(this, CategoryAddOrEditActivity.class);
                intent.putExtra("category", category);
                startActivityForResult(intent, 1);
                break;
            case 2://删除
                delete(category);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSlideMenuItemClick(SlideMenuItem item) {
        slideMenuToggle();
        if (item.getItemId() == 0){//新建
            Intent intent = new Intent(this,
                    CategoryAddOrEditActivity.class);
            startActivityForResult(intent, 1);
            return;
        }
    }

    private void delete(Category category) {
        String msg = getString(
                R.string.dialog_message_category_delete,
                new Object[]{category.getCategoryName()});
        showAlertDialog(R.string.dialog_title_delete, msg, new OnDeleteClickListener());
    }

    private class OnDeleteClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            boolean result = categoryBusiness.hideCategoryByPath(
                    category.getPath());
            if (result){
                initData();
            }else {
                showMsg(getString(R.string.tips_delete_fail));
            }
        }
    }

//    private void showCategoryAddOrEditDialog(Category category){
//        View view = getInflater().inflate(R.layout.category_add_or_edit, null);
//        EditText category_name_et = (EditText) view.findViewById(
//                R.id.category_name_et);
//        CheckBox category_check_default_cb = (CheckBox) view.findViewById(
//                R.id.category_check_default_cb);
//        String title;
//        if (category == null){
//            title = getString(R.string.dialog_title_category,
//                    new Object[]{getString(R.string.title_add)});
//        }else {
//            category_name_et.setText(category.getCategoryName());
//            title = getString(R.string.dialog_title_category,
//                    new Object[]{getString(R.string.title_edit)});
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title)
//                .setView(view)
//                .setIcon(R.drawable.grid_category)
//                .setNeutralButton(getString(R.string.button_text_save),
//                        new OnAddOrEditCategoryListener(category,
//                                category_name_et, category_check_default_cb, true))
//                .setNegativeButton(getString(R.string.button_text_cancel),
//                        new OnAddOrEditCategoryListener(null, null, null, false))
//                .show();
//    }
//
//    private class OnAddOrEditCategoryListener implements DialogInterface.OnClickListener{
//        private Category category;
//        private EditText categoryNameET;
//        private CheckBox categoryDefaultCB;
//        private boolean isSaveButton;//是否为保存按钮
//
//        public OnAddOrEditCategoryListener(Category category,
//            EditText categoryNameET, CheckBox categoryDefaultCB,
//                                              boolean isSaveButton) {
//            this.category = category;
//            this.categoryNameET = categoryNameET;
//            this.categoryDefaultCB = categoryDefaultCB;
//            this.isSaveButton = isSaveButton;
//        }
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if (!isSaveButton){//不是保存按钮，可以关闭
//                setAlertDialogIsClose(dialog, true);
//                return;
//            }
//            int isDefault = 0;
//            if (category == null){//新建账本
//                category = new Category();
//            }else {
//                isDefault = category.getIsDefault();
//            }
//            String categoryName = categoryNameET.getText().toString().trim();
//            boolean checkResult = RegexTools.isChineseEnglishNum(categoryName);
//            if (!checkResult){
//                showMsg(getString(R.string.check_text_chinese_english_num,
//                        new Object[]{categoryNameET.getHint()}));
//                setAlertDialogIsClose(dialog, false);
//                return;
//            }else {
//                setAlertDialogIsClose(dialog, true);
//            }
//
//            checkResult = categoryBusiness.isExistCategoryByCategoryName(categoryName, category.getCategoryId());
//            if (checkResult){
//                showMsg(getString(R.string.check_text_category_exist));
//                setAlertDialogIsClose(dialog, false);
//                return;
//            }else {
//                setAlertDialogIsClose(dialog, true);
//            }
//
//            category.setCategoryName(categoryName);
//            //是否为默认账本的判断
//            if (categoryDefaultCB.isChecked()){
//                category.setIsDefault(1);
//            }else {
//                category.setIsDefault(0);
//            }
//            if (category.getCategoryId() > 0 && isDefault == 1){
//                category.setIsDefault(1);
//            }
//            boolean result = false;
//            if (category.getCategoryId() == 0){
//                result = categoryBusiness.insertCategory(category);
//            }else {
//                result = categoryBusiness.updateCategory(category);
//            }
//
//            if (result){
//                initData();
//            }else {
//                showMsg(getString(R.string.tips_add_fail));
//            }
//        }
//    }
}
