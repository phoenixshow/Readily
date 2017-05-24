package com.phoenix.readily.business;

import android.content.ContentValues;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.phoenix.readily.R;
import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.dao.CategoryDAO;
import com.phoenix.readily.entity.Category;

import java.util.List;

/**
 * Created by flashing on 2017/5/16.
 */

public class CategoryBusiness extends BaseBusiness {
    private CategoryDAO categoryDAO;

    public CategoryBusiness(Context context) {
        super(context);
        categoryDAO = new CategoryDAO(context);
    }

    //获取所有大类的列表
    public List<Category> getNotHideRootCategory(){
        return categoryDAO.getCategorys(" and parentId=0 and state=1");
    }

    //根据父ID获取未隐藏的子类总数
    public int getNotHideCountByParentId(int parentId){
        return categoryDAO.getCount(" and parentId="+parentId+" and state=1");
    }

    //根据父ID获取子类列表
    public List<Category> getNotHideCategoryListByParentId(int parentId){
        return categoryDAO.getCategorys(" and parentId="+parentId+" and state=1");
    }

    //获取未隐藏的类别总数
    public int getNotHideCount(){
        return categoryDAO.getCount(" and state=1");
    }

    public ArrayAdapter<Category> getRootCategoryArrayAdapter(){
        List<Category> list = getNotHideRootCategory();
        list.add(0, new Category(0, context.getString(R.string.spinner_please_choose)));
        ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<Category>(context,
                R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    public boolean insertCategory(Category category){
        categoryDAO.beginTransaction();
        try {
            boolean result = categoryDAO.insertCategory(category);
            boolean result2 = true;
            Category parentCategory = getCategoryByCategoryId(
                    category.getParentId());
            String path;
            if (parentCategory != null){//如果有父类别
                //路径 = 父类别的路径 + 当前类别的路径
                path = parentCategory.getPath() +
                        category.getCategoryId() + ".";
            }else {//说明当前类就是父类别
                path = category.getCategoryId() + ".";
            }
            category.setPath(path);
            result2 = editCategory(category);
            if (result && result2){
                categoryDAO.setTransactionSuccessful();
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            categoryDAO.endTransaction();
        }
    }

    public boolean editCategory(Category category){
        String condition = " categoryId="+category.getCategoryId();
        boolean result = categoryDAO.updateCategory(condition, category);
        return result;
    }

    public boolean updateCategory(Category category){
        categoryDAO.beginTransaction();
        try {
            boolean result = editCategory(category);
            boolean result2 = true;
            Category parentCategory = getCategoryByCategoryId(category.getParentId());
            String path;
            if(parentCategory!=null){//有父类别
                //父类别的路径+当前类别的路径
                path = parentCategory.getPath()+category.getCategoryId()+".";
            }else{//没有父类别，说明当前类就是父类别
                path = category.getCategoryId()+".";
            }
            category.setPath(path);
            result2 = editCategory(category);
            if(result && result2){
                categoryDAO.setTransactionSuccessful();
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            categoryDAO.endTransaction();
        }
    }

    //通过路径隐藏类别
    public boolean hideCategoryByPath(String path) {
        //2.    2.3.    2.3.6.
        // path like '2.%'
        String condition = " path like '" + path + "%'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        boolean result = categoryDAO.updateCategory(condition,
                contentValues);
        return result;
    }

    public Category getCategoryByCategoryId(int categoryId){
        List<Category> list = categoryDAO.getCategorys(" and categoryId="+categoryId);
        if(list != null && list.size()==1){
            return list.get(0);
        }else{
            return null;
        }
    }
}
