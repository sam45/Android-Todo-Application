package com.samvandenberge.todoalarmpad.extension;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.i("SAM", "onGetViewFactory");
		return (new WidgetViewsFactory(this.getApplicationContext(), intent));
	}

	public IBinder onBind(Intent intent) {
		return super.onBind(intent);
	}
}