package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.samvandenberge.todoalarmpad.model.Todo;
import com.samvandenberge.todoalarmpad.sqlite.DatabaseTodo;

public class OverviewFragment extends ListFragment {
	private static final String LOG_TAG = "OverviewFragment";
	private final int SPEECHTOTEXT = 1;

	private Button mAddButton;
	private ImageView mSpeechButton;
	private EditText mNewTodo;

	private List<Todo> mTodoItems;
	private ArrayAdapter<Todo> mAdapter;
	private DatabaseTodo db;

	public OverviewFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = DatabaseTodo.getInstance(getActivity());
		mTodoItems = db.getAllTodos();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mNewTodo = (EditText) rootView.findViewById(R.id.etAdd);
		mAddButton = (Button) rootView.findViewById(R.id.btnAdd);
		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addTodo();
			}
		});

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

			//				if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
			//					
			//				}
			//				return true;

		});

		mSpeechButton = (ImageView) rootView.findViewById(R.id.btnSpeech);
		mSpeechButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				// Getting an instance of PackageManager
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

		mAdapter = new TodoListAdapter(getActivity(), R.layout.list_item, mTodoItems);
		setListAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SPEECHTOTEXT:
			if (resultCode == Activity.RESULT_OK && null != data) {
				ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				mNewTodo.setText(text.get(0));
			}
			break;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(LOG_TAG, "Clicked a list item");
	}

	/**
	 * Add a new Todo item
	 */
	private void addTodo() {
		String todoName = mNewTodo.getText().toString();
		if (!todoName.equals("")) {
			Todo todo = new Todo(todoName, 0);
			db.createTodo(todo);
			mTodoItems.add(todo);
			updateList();
			mNewTodo.setText("");
		}
	}

	/**
	 * Update the ListView
	 */
	private void updateList() {
		mAdapter.notifyDataSetChanged();
	}
}