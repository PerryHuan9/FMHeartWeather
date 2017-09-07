package com.vip.fmheartweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.vip.fmheartweather.db.City;
import com.vip.fmheartweather.db.County;
import com.vip.fmheartweather.db.Province;
import com.vip.fmheartweather.util.LogUtil;
import com.vip.fmheartweather.util.MyUtils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */

public class LocationActivity extends AppCompatActivity {
	public static final String SELECT_COUNTY = "selectCounty";
	public static final String WEATHER_ID = "weatherId";
	private Spinner provinceSpinner, citySpinner, countySpinner;
	private List<String> provinceStrList = new ArrayList<>();
	private List<String> cityStrList = new ArrayList<>();
	private List<String> countySrtList = new ArrayList<>();
	private ArrayAdapter<String> provinceAdapter;
	private ArrayAdapter<String> cityAdapter;
	private ArrayAdapter<String> countyAdapter;
	private ProgressDialog progressDialog;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private int provinceId, cityId;
	private String selectCounty, weatherId;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		Toolbar toolbar = (Toolbar) findViewById(R.id.loc_toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		provinceSpinner = (Spinner) findViewById(R.id.province);
		citySpinner = (Spinner) findViewById(R.id.city);
		countySpinner = (Spinner) findViewById(R.id.county);
		provinceAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
			   provinceStrList);
		cityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cityStrList);
		countyAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
			   countySrtList);
		provinceSpinner.setAdapter(provinceAdapter);
		citySpinner.setAdapter(cityAdapter);
		countySpinner.setAdapter(countyAdapter);
		provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				provinceId = provinceList.get(i).getProvinceCode();
				initCityStrList();
				initCountyStrList();

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				cityId = cityList.get(i).getCityCode();
				initCountyStrList();

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				County county = countyList.get(i);
				selectCounty = county.getCountyName();
				weatherId = county.getWeatherId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		initProvinceStrList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loca_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.set_city:
				Intent intent = new Intent();
				intent.putExtra(SELECT_COUNTY, selectCounty);
				intent.putExtra(WEATHER_ID, weatherId);
				setResult(RESULT_OK, intent);
				finish();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void initProvinceStrList() {
		provinceList = DataSupport.findAll(Province.class);

		if (provinceList.size() > 0) {
			provinceStrList.clear();
			for (Province province : provinceList) {
				provinceStrList.add(province.getProvinceName());
			}
			provinceAdapter.notifyDataSetChanged();
			provinceSpinner.setSelection(0);
		} else {
			String adress = "http://guolin.tech/api/china";
			initFromService(adress, "province");
		}

	}

	private void initCityStrList() {
		cityList = DataSupport.where("provinceCode=?", String.valueOf(provinceId)).find(City
			   .class);
		if (cityList.size() > 0) {
			cityStrList.clear();
			for (City city : cityList) {
				cityStrList.add(city.getCityName());
			}
			cityAdapter.notifyDataSetChanged();
			citySpinner.setSelection(0);
			cityId = cityList.get(0).getCityCode();
		} else {
			String address = "http://guolin.tech/api/china/" + provinceId;
			initFromService(address, "city");
		}
	}

	private void initCountyStrList() {
		countyList = DataSupport.where("cityCode=?", String.valueOf(cityId)).find(County.class);
		if (countyList.size() > 0) {
			countySrtList.clear();
			for (County county : countyList) {
				countySrtList.add(county.getCountyName());
			}
			countyAdapter.notifyDataSetChanged();
			countySpinner.setSelection(0);
		} else {
			String address = "http://guolin.tech/api/china/" + provinceId + "/" + cityId;
			initFromService(address, "county");
		}

	}

	private void initFromService(final String address, final String what) {
		LogUtil.v("tag", "address:" + address);
		showProgressDialog();
		MyUtils.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				closeProgressDialoh();
				Toast.makeText(getApplicationContext(), "加载失败", Toast
					   .LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String text = response.body().string();
				LogUtil.v("tag", "text:" + text);
				boolean result = false;
				if ("province".equals(what)) {
					result = MyUtils.handleProvinceResponse(text);
				} else if ("city".equals(what)) {
					result = MyUtils.handleCityResponse(text, provinceId);
				} else if ("county".equals(what)) {
					result = MyUtils.handleCountyResponse(text, cityId);
				}

				if (result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialoh();
							if ("province".equals(what)) {
								initProvinceStrList();
							} else if ("city".equals(what)) {
								initCityStrList();
							} else if ("county".equals(what)) {
								initCountyStrList();
							}
						}
					});


				}
			}

		});

	}


	private void showProgressDialog() {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialoh() {
		if (null != progressDialog) {
			progressDialog.dismiss();
		}
	}


}
