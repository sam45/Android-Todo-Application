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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.mindmeapp.extensions.ExtensionData;
import com.mindmeapp.extensions.MindMeExtension;
import com.samvandenberge.todoalarmpad.R;
import com.samvandenberge.todoalarmpad.data.DatabaseTodo;
import com.samvandenberge.todoalarmpad.data.Todo;

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

		// RemoteView
        RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.extension_remoteview);
        rv.setTextViewText(R.id.todo, todoItems.get(0).getName());
        
        
        // to populate a list, use a service
        // http://www.androidbook.com/item/3637
        // http://www.cnblogs.com/carlo/p/3333864.html
       
       //rv.setTextViewText(R.id.quote, q.quote + " (" + q.author + ")");
		
		// Publish the extension data update
		publishUpdate(new ExtensionData().visible(true).icon(R.drawable.ic_launcher)
				.statusToDisplay("TODO: " + todoItems.get(0).getName())
				.statusToSpeak(name + todoItems.get(0).getName() + ".")
				.languageToSpeak(Locale.US)
				.viewsToDisplay(rv)
				.contentDescription("Reminder " + todoItems.get(0).getName()));
	}
}