# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#指定代码的压缩级别
-optimizationpasses 5
#混淆时是否要记录日志
-verbose
#忽略警告
-ignorewarning

-keep class com.phoenix.readily.database.dao.AccountBookDAO {
    public void onCreate(android.database.sqlite.SQLiteDatabase);
}

-keep class com.phoenix.readily.database.dao.CategoryDAO {
	public final void onCreate(android.database.sqlite.SQLiteDatabase);
}

-keep class com.phoenix.readily.database.dao.CreateViewDAO {
	*;
}

-keep class com.phoenix.readily.database.dao.PayoutDAO {
	public final void onCreate(android.database.sqlite.SQLiteDatabase);
}

-keep class com.phoenix.readily.database.dao.UserDAO {
	public final void onCreate(android.database.sqlite.SQLiteDatabase);
}

-keep class org.achartengine.**{*;}

#assumenosideeffects表示忽略它的作用
-assumenosideeffects class android.util.Log{
	public static int v(java.lang.String,java.lang.String);
	public static int d(java.lang.String,java.lang.String);
	public static int i(java.lang.String,java.lang.String);
}