package com.samvandenberge.todoalarmpad.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.samvandenberge.todoalarmpad.model.Todo;
import com.samvandenberge.todoalarmpad.util.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseTodo extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "db_todos.sql";
	// tables
	public static final String TABLE_TODO = "todos";
	// common column names
	public static final String KEY_ID = "_id";
	public static final String KEY_TODO = "todo";
	public static final String KEY_STATUS = "status";
	public static final String KEY_CREATED_AT = "created_at";

	private static DatabaseTodo sInstance;

	// Use the application context, which will ensure that you
	// don't accidentally leak an Activity's context.
	// See this article for more information: http://bit.ly/6LRzfx
	public static DatabaseTodo getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DatabaseTodo(context.getApplicationContext());
		}
		return sInstance;
	}

	/**
	 * Constructor should be private to prevent direct instantiation. make call
	 * to static factory method "getInstance()" instead.
	 */
	private DatabaseTodo(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Table Create Statements
	private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_TODO + " TEXT NOT NULL," + KEY_STATUS + " INTEGER," 
			+ KEY_CREATED_AT + " DATETIME" + ")";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create required tables
		db.execSQL(CREATE_TABLE_TODO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= newVersion) {
			return;
		}
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

		onCreate(db);
	}

	/**
	 * Create a new TODO item
	 * 
	 * @param todo
	 */
	public long createTodo(Todo todo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TODO, todo.getNote());
		values.put(KEY_STATUS, todo.getStatus());
		values.put(KEY_CREATED_AT, Helper.getDateTime());
		values.put(KEY_STATUS, 0);
		
		return db.insert(TABLE_TODO, null, values);
	}

	/**
	 * Get a single TODO
	 * 
	 * @param id
	 */
	public Todo getTodo(long todoId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_TODO + " WHERE " + KEY_ID + " = " + todoId;
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Todo td = new Todo();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
		td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
		td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

		return td;
	}

	/**
	 * Delete a single TODO
	 * 
	 * @param todo
	 */
	public void deleteTodo(long todoId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TODO, KEY_ID + " = ?", new String[] { String.valueOf(todoId) });
	}
	
	public void deleteTodoWithStatus(int status) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_TODO, KEY_STATUS + " = ?", new String[] { String.valueOf(status) });
	}

	/**
	 * Get all TODO's
	 * 
	 * @return
	 */
	public List<Todo> getAllTodos() {
		List<Todo> todoItems = new ArrayList<Todo>();

		String selectQuery = "SELECT * FROM " + TABLE_TODO;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Todo td = new Todo();
				td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
				td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
				td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
				todoItems.add(td);
			} while (c.moveToNext());
		}

		return todoItems;
	}
	
	/**
	 * 
	 * @param todoId
	 */
	public void setTodoStatus(long todoId, int status) {
		SQLiteDatabase db = this.getWritableDatabase();

		String strFilter = KEY_ID + "=" + todoId;
		ContentValues args = new ContentValues();
		args.put(KEY_STATUS, status);
		db.update(TABLE_TODO, args, strFilter, null);
	}

	/**
	 * Close the database
	 */
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}
}
