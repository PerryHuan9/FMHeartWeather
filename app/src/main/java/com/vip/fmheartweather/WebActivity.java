package com.vip.fmheartweather;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 */

public class WebActivity extends AppCompatActivity {
	private ProgressBar progressBar;
	private WebView webView;
	private TextView titleView;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		Toolbar toolbar = (Toolbar) findViewById(R.id.web_toolbar);
		toolbar.setTitle("");
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		final String url = getIntent().getStringExtra(MainActivity.WEB_URL);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				} else {
					if (progressBar.getVisibility() == View.GONE) {
						progressBar.setVisibility(View.VISIBLE);
					}
					progressBar.setProgress(newProgress);
				}

				super.onProgressChanged(view, newProgress);
			}
		});
		webView.loadUrl(url);

		titleView = (TextView) findViewById(R.id.web_title);
		titleView.setText(getIntent().getStringExtra(MainActivity.WEB_TITLE));
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (webView.canGoBack()) {
				webView.goBack();
				return true;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("是否退出？");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			});
			builder.setNeutralButton("否", null);
			builder.show();
			return true;

		}

		return super.onKeyDown(keyCode, event);
	}
}
