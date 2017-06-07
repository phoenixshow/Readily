package com.phoenix.readily.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.phoenix.readily.R;

import java.lang.reflect.Field;

/**
 * Created by flashing on 2017/5/12.
 */

public class BaseActivity extends Activity {
    private ProgressDialog progressDialog;

    protected void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showMsg(int resId){
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    protected void openActivity(Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    protected LayoutInflater getInflater(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        return layoutInflater;
    }

    protected void setAlertDialogIsClose(DialogInterface dialog, boolean isClose){
        try {
            //获取对话框类的父类，再获取父类声明的字段mShowing
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            //由于mShowing是私有变量，需要设置访问权限为true
            field.setAccessible(true);
            //控制开启或关闭
            field.set(dialog, isClose);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected AlertDialog showAlertDialog(int titleResId, String msg,
                                          DialogInterface.OnClickListener clickListener){
        String title = getResources().getString(titleResId);
        return showAlertDialog(title, msg, clickListener);
    }

    protected AlertDialog showAlertDialog(String title, String msg,
                                          DialogInterface.OnClickListener clickListener){
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.button_text_yes, clickListener)
                .setNegativeButton(R.string.button_text_no, null)
                .show();
    }

    protected void showProgressDialog(int titleResId, int msgResId){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(titleResId);
        progressDialog.setMessage(getString(msgResId));
        progressDialog.show();
    }

    protected void dismissProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
