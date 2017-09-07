package com.vip.fmheartweather.util;

import android.util.Log;

/**
 *
 */

public class LogUtil {
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	private static final int ASSERT = 6;
	private static int level = DEBUG;

	public static void v(String tag, String str) {
		if (level <= VERBOSE) {
			Log.v(tag, str);
		}
	}

	public static void d(String tag, String str) {
		if (level <= DEBUG) {
			Log.v(tag, str);
		}
	}

	public static void i(String tag, String str) {
		if (level <= INFO) {
			Log.i(tag, str);
		}
	}


	public static void w(String tag, String str) {
		if (level <= WARN) {
			Log.i(tag, str);
		}
	}

	public static void e(String tag, String str) {
		if (level <= ERROR) {
			Log.i(tag, str);
		}
	}

}
