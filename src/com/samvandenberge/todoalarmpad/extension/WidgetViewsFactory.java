package com.samvandenberge.todoalarmpad.extension;

import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.samvandenberge.todoalarmpad.R;
import com.samvandenberge.todoalarmpad.model.Todo;
import com.samvandenberge.todoalarmpad.sqlite.DatabaseTodo;

public class WidgetViewsFactory implements RemoteViewsFactory {
	private Context mContext;
	private int mAppWidgetId;
	private List<Todo> mTodoItems;
	private static final int mCount = 10;

	public WidgetViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	/**
	 * In onCreate() you setup any connections / cursors to your data source.
	 * Heavy lifting, for example downloading or creating content etc, should be
	 * deferred to onDataSetChanged() or getViewAt(). Taking more than 20
	 * seconds in this call will result in an ANR.
	 */
	public void onCreate() {
		DatabaseTodo db = DatabaseTodo.getInstance(mContext);
		mTodoItems = db.getAllTodos();
		Log.i("SAM", mTodoItems.size() + "");
	}

	@Override
	public void onDataSetChanged() {
		// This is triggered when you call AppWidgetManager  notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do  heaving lifting in
		// here, synchronously. For example, if you need to process an image,  fetch something
		// from the network, etc., it is ok to do it here, synchronously. The  widget will remain
		// in its current state while work is being done here, so you don't need  to worry about
		// locking up the widget.
	}

	@Override
	public void onDestroy() {
		// In onDestroy() you should tear down anything that was setup for your
		// data source, eg. cursors, connections, etc.
		mTodoItems.clear();
	}

	@Override
	public int getCount() {
		//return mTodoItems.size();
		return mCount;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		Log.i("SAM", "remoteviews getViewAt position: " + position);
		// position will always range from 0 to getCount() - 1.
		//TODO
		// We construct a remote views item based on our widget item xml file,
		// and set the text based on the position.
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.extension_list_item);
		rv.setTextViewText(R.id.tvTodo, mTodoItems.get(position).getNote());
		// rv.setTextViewText(R.id.widget_item,
		// mWidgetItems.get(position).text);

		// Next, we set a fill-intent which will be used to fill-in the pending
		// intent template
		// which is set on the collection view in StackWidgetProvider.
		 Bundle extras = new Bundle();
	        extras.putInt(TodoWidgetProvider.EXTRA_ITEM, position);
	        Intent fillInIntent = new Intent();
	        fillInIntent.putExtras(extras);
	        rv.setOnClickFillInIntent(R.id.tvTodo, fillInIntent);

		// You can do heaving lifting in here, synchronously. For example, if
		// you need to
		// process an image, fetch something from the network, etc., it is ok to
		// do it here,
		// synchronously. A loading view will show up in lieu of the actual
		// contents in the
		// interim.

		// Return the remote views object.
		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt()
		// is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}

	@Override
	public int getViewTypeCount() {
		// The number of views that will be returned
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	};
}