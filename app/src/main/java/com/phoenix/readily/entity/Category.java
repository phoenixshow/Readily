package com.phoenix.readily.entity;

import java.io.Serializable;
import java.util.Date;

import static android.R.attr.isDefault;

/**
 * 类别
 */

public class Category implements Serializable{
    private int categoryId;
    //类别名称
    private String categoryName;
    //父类型ID
    private int parentId = 0;
    //路径
    private String path;
    //添加日期
    private Date createDate = new Date();
    //状态：0失效，1启用，默认启用
    private int state = 1;

    public Category() {
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
