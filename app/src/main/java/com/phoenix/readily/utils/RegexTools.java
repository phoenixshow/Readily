package com.phoenix.readily.utils;

public class RegexTools {
	public final static String chinese_english_num = "[a-zA-Z0-9\u4e00-\u9fa5]+";
	//判断是否是正整数，并且不能超过小数点后两位
	public final static String money = "[\\d]+|[\\d]+[.]{1}[0-9]{1,2}|[\\d]+[.]{1}[0-9]{1}[0-9]{1}";

	public static boolean isChineseEnglishNum(String value){
		return value.matches(chinese_english_num);
	}

	public static boolean isMoney(String value){
		return value.matches(money);
	}

	public static boolean isNull(Object object){
		if(object == null){
			return true;
		}else{
			return false;
		}
	}
}
