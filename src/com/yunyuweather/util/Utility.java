package com.yunyuweather.util;

import com.yunyuweather.db.YunyuWeatherDB;
import com.yunyuweather.model.City;
import com.yunyuweather.model.Country;
import com.yunyuweather.model.Province;

import android.R.bool;
import android.R.id;
import android.database.Cursor;
import android.text.TextUtils;

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
		
		
}


