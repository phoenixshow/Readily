package com.phoenix.readily.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.adapter.AppGridAdapter;
import com.phoenix.readily.business.CategoryBusiness;
import com.phoenix.readily.entity.Category;
import com.phoenix.readily.utils.RegexTools;
import com.phoenix.readily.view.SlideMenuItem;
import com.phoenix.readily.view.SlideMenuView;

import static com.phoenix.readily.R.id.main_body_gv;

public class CategoryAddOrEditActivity extends FrameActivity implements View.OnClickListener {
    private EditText category_name_et;
    private Spinner category_parentid_sp;
    private Button category_save_btn;
    private Button category_cancel_btn;

    private CategoryBusiness categoryBusiness;
    private Category category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(R.layout.category_add_or_edit);
        removeBottomBox();
        initVariable();
        initView();
        initListeners();
        initData();
        setTitle();
    }

    private void setTitle(){
        String title;
        if (category == null){
            title = getString(R.string.title_category_add_or_edit,
                    new Object[]{getString(R.string.title_add)});
        }else {
            title = getString(R.string.title_category_add_or_edit,
                    new Object[]{getString(R.string.title_edit)});
            bindData(category);
        }
        setTopBarTitle(title);
    }

    //初始化变量
    private void initVariable() {
        categoryBusiness = new CategoryBusiness(this);
        category = (Category) getIntent().getSerializableExtra("category");
    }

    //初始化控件
    private void initView() {
        category_name_et = (EditText) findViewById(R.id.category_name_et);
        category_parentid_sp = (Spinner) findViewById(R.id.category_parentid_sp);
        category_save_btn = (Button) findViewById(R.id.category_save_btn);
        category_cancel_btn = (Button) findViewById(R.id.category_cancel_btn);
    }

    //初始化监听
    private void initListeners() {
        category_save_btn.setOnClickListener(this);
        category_cancel_btn.setOnClickListener(this);
    }

    //初始化数据
    private void initData() {
        ArrayAdapter<Category> arrayAdapter = categoryBusiness.getRootCategoryArrayAdapter();
        category_parentid_sp.setAdapter(arrayAdapter);
    }

    private void bindData(Category category){
        category_name_et.setText(category.getCategoryName());
        ArrayAdapter arrayAdapter = (ArrayAdapter) category_parentid_sp.getAdapter();
        if (category.getParentId() != 0){//说明是个子类
            int position = 0;
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                Category categoryItem = (Category) arrayAdapter.getItem(i);
                if (categoryItem.getCategoryId() == category.getParentId()){
                    position = arrayAdapter.getPosition(categoryItem);
                    break;
                }
            }
            category_parentid_sp.setSelection(position);
        }else {//父类
            int count = categoryBusiness.getNotHideCountByParentId(category.getCategoryId());
            if (count != 0){//它有子类
                category_parentid_sp.setEnabled(false);
            }
        }
    }

    private void addOrEditCategory(){
        String categoryName = category_name_et.getText().
                toString().trim();
        boolean checkResult = RegexTools.
                isChineseEnglishNum(categoryName);
        if (!checkResult){
            showMsg(getString(R.string.check_text_chinese_english_num,
                    new Object[]{getString(
                            R.string.textview_text_category_name)}));
            return;
        }
        if (category == null){
            category = new Category();
            category.setPath("");
        }
        category.setCategoryName(categoryName);
        //如果不是“--请选择--”的话，说明有父类
        if (!getString(R.string.spinner_please_choose).equals(
                category_parentid_sp.getSelectedItem().toString())){
            Category parentCategory = (Category) category_parentid_sp.
                    getSelectedItem();
            if(parentCategory != null){
                if (category.getCategoryId() != 0 && category.getCategoryId() == parentCategory.getCategoryId()){
                    showMsg(getString(R.string.check_text_parent_is_self));
                    return;
                }
                category.setParentId(parentCategory.getCategoryId());
            }
        }
        //是“--请选择--”
        else {
            category.setParentId(0);
        }
        boolean result = false;
        if (category.getCategoryId() == 0){
            result = categoryBusiness.insertCategory(category);
        }else {
            result = categoryBusiness.updateCategory(category);
        }
        if (result){
            showMsg(getString(R.string.tips_add_success));
            finish();
        }else {
            showMsg(getString(R.string.tips_add_fail));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category_save_btn://保存
                addOrEditCategory();
                break;
            case R.id.category_cancel_btn://取消
                finish();
                break;
        }
    }
}
