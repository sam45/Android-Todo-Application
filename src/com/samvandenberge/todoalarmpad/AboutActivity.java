package com.samvandenberge.todoalarmpad;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_about);
		
		final TextView intro = (TextView)findViewById(R.id.intro);
		intro.setText(getString(R.string.app_name) + " uses the following Open Source libraries:");

		// statusBar tint
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			int actionBarColor = Color.parseColor("#5d98db");
			tintManager.setStatusBarTintColor(actionBarColor);
		}
	}
}
