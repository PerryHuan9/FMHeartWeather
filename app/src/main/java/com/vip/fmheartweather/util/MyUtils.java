package com.vip.fmheartweather.util;

import android.text.TextUtils;

import com.vip.fmheartweather.db.City;
import com.vip.fmheartweather.db.County;
import com.vip.fmheartweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *工具类
 */

public class MyUtils {
	public static void sendOkHttpRequest(String url, Callback callback) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		client.newCall(request).enqueue(callback);
	}

	public static boolean handleProvinceResponse(String response) {
		try {
			if (!TextUtils.isEmpty(response)) {
				JSONArray jsonArray = new JSONArray(response);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Province province = new Province();
					province.setProvinceName(jsonObject.getString("name"));
					province.setProvinceCode(jsonObject.getInt("id"));
					province.save();
				}
				LogUtil.w("tag", "已经保存成功");
				return true;
			}
		} catch (JSONException e) {
			LogUtil.w("tag", "出错了");
			e.printStackTrace();
		}
		return false;
	}

	public static boolean handleCityResponse(String address, int provinceCode) {
		try {
			if (!TextUtils.isEmpty(address)) {
				JSONArray cityJson = new JSONArray(address);
				for (int i = 0; i < cityJson.length(); i++) {
					JSONObject jsonObject = cityJson.getJSONObject(i);
					City city = new City();
					city.setProvinceCode(provinceCode);
					city.setCityCode(jsonObject.getInt("id"));
					city.setCityName(jsonObject.getString("name"));
					city.save();
				}
				return true;

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean handleCountyResponse(String address, int cityCode) {
		try {
			if (!TextUtils.isEmpty(address)) {
				JSONArray jsonArray = new JSONArray(address);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					County county = new County();
					county.setCountyName(jsonObject.getString("name"));
					county.setWeatherId(jsonObject.getString("weather_id"));
					county.setCityCode(cityCode);
					county.save();
				}
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}


}
