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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

		
		 // to populate a list, use a service
        // http://www.androidbook.com/item/3637
        // http://www.cnblogs.com/carlo/p/3333864.html
		
		// RemoteView
        RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.extension_remoteview);
        Intent updateIntent = new Intent(this, WidgetService.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        updateIntent.setData(Uri.parse(updateIntent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.list, updateIntent);

        // Alternative
        RemoteViews rv2 = new RemoteViews(this.getPackageName(), R.layout.extension_remoteview_manual);
        rv2.setTextViewText(R.id.tvExtensionTodo1, todoItems.get(0).getNote());
        rv2.setTextViewText(R.id.tvExtensionTodo2, todoItems.get(1).getNote());
        rv2.setTextViewText(R.id.tvExtensionTodo3, todoItems.get(2).getNote());
        rv2.setTextViewText(R.id.tvExtensionTodo4, todoItems.get(3).getNote());
        rv2.setTextViewText(R.id.tvExtensionTodo5, todoItems.get(4).getNote());
        rv2.setTextViewText(R.id.tvExtensionTodo6, todoItems.get(6).getNote());
        
        
        
//        LayoutInflater inflater = (LayoutInflater)getApplication().getSystemService
//        	      (Context.LAYOUT_INFLATER_SERVICE);
//        View inflated = inflater.inflate(R.layout.extension_remoteview_manual, null);
//        View parent = inflated.findViewById(R.id.tvExtensionTodoParent);
//        
//        ViewGroup rootView = (ViewGroup) rv.findViewById(android.R.id.content).getRootView();
//        applyFontRecursively(rootView, font);
//        
//        
        //RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.extension_remoteview);
       // Intent svcIntent = new Intent(this, WidgetService.class);
        
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        ComponentName thisAppWidget = new ComponentName(this.getPackageName(), TodoWidgetProvider.class.getName());
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
  
//        Intent intent = new Intent(this, WidgetService.class);
//        PendingIntent pendingIntentTemplate = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        rv.setPendingIntentTemplate(R.id.list, pendingIntentTemplate);
//        rv.setRemoteAdapter(R.id.list, intent);
        

       // svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
//        RemoteViews widget = new RemoteViews(this.getPackageName(), R.layout.extension_remoteview);
//        widget.setRemoteAdapter(R.id.list, svcIntent);
		
		// Publish the extension data update
		publishUpdate(new ExtensionData().visible(true).icon(R.drawable.ic_launcher)
				.statusToDisplay(todoItems.size() + " Todo\'s")
				.statusToSpeak("You have " + todoItems.size() + " Todo\'s")
				.languageToSpeak(Locale.US)
				.viewsToDisplay(rv2)
				.contentDescription("You have " + todoItems.size() + " Todo\'s"));
	}
}