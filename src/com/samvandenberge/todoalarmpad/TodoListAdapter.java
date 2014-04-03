package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
import java.util.List;

import com.samvandenberge.todoalarmpad.model.Todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TodoListAdapter extends ArrayAdapter<Todo> {
	private List<Todo> todoItems = new ArrayList<Todo>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public TodoListAdapter(Context context, int textViewResourceId,
			List<Todo> items) {
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
			newView = inflater.inflate(R.layout.list_item, null);
			holder.name = (TextView) newView.findViewById(R.id.tvName);
			holder.checkbox = (CheckBox) newView.findViewById(R.id.checkboxTodo);
			// store data in view
			newView.setTag(holder);

			// re-use view
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		// refresh data
		holder.name.setText(todo.getNote());
		holder.checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 CheckBox c = (CheckBox) v;

                Log.i("SAM", "checkbox " + holder.name.getText().toString() + " clicked");
			}
		});

		return newView;
	}

	static class ViewHolder {
		TextView name;
		CheckBox checkbox;
	}
}