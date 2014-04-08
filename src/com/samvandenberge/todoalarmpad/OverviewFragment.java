package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.samvandenberge.todoalarmpad.extension.TodoExtension;
import com.samvandenberge.todoalarmpad.model.Todo;
import com.samvandenberge.todoalarmpad.sqlite.DatabaseTodo;

public class OverviewFragment extends ListFragment {
	private static final String TAG = "OverviewFragment";
	private static final String KEY_SORT_MODE = "sort_mode";
	private final int SPEECHTOTEXT = 1;

	private Button mAddButton;
	private ImageView mSpeechButton;
	private EditText mNewTodo;
	private TextView mEmptyView;

	private List<Todo> mTodoItems;
	private ArrayAdapter<Todo> mAdapter;
	private DatabaseTodo db;
	private boolean mIsSortInversed;

	public OverviewFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		db = DatabaseTodo.getInstance(getActivity());
		mTodoItems = db.getAllTodos();
		mIsSortInversed = false; // default
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String sortMode = sharedPref.getString(KEY_SORT_MODE, "time_added_oldest");
		if (sortMode.equals("time_added_oldest")) {
			menu.findItem(R.id.action_sort_by_time_added_oldest).setChecked(true);
			mIsSortInversed = false;
		} else {
			menu.findItem(R.id.action_sort_by_time_added).setChecked(true);
			mIsSortInversed = true;
			Collections.reverse(mTodoItems);
			updateList();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = sharedPref.edit();

		switch (item.getItemId()) {
		case R.id.action_clear_completed:
			deleteTodos();
			return true;
		case R.id.action_sort_by_time_added:
			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);
				editor.putString(KEY_SORT_MODE, "time_added");
				editor.commit();
				mIsSortInversed = true;
				Collections.reverse(mTodoItems);
				updateList();
			}
			return true;
		case R.id.action_sort_by_time_added_oldest:
			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);
				editor.putString(KEY_SORT_MODE, "time_added_oldest");
				editor.commit();
				// reverse 
				mIsSortInversed = false;
				Collections.reverse(mTodoItems);
				updateList();
			}
			return true;
		case R.id.action_clear_all:
			confirmClearAll();
			return true;
		case R.id.action_about:
			Intent intent = new Intent(getActivity(), AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mEmptyView = (TextView)rootView.findViewById(R.id.empty);
		mAddButton = (Button) rootView.findViewById(R.id.btnAdd);
		mAddButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addTodo();
			}
		});

		mNewTodo = (EditText) rootView.findViewById(R.id.etAdd);
		mNewTodo.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				} else if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					addTodo();
					return true;
				}
				return false;
			}
		});

		mSpeechButton = (ImageView) rootView.findViewById(R.id.btnSpeech);
		mSpeechButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				PackageManager pm = getActivity().getPackageManager();
				// Querying Package Manager
				List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
				if (activities.size() <= 0) {
					Log.i("SAM", "No Activity found to handle the action ACTION_RECOGNIZE_SPEECH");
					return;
				}

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
				startActivityForResult(intent, SPEECHTOTEXT);
			}
		});

		mAdapter = new TodoListAdapter(this.getActivity(), R.layout.list_item_checked, mTodoItems);
		setListAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setEmptyView(mEmptyView);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckedTextView tv = (CheckedTextView) view;
				toggle(tv);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SPEECHTOTEXT:
			if (resultCode == Activity.RESULT_OK && data != null) {
				ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if (text.size() > 0) {
					mNewTodo.setText(text.get(0));
					mNewTodo.setSelection(mNewTodo.getText().length()); // set cursor at the end
				}
			}
			break;
		}
	}

	/**
	 * Toggle CheckBoxes and strike through if checked
	 * 
	 * @param v
	 */
	public void toggle(CheckedTextView v) {
		int id = v.getId();

		// get the clicked Todo
		Todo clicked = null;
		for (Todo t : mTodoItems) {
			if (id == t.getId()) {
				clicked = t;
			}
		}

		// implement checking because of choise_mode none in XML
		if (v.isChecked() && clicked != null) {
			v.setChecked(false);
			clicked.setStatus(0); // unmarked
			db.setTodoStatus(id, 0);
			v.setPaintFlags(v.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		} else if (clicked != null) {
			v.setChecked(true);
			v.setPaintFlags(v.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			clicked.setStatus(1); // marked
			db.setTodoStatus(id, 1);
		}
	}

	/**
	 * Add a new Todo item
	 */
	private void addTodo() {
		String todoName = mNewTodo.getText().toString();
		if (!todoName.equals("")) {
			Todo todo = new Todo(todoName, 0);
			long id = db.createTodo(todo);
			todo.setId((int) id); // update id

			// prepend
			if (mIsSortInversed) {
				mTodoItems.add(0, todo);
			} else {
				// add at the end of the list
				mTodoItems.add(todo);
			}

			updateList();
			mNewTodo.setText("");
		}
	}

	/**
	 * Delete todo's
	 */
	private void deleteTodos() {
		boolean isDataChanged = false;

		db.deleteTodoWithStatus(1);

		for (int i = mTodoItems.size() - 1; i >= 0; i--) {
			if (mTodoItems.get(i).getStatus() == 1) {
				mTodoItems.remove(i);
				isDataChanged = true;
			}
		}
		if (isDataChanged) {
			updateList();
		}
	}

	/**
	 * Update the ListView
	 */
	private void updateList() {
		mAdapter.notifyDataSetChanged();
		// update extension data
		Log.i(TAG, "Send broadcast to extension");
		getActivity().sendBroadcast(new Intent(TodoExtension.ACTION_UPDATE_ALARMPAD));
	}
	
	/**
	 * Delete all todo's but confirm first
	 */
	private void confirmClearAll() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Are you sure you want to delete all todo\'s?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						db.deleteTodoWithStatus(0);
						db.deleteTodoWithStatus(1);
						mTodoItems.clear();
						updateList();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}