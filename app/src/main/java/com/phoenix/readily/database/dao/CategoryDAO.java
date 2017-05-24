package com.phoenix.readily.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.phoenix.readily.R;
import com.phoenix.readily.database.base.SQLiteDAOBase;
import com.phoenix.readily.entity.Category;
import com.phoenix.readily.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by flashing on 2017/5/15.
 */

public class CategoryDAO extends SQLiteDAOBase {
    public CategoryDAO(Context context) {
        super(context);
    }

    @Override
    protected String[] getTableNameAndPK() {
        return new String[]{"category", "categoryId"};
    }

    @Override
    protected Object findModel(Cursor cursor) {
        Category category = new Category();
        category.setCategoryId(cursor.getInt(cursor.getColumnIndex("categoryId")));
        category.setCategoryName(cursor.getString(cursor.getColumnIndex("categoryName")));
        category.setParentId(cursor.getInt(cursor.getColumnIndex("parentId")));
        category.setPath(cursor.getString(cursor.getColumnIndex("path")));
        Date createDate = DateUtil.getDate(cursor.getString(
                cursor.getColumnIndex("createDate")), "yyyy-MM-dd HH:mm:ss");
        category.setCreateDate(createDate);
        category.setState(cursor.getInt(cursor.getColumnIndex("state")));
        return category;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder sql = new StringBuilder();
        sql.append("Create  TABLE [category](");
        sql.append("[categoryId] integer PRIMARY KEY AUTOINCREMENT NOT NULL");
        sql.append(",[categoryName] varchar(20) NOT NULL");
        sql.append(",[parentId] int NOT NULL");
        sql.append(",[path] text NOT NULL");
        sql.append(",[createDate] datetime NOT NULL");
        sql.append(",[state] int NOT NULL");

        sql.append(")");
        database.execSQL(sql.toString());

        initDefaultData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database) {
    }

    public boolean insertCategory(Category category){
        ContentValues contentValues = createParms(category);
        long newid = getDatabase().insert(getTableNameAndPK()[0], null, contentValues);
        category.setCategoryId((int) newid);
        return newid > 0;
    }

    public boolean deleteCategory(String condition){
        return delete(getTableNameAndPK()[0], condition);
    }

    public boolean updateCategory(String condition, Category category){
        ContentValues contentValues = createParms(category);
        return updateCategory(condition, contentValues);
    }

    public boolean updateCategory(String condition, ContentValues contentValues){
        return getDatabase().update(getTableNameAndPK()[0], contentValues,
                condition, null) >= 0;
    }

    public List<Category> getCategorys(String condition){
            String sql = "select * from "+getTableNameAndPK()[0]+" where 1=1 "+ condition;
            return getList(sql);
    }

    private ContentValues createParms(Category info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("categoryName", info.getCategoryName());
        contentValues.put("parentId", info.getParentId());
        contentValues.put("path", info.getPath());
        contentValues.put("createDate", DateUtil.getFormatDateTime(
                info.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put("state", info.getState());
        return contentValues;
    }

    private void initDefaultData(SQLiteDatabase database){
        Category category = new Category();
        category.setPath("");
        category.setParentId(0);
        String[] categoryName = getContext().getResources().getStringArray(
                R.array.InitDefaultCategoryName);
        for (int i = 0; i < categoryName.length; i++) {
            category.setCategoryName(categoryName[i]);
            ContentValues contentValues = createParms(category);
            long newId = database.insert(getTableNameAndPK()[0], null, contentValues);
            category.setPath(newId+".");
            contentValues = createParms(category);
            database.update(getTableNameAndPK()[0], contentValues,
                    " categoryId=?", new String[]{newId+""});
        }
    }
}
