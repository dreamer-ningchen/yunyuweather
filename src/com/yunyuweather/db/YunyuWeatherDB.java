package com.yunyuweather.db;

import java.util.ArrayList;
import java.util.List;

import com.yunyuweather.model.City;
import com.yunyuweather.model.Country;
import com.yunyuweather.model.Province;

import android.R.id;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AutoCompleteTextView.Validator;

public class YunyuWeatherDB {
     public static final String DB_NAME="yunyu_weather";
     public static final int VERSION = 1;
     private static YunyuWeatherDB yunyuWeatherDB;
     private SQLiteDatabase db;
     
     private YunyuWeatherDB (Context context) {
		YunyuWeatherOpenHelper yunyuWeatherOpenHelper = new YunyuWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = yunyuWeatherOpenHelper.getWritableDatabase();
	}
     
     //获取YunyuWeather实例
     
     public synchronized static YunyuWeatherDB getInstance(Context context){
    	 if(yunyuWeatherDB==null){
    		 yunyuWeatherDB = new YunyuWeatherDB(context);
    	 }
		return yunyuWeatherDB;
     }
     
     //省份实例储存到数据库
     
     public void saveProvince(Province province){
    	 if(province!=null){
    		 ContentValues values = new ContentValues();
    		 values.put("province_name", province.getProvinceName());
    		 values.put("province_code", province.getPronvinceCode());
    		 db.insert(DB_NAME, null, values);
    	 }
    	 
     
     }
     
     //从数据库读取全国省份信息
     
     public List<Province> loadProvince(){
    	 List<Province> list = new ArrayList<Province>();
    	 Cursor cursor = db.query(DB_NAME, null, null, null, null, null, null);
    	 if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setPronvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
    	 return list;
     }
     
     //储存城市信息
     
     public void saveCity(City city){
    	 if (city!=null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert(DB_NAME, null, values);
		}
     }
     
     //从数据库读取城市信息
     
     public List<City> loadCity(int provinceId){
    	 List<City> list = new ArrayList<City>();
    	 Cursor cursor = db.query(DB_NAME, null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
    	 if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				list.add(city);
			} while (cursor.moveToNext());									
		}
    	 return list;
     }
     
     //储存城镇实例到数据库
     
     public void saveCountry(Country country){
    	 if (country!=null) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert(DB_NAME, null, values);
		}
     }
     
     //从数据库获取城镇实例
     
     public List<Country> loadCountry(int cityId){
    	 List<Country> list = new ArrayList<Country>();
    	 Cursor cursor = db.query(DB_NAME, null, "city_id = ?", new String[]{String.valueOf("cityId")}, null, null, null);
    	 if (cursor.moveToFirst()) {
			do {
			   Country country = new Country();
			   country.setId(cursor.getInt(cursor.getColumnIndex("id")));
			   country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
			   country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
               country.setCityId(cityId);
			   list.add(country);
			} while (cursor.moveToNext());
		}
    	 return list;
     }
}