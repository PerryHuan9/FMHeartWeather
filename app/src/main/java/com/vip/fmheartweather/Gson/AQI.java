package com.vip.fmheartweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 *
 */

public class AQI {
	@SerializedName("city")
	public AQICity aqiCity;

	public class AQICity{
		public String aqi;
		public String pm25;
	}
}

