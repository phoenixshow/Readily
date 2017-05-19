package com.phoenix.readily.business;

import android.content.ContentValues;
import android.content.Context;

import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.dao.AccountBookDAO;
import com.phoenix.readily.entity.AccountBook;

import java.util.List;

/**
 * Created by flashing on 2017/5/16.
 */

public class AccountBookBusiness extends BaseBusiness {
    private AccountBookDAO accountBookDAO;

    public AccountBookBusiness(Context context) {
        super(context);
        accountBookDAO = new AccountBookDAO(context);
    }

    public boolean insertOrUpdateAccountBook(AccountBook accountBook, boolean isInsert){
        //开启事务
        accountBookDAO.beginTransaction();
        boolean result;
        try {
            if (isInsert) {
                //进行插入操作
                result = accountBookDAO.insertAccountBook(accountBook);
            }else {
                String condition = " accountBookId="+accountBook.getAccountBookId();
                result = accountBookDAO.updateAccountBook(condition, accountBook);
            }
            boolean result2 = true;
            if (accountBook.getIsDefault() == 1 && result){
                result2 = setIsDefault(accountBook.getAccountBookId());
            }
            if (result && result2){
                accountBookDAO.setTransactionSuccessful();
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            accountBookDAO.endTransaction();
        }
    }

    public boolean insertAccountBook(AccountBook accountBook){
        return insertOrUpdateAccountBook(accountBook, true);
    }

    public boolean setIsDefault(int accountBookId) throws Exception{
        //先将默认账本设为非默认
        String condition = " isDefault=1";
        ContentValues contentValues = new ContentValues();
        contentValues.put("isDefault", 0);
        boolean result = accountBookDAO.updateAccountBook(condition, contentValues);

        //再将传入ID的账本设为默认账本
        condition = " accountBookId="+accountBookId;
        contentValues.clear();
        contentValues.put("isDefault", 1);
        boolean result2 = accountBookDAO.updateAccountBook(condition, contentValues);
        if (result && result2){
            return true;
        }else {
            return false;
        }
    }

//    public boolean deleteAccountBookByAccountBookId(int accountBookId){
//        String condition = " and accountBookId="+accountBookId;
//        boolean result = accountBookDAO.deleteAccountBook(condition);
//        return result;
//    }

    public boolean updateAccountBook(AccountBook accountBook){
        return insertOrUpdateAccountBook(accountBook, false);
    }

    public List<AccountBook> getAccountBook(String condition){
        return accountBookDAO.getAccountBook(condition);
    }

    public AccountBook getAccountBookByAccountBookId(int accountBookId){
        List<AccountBook> list = accountBookDAO.getAccountBook(" and accountBookId="+accountBookId);
        if (list != null && list.size() == 1){
            return list.get(0);
        }else {
            return null;
        }
    }

    //获取未删除的用户
    public List<AccountBook> getNotHideAccountBook(){
        return accountBookDAO.getAccountBook(" and state=1");
    }

    //根据用户名称查询用户是否存在
    public boolean isExistAccountBookByAccountBookName(String accountBookName, Integer accountBookId){
        String condition = " and accountBookName='"+accountBookName+"'";
        if (accountBookId != null){
            condition += " and accountBookId <> "+accountBookId;
        }
        List<AccountBook> list = accountBookDAO.getAccountBook(condition);
        if (list != null && list.size() > 0){
            return true;
        }else {
            return false;
        }
    }

    //根据用户ID隐藏用户
    public boolean hideAccountBookByAccountBookId(int accountBookId){
        String condition = " accountBookId="+accountBookId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return accountBookDAO.updateAccountBook(condition, contentValues);
    }
}
