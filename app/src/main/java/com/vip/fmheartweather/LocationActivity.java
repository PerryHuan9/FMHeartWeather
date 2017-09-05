package com.vip.fmheartweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.vip.fmheartweather.db.Province;
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
	private Spinner provinceSpinner, citySpinner, countySpinner;
	private ArrayList<String> provinceStrList = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		Toolbar toolbar = (Toolbar) findViewById(R.id.loc_toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		provinceSpinner = (Spinner) findViewById(R.id.province);
		citySpinner = (Spinner) findViewById(R.id.city);
		countySpinner = (Spinner) findViewById(R.id.county);
		provinceSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout
			   .simple_spinner_item,
			   provinceStrList));
		initProvinceList();
		provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});


	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void initProvinceList() {
		List<Province> provinceList = DataSupport.findAll(Province.class);
		if (provinceList.size() > 0) {
			for (Province province : provinceList) {
				provinceStrList.add(province.getProvinceName());
			}
		}else {

		}

	}

	private void initFromService(String address,final String what){
		MyUtils.sendOkHttpRequest(address, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if(what.equals("province")){

				}
			}
		});

	}




}
