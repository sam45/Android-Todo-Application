package com.samvandenberge.todoalarmpad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// don't pop up keyboard automatically
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new OverviewFragment()).commit();
		}
	}

	public boolean performAction(int itemId, int position) {
		return (false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
