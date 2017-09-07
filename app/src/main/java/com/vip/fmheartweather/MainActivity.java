package com.vip.fmheartweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vip.fmheartweather.Gson.Weather;
import com.vip.fmheartweather.util.MyUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
	public static final String WEATHER_INFO = "WEATHER";
	public static final String BING_PIC = "BINGPIC";
	public static final int REQUEST_CODE = 1;
	private DrawerLayout drawerLayout;
	private LinearLayout forecastLayout;
	private TextView titleView;
	private TextView tempView;
	private TextView infoView;
	private TextView aqiView;
	private TextView pm25View;
	private TextView confortView;
	private TextView carWashView;
	private TextView sportView;
	private ImageView backgroundView;
	private SwipeRefreshLayout refreshLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (null != actionBar) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
		}
		initAllView();

	}

	private void initAllView() {
		forecastLayout = (LinearLayout) findViewById(R.id.forecast);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
		backgroundView = (ImageView) findViewById(R.id.bg);
		titleView = (TextView) findViewById(R.id.myTitle);
		tempView = (TextView) findViewById(R.id.temperature);
		infoView = (TextView) findViewById(R.id.info_text);
		aqiView = (TextView) findViewById(R.id.aqi_text);
		pm25View = (TextView) findViewById(R.id.aqi_text);
		confortView = (TextView) findViewById(R.id.confort_text);
		carWashView = (TextView) findViewById(R.id.carwash_text);
		sportView = (TextView) findViewById(R.id.sport_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ic_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				break;
			case R.id.menu_location:
				startActivityForResult(new Intent(this, LocationActivity.class), REQUEST_CODE);
				break;

		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

		}

	}

	private void requestWeatherInfo(final String weatherId) {
		String weatherAddress = "http://guolin.tech/api/weather?cityid=" + weatherId +
			   "0cb1f9b9c392400c86be4dad4d70147f";
		MyUtils.sendOkHttpRequest(weatherAddress, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "加载天气信息失败", Toast.LENGTH_SHORT)
							   .show();
						refreshLayout.setRefreshing(false);
					}
				});
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String weatherInfo = response.body().string();
				final Weather weather = MyUtils.handleWeatherResponse(weatherInfo);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (weather != null && "ok".equals(weather.status)) {
							SharedPreferences.Editor editor = PreferenceManager
								   .getDefaultSharedPreferences(MainActivity.this).edit();
							editor.putString(WEATHER_INFO, weatherInfo);
							editor.apply();
							showWeatherInfo(weather);
						} else {
							Toast.makeText(MainActivity.this, "获取天气信息失败", Toast
								   .LENGTH_SHORT).show();
						}
						refreshLayout.setRefreshing(false);

					}
				});
			}
		});
		loadBingPic();
	}

	private void showWeatherInfo(Weather weather) {

	}

	/**
	 * 加载每日一图
	 */
	private void loadBingPic() {
		String address = "http://guolin.tech/aqi/bing_pic";
		MyUtils.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String bingpic = response.body().string();
				SharedPreferences.Editor editor = PreferenceManager
					   .getDefaultSharedPreferences(MainActivity.this).edit();
				editor.putString(BING_PIC, bingpic);
				editor.apply();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Glide.with(MainActivity.this).load(bingpic).into(backgroundView);
					}
				});

			}
		});

	}


}
