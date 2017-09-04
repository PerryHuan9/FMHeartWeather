package com.vip.fmheartweather.db;

import org.litepal.crud.DataSupport;

/**
 *
 */

public class City extends DataSupport {
	private int id;
	private String cityName;
	private int cityCode;
	private int provinceCode;

	public int getCityCode() {
		return cityCode;
	}

	public void setCityCode(int cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(int provinceCode) {
		this.provinceCode = provinceCode;
	}
}
