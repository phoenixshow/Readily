package com.phoenix.readily.entity;

import java.util.Date;

/**
 * Created by flashing on 2017/5/15.
 */

public class AccountBook {
    private int accountBookId;
    //用户名称
    private String accountBookName;
    //添加日期
    private Date createDate = new Date();
    //状态：0失效，1启用，默认启用
    private int state = 1;
    //是否默认账本：0否，1是
    private int isDefault;

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getAccountBookId() {
        return accountBookId;
    }

    public void setAccountBookId(int accountBookId) {
        this.accountBookId = accountBookId;
    }

    public String getAccountBookName() {
        return accountBookName;
    }

    public void setAccountBookName(String accountBookName) {
        this.accountBookName = accountBookName;
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
}
