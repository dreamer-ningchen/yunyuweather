package com.yunyuweather.activity;

import com.example.yunyuweather.R;
import com.yunyuweather.util.HttpCallbackListener;
import com.yunyuweather.util.HttpUtil;
import com.yunyuweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
    
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDesText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button chooseCity;
	private Button refreshWeather;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout  = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDesText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		chooseCity = (Button) findViewById(R.id.choose_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		//从ChooseAreaActivity获取Intent数据
		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)) {
			publishText.setText("同步中.....");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		}else {
			showWeather();
		}
		chooseCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
          switch (v.getId()) {
		case R.id.choose_city:
				Intent intent = new Intent(this, ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("加载中.....");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	
	private void queryWeatherCode(String countryCode) {
        String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
        queryFromServer(address,"countryCode");
	}
	
	private void queryWeatherInfo(String weatherCode) {
		 String address ="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		 queryFromServer(address, "weatherCode");
	}
	
	  
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
						if ("countryCode".equals(type)) {
							if (!TextUtils.isEmpty(response)) {  //判断response是否为空
							String[] array = response.split("\\|");
							if (array!=null && array.length==2) {
								String weatherCode = array[1];
								queryWeatherInfo(weatherCode);
							   }
							}  
						}else if ("weatherCode".equals(type)) {
							Utility.handleWeatherResponse(WeatherActivity.this, response);
							runOnUiThread(new Runnable() {  //回到主线程显示天气
								
								@Override
								public void run() {
 								   showWeather();
								}
							});
						}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
                        publishText.setText("同步失败");						
					}
				});
				
			}
		});
	}

	private void showWeather() {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          cityNameText.setText(prefs.getString("city_name", ""));
          temp1Text.setText(prefs.getString("temp_1", ""));
          temp2Text.setText(prefs.getString("temp_2", ""));
          publishText.setText("今天 "+prefs.getString("publish_time", "")+" 发布");
          currentDateText.setText(prefs.getString("current_time", ""));
          weatherDesText.setText(prefs.getString("weather_desp", ""));
          weatherInfoLayout.setVisibility(View.VISIBLE);
          cityNameText.setVisibility(View.VISIBLE);
          
	}
	

}
