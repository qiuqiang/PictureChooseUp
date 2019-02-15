package com.example.picture.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

public class SharedPreferencesUtil {
	private SharedPreferences sp;
	private Editor editor;
	Context context;
	private final static String SP_NAME = "mydata";
	private final static int MODE = Context.MODE_PRIVATE;

	public SharedPreferencesUtil(Context context) {
		this.context=context;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			sp = context.getSharedPreferences(SP_NAME, MODE);
			editor = sp.edit();
		}else {
			Log.e("存储卡提示","没有准备好存储卡");
		}
	}

	public boolean save(String key, String value) {
		editor.putString(key, value);
		return editor.commit();
	}

	public String read(String key) {
		return  sp.getString(key, "");
	}
}
