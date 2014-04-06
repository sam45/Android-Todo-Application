package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samvandenberge.todoalarmpad.model.Todo;

public class TodoListAdapter extends ArrayAdapter<Todo> {
	private List<Todo> todoItems = new ArrayList<Todo>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public TodoListAdapter(Context context, int textViewResourceId, List<Todo> items) {
		super(context, textViewResourceId, items);
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		todoItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View newView = convertView;
		final ViewHolder holder;
		Todo todo = todoItems.get(position);

		// inflate new view
		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.list_item_checked, null);
			holder.name = (TextView) newView.findViewById(R.id.tvNote);
			// store data in view
			newView.setTag(holder);
			// re-use view
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		// refresh data
		holder.name.setText(todo.getNote());

		return newView;
	}

	static class ViewHolder {
		TextView name;
	}
}