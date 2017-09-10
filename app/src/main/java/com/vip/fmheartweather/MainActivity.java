package com.vip.fmheartweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vip.fmheartweather.Gson.Forecast;
import com.vip.fmheartweather.Gson.Weather;
import com.vip.fmheartweather.service.WeatherUpdateService;
import com.vip.fmheartweather.util.LogUtil;
import com.vip.fmheartweather.util.MyUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
	public static final String WEB_URL = "weburl";
	public final String READ_URL = "http://m.b5200.net/";
	public final String MUSIC_URL = "https://m.y.qq.com/";
	public final String GAME_URL = "https://mobile.baidu" +
		   ".com/search?w=%E6%B8%B8%E6%88%8F&source=" +
		   "aladdin@wise_app_rank@game@more&ala" +
		   "=wise_app_rank@%E6%B8%B8%E6%88%8F&from=1015530d";
	public static final String WEB_TITLE = "webtitle";
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
	private ScrollView scrollView;
	private String weatherId;
	private NavigationView navView;


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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherInfo = preferences.getString(WEATHER_INFO, null);
		if (weatherInfo != null) {
			Weather weather = MyUtils.handleWeatherResponse(weatherInfo);
			weatherId = weather.basic.weatherId;
			showWeatherInfo(weather);
		} else {
			weatherId = "CN101010100";
			requestWeatherInfo(weatherId);
		}
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshLayout.setRefreshing(true);
				requestWeatherInfo(weatherId);
			}
		});
		navView.setNavigationItemSelectedListener(new NavigationView
			   .OnNavigationItemSelectedListener() {

			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Intent intent = new Intent(MainActivity.this, WebActivity.class);
				switch (item.getItemId()) {
					case R.id.read:
						intent.putExtra(WEB_URL, READ_URL);
						intent.putExtra(WEB_TITLE,"阅读");
						break;
					case R.id.game:
						intent.putExtra(WEB_URL, GAME_URL);
						intent.putExtra(WEB_TITLE,"游戏");
						break;
					case R.id.music:
						intent.putExtra(WEB_URL, MUSIC_URL);
						intent.putExtra(WEB_TITLE,"音乐");
						break;
					case R.id.settings:
						break;

				}
				startActivity(intent);
				drawerLayout.closeDrawers();
				return false;
			}
		});
	}

	private void initAllView() {
		forecastLayout = (LinearLayout) findViewById(R.id.forecast);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
		backgroundView = (ImageView) findViewById(R.id.bg);
		titleView = (TextView) findViewById(R.id.myTitle);
		tempView = (TextView) findViewById(R.id.temperature);
		infoView = (TextView) findViewById(R.id.weather_info);
		aqiView = (TextView) findViewById(R.id.aqi_text);
		pm25View = (TextView) findViewById(R.id.aqi_text);
		confortView = (TextView) findViewById(R.id.confort_text);
		carWashView = (TextView) findViewById(R.id.carwash_text);
		sportView = (TextView) findViewById(R.id.sport_text);
		scrollView = (ScrollView) findViewById(R.id.scrollV);
		navView = (NavigationView) findViewById(R.id.navigation_view);
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
			weatherId = data.getStringExtra(LocationActivity.WEATHER_ID);
			LogUtil.d("tag", "Beijin weather ID:" + weatherId);
			requestWeatherInfo(weatherId);
		}

	}

	private void requestWeatherInfo(final String weatherId) {
		scrollView.setVisibility(View.INVISIBLE);
		loadBingPic();
		final String weatherAddress = "http://guolin.tech/api/weather?cityid=" + weatherId +
			   "&key=bc0418b57b2d4918819d3974ac1285d9";
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
				LogUtil.d("tag", "response data:" + weatherInfo);
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

	}

	private void showWeatherInfo(Weather weather) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String bingPic = preferences.getString(BING_PIC, null);
		if (null != bingPic) {
			Glide.with(this).load(bingPic).into(backgroundView);
		}
		LogUtil.d("tag", "start show view：" + weather.basic.cityName);
		scrollView.smoothScrollTo(0, 0);
		titleView.setText(weather.basic.cityName);
		String temperature = weather.now.temperature + "℃";
		tempView.setText(temperature);
		infoView.setText(weather.now.more.info);
		forecastLayout.removeAllViews();
		LogUtil.d("tag", "forecast size:" + weather.forecastList.size());
		for (Forecast forecast : weather.forecastList) {
			View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
				   forecastLayout, false);
			TextView dateView = view.findViewById(R.id.date_text);
			TextView infoView = view.findViewById(R.id.info_text);
			TextView maxView = view.findViewById(R.id.max_text);
			TextView minView = view.findViewById(R.id.min_text);
			dateView.setText(forecast.date);
			infoView.setText(forecast.more.info);
			maxView.setText(forecast.temperature.max);
			minView.setText(forecast.temperature.min);
			forecastLayout.addView(view);
		}
		if (weather.aqi != null) {
			aqiView.setText(weather.aqi.aqiCity.aqi);
			pm25View.setText(weather.aqi.aqiCity.pm25);
		}
		String comfort = "舒适度：" + weather.suggestion.comfort.info;
		String carWash = "洗车指数：" + weather.suggestion.carWash.info;
		String sport = "运动建议：" + weather.suggestion.sport.info;
		confortView.setText(comfort);
		carWashView.setText(carWash);
		sportView.setText(sport);
		scrollView.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, WeatherUpdateService.class);
		startService(intent);

	}

	/**
	 * 加载每日一图
	 */
	private void loadBingPic() {
		String address = "http://guolin.tech/api/bing_pic";
		MyUtils.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String bingpic = response.body().string();
				SharedPreferences.Editor editor = PreferenceManager
					   .getDefaultSharedPreferences(MainActivity.this).edit();
				editor.putString(BING_PIC, bingpic);
				editor.apply();
				LogUtil.d("tag", "已完成图片的加载:" + bingpic);
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
