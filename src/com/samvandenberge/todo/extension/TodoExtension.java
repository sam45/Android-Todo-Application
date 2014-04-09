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

import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.mindmeapp.extensions.ExtensionData;
import com.mindmeapp.extensions.MindMeExtension;
import com.samvandenberge.todo.R;
import com.samvandenberge.todo.model.Todo;
import com.samvandenberge.todo.sqlite.DatabaseTodo;

public class TodoExtension extends MindMeExtension {
	public static final String ACTION_UPDATE_ALARMPAD = "action_update_alarmpad";
	public static final String PREF_SPEAK_BEFORE = "pref_speak_before";
	public static final String PREF_COUNT_ONLY = "pref_count_only";

	private List<Todo> todoItems = null;
	private AlarmChangedReceiver mAlarmChangedReceiver;

	public ExtensionData getData() {
		// Get preference value for text to speak before the quote
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String name = sp.getString(PREF_SPEAK_BEFORE, getString(R.string.pref_speak_before_default)) + " ";
		boolean noRemoteView = sp.getBoolean(PREF_COUNT_ONLY, false);
		// Get the todo items 
		DatabaseTodo db = DatabaseTodo.getInstance(getApplicationContext());
		todoItems = db.getNonCompletedTodos();

		// data to show
		ExtensionData data = new ExtensionData().visible(true).icon(R.drawable.ic_alarmpad_extension)
				.languageToSpeak(Locale.US);
		if (todoItems != null && todoItems.size() > 0) {
			if (todoItems.size() == 1) {
				data.statusToDisplay(todoItems.size() + " Todo");
				data.statusToSpeak(name + " " + todoItems.size() + " task.")
					.contentDescription("You have " + todoItems.size() + " task.");
			} else {
				data.statusToDisplay(todoItems.size() + " Todo\'s");
				data.statusToSpeak(name + " " + todoItems.size() + " tasks.")
					.contentDescription("You have " + todoItems.size() + " tasks.");
			}

			
			if (!noRemoteView) {
				RemoteViews main = showTasks();
				data.viewsToDisplay(main);
			}
		} else {
			// Publish the extension data update
			data.statusToDisplay("No Todo\'s").statusToSpeak(name + " no tasks.")
					.contentDescription("You have no tasks.");
		}
		return data;
	}

	@Override
	protected void onUpdateData(int reason) {
		publishUpdate(getData());
	}

	/**
	 * Show the tasks in a remote view
	 * 
	 * @param pendingIntent
	 * @return
	 */
	private RemoteViews showTasks() {
		RemoteViews main = new RemoteViews(this.getPackageName(), R.layout.remoteview_parent);
		for (Todo item : todoItems) {
			RemoteViews newremoteview = new RemoteViews(this.getPackageName(), R.layout.remoteview_item);
			newremoteview.setTextViewText(R.id.remoteTextview, item.getNote());
			main.addView(R.id.remoteParent, newremoteview);

			Intent intent = new Intent(this, com.samvandenberge.todo.MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			newremoteview.setOnClickPendingIntent(R.id.remoteTextview, activity);
		}

		return main;
	}

	/**
	 * Manually refresh the data for the extension
	 * 
	 * @author Sam
	 * 
	 */
	class AlarmChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			publishUpdate(getData());
		}
	}

	@Override
	protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);
		if (mAlarmChangedReceiver != null) {
			try {
				unregisterReceiver(mAlarmChangedReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_ALARMPAD);
		mAlarmChangedReceiver = new AlarmChangedReceiver();
		registerReceiver(mAlarmChangedReceiver, intentFilter);
	}
}