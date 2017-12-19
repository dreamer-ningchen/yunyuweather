package com.yunyuweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class YunyuWeatherOpenHelper extends SQLiteOpenHelper {
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
			/**
			* City表建表语句
			*/
			public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer)";
			/**
			* County表建表语句
			*/
			public static final String CREATE_COUNTRY = "create table Country ("
			+ "id integer primary key autoincrement, "
			+ "country_name text, "
			+ "country_code text, "
			+ "city_id integer)";
	public YunyuWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_COUNTRY);
        db.execSQL(CREATE_CITY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
    
}
