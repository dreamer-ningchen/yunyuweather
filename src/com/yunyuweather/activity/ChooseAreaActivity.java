package com.yunyuweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.yunyuweather.db.YunyuWeatherDB;
import com.yunyuweather.model.City;
import com.yunyuweather.model.Country;
import com.yunyuweather.model.Province;
import com.yunyuweather.util.HttpCallbackListener;
import com.yunyuweather.util.HttpUtil;
import com.yunyuweather.util.Utility;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
      public static final int LEVEL_PROVINCE = 0;
      public static final int LEVEL_CITY = 1;
      public static final int LEVEL_COUNTRY = 2;
      
      private ProgressDialog progressDialog;
      private TextView titleText;
      private ListView listView;
      private ArrayAdapter<String> adapter;
      private YunyuWeatherDB yunyuWeatherDB;
      private List<String> dataList = new ArrayList<String>();
      
      private List<Province> provinceList;
      private List<City> cityList;
      private List<Country> countryList;
      
      private Province selectedPronvince;
      private City selectedCity;
      
      private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.example.yunyuweather.R.layout.choose_area);
		listView = (ListView) findViewById(com.example.yunyuweather.R.id.list_view);
		titleText = (TextView) findViewById(com.example.yunyuweather.R.id.title_text);
		adapter = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		yunyuWeatherDB = YunyuWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                   if (currentLevel == LEVEL_PROVINCE) {
					selectedPronvince = provinceList.get(index);
					queryCities();
				}else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCountries();
				}
			}
		});
		queryProvincies();
	}

	protected void queryCities() {
         cityList = yunyuWeatherDB.loadCity(selectedPronvince.getId());
         if (cityList.size()>0) {
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedPronvince.getProvinceName());
			currentLevel = LEVEL_CITY;
         }else {
			queryFromServer(selectedPronvince.getPronvinceCode(),"city");
		}
	}

	private void queryProvincies() {
        provinceList = yunyuWeatherDB.loadProvince();
        if (provinceList.size()>0) {
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else {
			queryFromServer(null,"province");
		}
        
    
	}
      private void queryCountries(){
    	  countryList = yunyuWeatherDB.loadCountry(selectedCity.getId());
    	  if (countryList.size()>0) {
    		  dataList.clear();
			for(Country country : countryList){
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else {
			queryFromServer(selectedCity.getCityCode(),"country");
		}
      }

	private void queryFromServer(final String code, final String type) {
          String address;
          
          if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
        //showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)) {
					result  = Utility.handleProvinceResponse(yunyuWeatherDB, response);
				}else if("city".equals(type)){
                    result = Utility.handleCityResponse(yunyuWeatherDB, response, selectedPronvince.getId());
				}else if ("country".equals(type)) {
					result = Utility.handleCountryResponse(yunyuWeatherDB, response, selectedCity.getId());
				}
                if (result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvincies();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败..", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		  if(progressDialog==null){
			  progressDialog = new ProgressDialog(this);
			  progressDialog.setMessage("正在加载....");
			  progressDialog.setCanceledOnTouchOutside(false);
		  }
		  progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
      
    public void onBackPressed(){
    	if(currentLevel == LEVEL_COUNTRY){
    		queryCities();
    	}
    	if (currentLevel == LEVEL_CITY) {
			queryProvincies();
		}else {
			finish();
		}
    }
          
      
}
