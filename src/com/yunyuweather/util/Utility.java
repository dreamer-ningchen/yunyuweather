package com.yunyuweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.yunyuweather.db.YunyuWeatherDB;
import com.yunyuweather.model.City;
import com.yunyuweather.model.Country;
import com.yunyuweather.model.Province;

import android.R.bool;
import android.R.id;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
     
	//解析和处理服务器返回的省份信息
	public static synchronized boolean handleProvinceResponse(YunyuWeatherDB yunyuWeatherDB , String response){
		
		if (!TextUtils.isEmpty(response)) {
			String[] allProvincies = response.split(",");
			if (allProvincies!=null&& allProvincies.length>0) {
				for (String p : allProvincies) {
					Province province = new Province();
					String[] array = p.split("\\|");
					province.setPronvinceCode(array[0]);
					province.setProvinceName(array[1]);
					yunyuWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//解析和处理服务器返回的城市信息
		public static synchronized boolean handleCityResponse(YunyuWeatherDB yunyuWeatherDB , 
				String response,int provinceId){
			
			if (!TextUtils.isEmpty(response)) {
				String[] allCities = response.split(",");
				if (allCities!=null&& allCities.length>0) {
					for (String c : allCities) {
						City city = new City();
						String[] array = c.split("\\|");
						city.setCityCode(array[0]);
						city.setCityName(array[1]);
						city.setProvinceId(provinceId);
						yunyuWeatherDB.saveCity(city);
					}
					return true;
				}
			}
			return false;
		}
		
		//解析处理服务器返回的城镇信息
		
		public static synchronized boolean handleCountryResponse(YunyuWeatherDB yunyuWeatherDB ,
				String response, int cityId){
			if (!TextUtils.isEmpty(response)) {
				String[] allCountries = response.split(",");
				if (allCountries!=null &&allCountries.length>0) {
					for (String c : allCountries) {
						String[] array = c.split("\\|");
						Country country = new Country();
						country.setCountryCode(array[0]);
						country.setCountryName(array[1]);
						country.setCityId(cityId);
						yunyuWeatherDB.saveCountry(country);
					}
					return true;
				}
			}
			return false;
		}
		
		public static void handleWeatherResponse(Context context,String response){
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				String cityName = weatherInfo.getString("city");
				String cityCode = weatherInfo.getString("cityid");
				String temp1 = weatherInfo.getString("temp1");
				String temp2 = weatherInfo.getString("temp2");
				String weatherDesp = weatherInfo.getString("weather");
				String publishiTime = weatherInfo.getString("ptime");
				saveWeatherInfo(context,cityName,cityCode,temp1,temp2,weatherDesp,publishiTime);
			} catch (JSONException e) {
                   e.printStackTrace();
			}
		}

		private static void saveWeatherInfo(Context context,String cityName, String cityCode, String temp1, String temp2,
				String weatherDesp, String publishiTime) {
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
			    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			    editor.putBoolean("city_selected", true);
			    editor.putString("city_name", cityName);
			    editor.putString("weather_code", cityCode);
			    editor.putString("temp_1", temp1);
			    editor.putString("temp_2", temp2);
			    editor.putString("weather_desp", weatherDesp);
			    editor.putString("publish_time", publishiTime);
			    editor.putString("current_time", sdf.format(new Date()));
			    editor.commit();
		}
		
		
}


