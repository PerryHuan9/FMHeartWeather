package com.vip.fmheartweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.vip.fmheartweather.Gson.Weather;
import com.vip.fmheartweather.MainActivity;
import com.vip.fmheartweather.util.LogUtil;
import com.vip.fmheartweather.util.MyUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */

public class WeatherUpdateService extends Service {
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		updateBingPic();
		updateWeatherInfo();
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 *60*60*1000;
		long tirggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, WeatherUpdateService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		alarmManager.cancel(pi);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, tirggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateBingPic() {
		String address = "http://guolin.tech/api/bing_pic";
		MyUtils.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String bingPic = response.body().string();
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
					   (getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(MainActivity.BING_PIC, bingPic);
				editor.apply();
				LogUtil.d("tag", "update bg picture OK");
			}
		});

	}

	private void updateWeatherInfo() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
			   (getApplicationContext());
		String weatherInfo = preferences.getString(MainActivity.WEATHER_INFO, null);
		if (weatherInfo != null) {
			final Weather weather = MyUtils.handleWeatherResponse(weatherInfo);
			String weatherId = weather.basic.weatherId;
			String address = "http://guolin.tech/api/weather?cityid=" + weatherId +
				   "&key=bc0418b57b2d4918819d3974ac1285d9";
			MyUtils.sendOkHttpRequest(address, new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String newWeatherInfo = response.body().string();
					Weather newWeather = MyUtils.handleWeatherResponse(newWeatherInfo);
					if (weather != null && "ok".equals(newWeather.status)) {
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString(MainActivity.WEATHER_INFO, newWeatherInfo);
						editor.apply();
						LogUtil.d("tag", "update weather info Ok");
					}
				}
			});
		}


	}


}
