package com.phoenix.readily.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 消费/支出
 */

public class Payout {
    private int payoutId;
    private int accountBookId;//账本ID外键
    private String accountBookName;//账本名称
    private int categoryId;//类别ID外键
    private String categoryName;//类别名称
    private String path;//类别路径
    private BigDecimal amount;//消费金额
    private Date payoutDate = new Date();//消费日期
    private String payoutType;//计算方式
    private String payoutUserId;//消费人ID外键
    private String comment;//备注
    private Date createDate = new Date();//添加日期
    private int state = 1;//状态：0失效，1启用，默认启用

    public int getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(int payoutId) {
        this.payoutId = payoutId;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(Date payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getPayoutType() {
        return payoutType;
    }

    public void setPayoutType(String payoutType) {
        this.payoutType = payoutType;
    }

    public String getPayoutUserId() {
        return payoutUserId;
    }

    public void setPayoutUserId(String payoutUserId) {
        this.payoutUserId = payoutUserId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
