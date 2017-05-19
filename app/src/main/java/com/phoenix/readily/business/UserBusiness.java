package com.phoenix.readily.business;

import android.content.ContentValues;
import android.content.Context;

import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.dao.UserDAO;
import com.phoenix.readily.entity.Users;

import java.util.List;

/**
 * Created by flashing on 2017/5/16.
 */

public class UserBusiness extends BaseBusiness {
    private UserDAO userDAO;

    public UserBusiness(Context context) {
        super(context);
        userDAO = new UserDAO(context);
    }

    public boolean insertUser(Users user){
        boolean result = userDAO.insertUser(user);
        return result;
    }

    public boolean deleteUserByUserId(int userId){
        String condition = " and userId="+userId;
        boolean result = userDAO.deleteUser(condition);
        return result;
    }

    public boolean updateUser(Users user){
        String condition = " userId="+user.getUserId();
        boolean result = userDAO.updateUser(condition, user);
        return result;
    }

    public List<Users> getUsers(String condition){
        return userDAO.getUsers(condition);
    }

    public Users getUserByUserId(int userId){
        List<Users> list = userDAO.getUsers(" and userId="+userId);
        if (list != null && list.size() == 1){
            return list.get(0);
        }else {
            return null;
        }
    }

    //获取未删除的用户
    public List<Users> getNotHideUser(){
        return userDAO.getUsers(" and state=1");
    }

    //根据用户名称查询用户是否存在
    public boolean isExistUserByUserName(String userName, Integer userId){
        String condition = " and userName='"+userName+"'";
        if (userId != null){
            condition += " and userId <> "+userId;
        }
        List<Users> list = userDAO.getUsers(condition);
        if (list != null && list.size() > 0){
            return true;
        }else {
            return false;
        }
    }

    //根据用户ID隐藏用户
    public boolean hideUserByUserId(int userId){
        String condition = " userId="+userId;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", 0);
        return userDAO.updateUser(condition, contentValues);
    }
}
