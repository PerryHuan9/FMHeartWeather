package com.vip.fmheartweather.db;

import org.litepal.crud.DataSupport;

/**
 *
 */

public class County extends DataSupport {
	private int id;
	private String countyName;
	private int cityCode;
	private String weatherId;

	public int getCityCode() {
		return cityCode;
	}

	public void setCityCode(int cityCode) {
		this.cityCode = cityCode;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWeatherId() {
		return weatherId;
	}

	public void setWeatherId(String weatherId) {
		this.weatherId = weatherId;
	}
}
