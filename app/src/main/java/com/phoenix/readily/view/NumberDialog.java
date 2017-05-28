package com.phoenix.readily.view;

import java.math.BigDecimal;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.phoenix.readily.R;

public class NumberDialog extends Dialog implements View.OnClickListener{
	private Context context;
	public interface OnNumberDialogListener{
		void setNumberFinish(BigDecimal number);
	}

	//添加带上下文参数的构造方法
	public NumberDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.number_dialog);
		findViewById(R.id.dot_btn).setOnClickListener(this);
		findViewById(R.id.one_btn).setOnClickListener(this);
		findViewById(R.id.two_btn).setOnClickListener(this);
		findViewById(R.id.three_btn).setOnClickListener(this);
		findViewById(R.id.four_btn).setOnClickListener(this);
		findViewById(R.id.five_btn).setOnClickListener(this);
		findViewById(R.id.six_btn).setOnClickListener(this);
		findViewById(R.id.seven_btn).setOnClickListener(this);
		findViewById(R.id.eight_btn).setOnClickListener(this);
		findViewById(R.id.nine_btn).setOnClickListener(this);
		findViewById(R.id.zero_btn).setOnClickListener(this);
		findViewById(R.id.change_btn).setOnClickListener(this);
		findViewById(R.id.ok_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		EditText display_et = (EditText)findViewById(R.id.display_et);
		String number = display_et.getText().toString();
		switch (v.getId()) {
			case R.id.dot_btn:
				if(number.indexOf(".") == -1){
					number += ".";
				}
				break;
			case R.id.one_btn:
				number += "1";
				break;
			case R.id.two_btn:
				number += "2";
				break;
			case R.id.three_btn:
				number += "3";
				break;
			case R.id.four_btn:
				number += "4";
				break;
			case R.id.five_btn:
				number += "5";
				break;
			case R.id.six_btn:
				number += "6";
				break;
			case R.id.seven_btn:
				number += "7";
				break;
			case R.id.eight_btn:
				number += "8";
				break;
			case R.id.nine_btn:
				number += "9";
				break;
			case R.id.zero_btn:
				number += "0";
				break;
			case R.id.change_btn:
				if(number.length() !=0 ){
					number = number.substring(0, number.length()-1);
				}
				break;
			case R.id.ok_btn:
				BigDecimal bigDecimal;
				if(!".".equals(number) && number.length() !=0 ){
					bigDecimal = new BigDecimal(number);
				}else{
					bigDecimal = new BigDecimal(0);
				}
				((OnNumberDialogListener)context).setNumberFinish(bigDecimal);
				dismiss();
				break;
		}
		display_et.setText(number);
	}

}
