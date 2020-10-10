package com.phonecleaner.icecleaner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StatusUtils {
	private static String MyPREFERENCES = "App_setting";

	// -------------------------SAVE--------------------------------------
	public static void save(Context context, String key, int value) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putInt(key, value);
		editor.commit();

	}

	public static void saveLong(Context context, String key, long value) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putLong(key, value);
		editor.commit();

	}

	public static void save(Context context, String key, String value) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public static void save(Context context, String key, boolean value) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	// -------------------------READ--------------------------------------
	public static String read(Context context, String key, String defValue) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		return sharedpreferences.getString(key, defValue);
	}

	public static int read(Context context, String key, int defValue) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		return sharedpreferences.getInt(key, defValue);
	}

	public static long readLong(Context context, String key, long defValue) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		return sharedpreferences.getLong(key, defValue);
	}

	public static boolean read(Context context, String key, boolean defValue) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		return sharedpreferences.getBoolean(key, defValue);
	}

	// ---------------------------REMOVE--------------------------------------------
	public static void remove(Context context, String... key) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		for (int i = 0; i < key.length; i++)
			editor.remove(key[i]);
		editor.commit();

	}

	public static void removeAll(Context context) {
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.clear();
		editor.commit();

	}

}
