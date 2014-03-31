package com.samvandenberge.todoalarmpad;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.samvandenberge.todoalarmpad.data.DatabaseTodo;
import com.samvandenberge.todoalarmpad.data.Todo;

public class OverviewFragment extends ListFragment {
	private static final String LOG_TAG = "OverviewFragment";
	private Button mAddButton;
	private EditText mNewTodo;

	private List<Todo> mTodoItems;
	private ArrayAdapter<Todo> mAdapter;
	private DatabaseTodo db;

	public OverviewFragment() {}

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

		mAdapter = new TodoListAdapter(getActivity(), R.layout.list_item, mTodoItems);
		setListAdapter(mAdapter);

		return rootView;
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
			Todo todo = new Todo(todoName);
			db.createTodo(todo);
			mTodoItems.add(todo);
			updateList();
		}
	}

	/**
	 * Update the ListView
	 */
	private void updateList() {
		mAdapter.notifyDataSetChanged();
	}
}