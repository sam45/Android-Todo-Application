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

package com.samvandenberge.todoalarmpad.extension;

import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.mindmeapp.extensions.ExtensionData;
import com.mindmeapp.extensions.MindMeExtension;
import com.samvandenberge.todoalarmpad.R;
import com.samvandenberge.todoalarmpad.model.Todo;
import com.samvandenberge.todoalarmpad.sqlite.DatabaseTodo;

public class TodoExtension extends MindMeExtension {
	public static final String PREF_SPEAK_BEFORE = "pref_speak_before";
	public static final String PREF_REMINDER_ITEM = "pref_reminder_item";

	@Override
	protected void onUpdateData(int reason) {
		// Get preference value for text to speak before the quote
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String name = sp.getString(PREF_SPEAK_BEFORE, getString(R.string.pref_speak_before_default)) + " ";

		// TODO fix when there are no todo items
		// Get the todo items 
		DatabaseTodo db = DatabaseTodo.getInstance(getApplicationContext());
		List<Todo> todoItems = db.getAllTodos();

		RemoteViews main = new RemoteViews(this.getPackageName(), R.layout.testview);
		for (Todo item : todoItems) {
			RemoteViews newremoteview = new RemoteViews(this.getPackageName(), R.layout.testitem);
			newremoteview.setTextViewText(R.id.testtextview, item.getNote());
			main.addView(R.id.testview, newremoteview);
		}

		// onClick intent
//		Intent intent = new Intent(this, com.samvandenberge.todoalarmpad.MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		main.setOnClickPendingIntent(R.id.tvExtensionTodo1, activity);

		// Publish the extension data update
		publishUpdate(new ExtensionData().visible(true).icon(R.drawable.ic_launcher).statusToDisplay(todoItems.size() + " Todo\'s")
				.statusToSpeak("You have " + todoItems.size() + " tasks.").languageToSpeak(Locale.US).viewsToDisplay(main)
				.contentDescription("You have " + todoItems.size() + " tasks."));
	}
}