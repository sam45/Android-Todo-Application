package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
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
import android.widget.ListView;

public class OverviewFragment extends ListFragment {
	private static final String LOG_TAG = "OverviewFragment";
	private Button mAddButton;

	public OverviewFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		mAddButton = (Button) rootView.findViewById(R.id.btnAdd);
		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
			}
		});

		List<Todo> todoItems = new ArrayList<Todo>();
		todoItems.add(new Todo("Feed the dog"));
		todoItems.add(new Todo("Make homework"));

		TodoListAdapter adapter = new TodoListAdapter(getActivity(), R.layout.list_item, todoItems);
		

//		String[] numbers_text = new String[] { "one", "two", "three", "four",  
//			    "five", "six", "seven", "eight", "nine", "ten", "eleven",  
//			    "twelve", "thirteen", "fourteen", "fifteen" };  
//		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
//			     inflater.getContext(), android.R.layout.simple_list_item_1,  
//			     numbers_text);
//		
		setListAdapter(adapter);
		
		return rootView;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Log.i(LOG_TAG, "Clicked a list item");
	}
}