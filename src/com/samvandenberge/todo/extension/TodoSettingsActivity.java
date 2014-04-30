/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samvandenberge.todo.extension;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.samvandenberge.todo.R;

public class TodoSettingsActivity extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_launcher);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// statusBar tint
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// alternative for android:fitsSystemWindows="true"
			findViewById(android.R.id.list).setFitsSystemWindows(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			int actionBarColor = Color.parseColor("#5d98db");
			tintManager.setStatusBarTintColor(actionBarColor);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// TODO: if the previous activity on the stack isn't a ConfigurationActivity,
			// launch it.
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Fix for force cache refresh when updating settings
	 */
	@Override
	protected void onDestroy() {
		Log.i("TodoSettingsActivity", "Send broadcast to extension");
		sendBroadcast(new Intent(TodoExtension.ACTION_UPDATE_ALARMPAD));
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_example);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference(TodoExtension.PREF_SPEAK_BEFORE));
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return false;
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			// For all other preferences, set the summary to the value's
			// simple string representation.
			preference.setSummary(stringValue);

			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
				.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}
}
