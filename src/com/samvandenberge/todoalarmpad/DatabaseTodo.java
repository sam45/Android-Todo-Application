package com.samvandenberge.todoalarmpad;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseTodo extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "db_todos.sql";
	// tables
	public static final String TABLE_TODO = "exhibitors";
	// common column names
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";

	private static DatabaseTodo sInstance;

	// session column names
	public static final String KEY_SESSION_STARTDATE = "startdate";

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
	private static final String CREATE_TABLE_TODO = "CREATE TABLE "
			+ TABLE_TODO + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
			+ " TEXT NOT NULL);";

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
		values.put(KEY_NAME, todo.getName());

		return db.insert(TABLE_TODO, null, values);
	}

	/**
	 * Get a single TODO
	 * 
	 * @param id
	 */
	public Todo getTodo(long todoId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_TODO + " WHERE "
				+ KEY_ID + " = " + todoId;
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Todo t = new Todo();
		t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		return t;
	}

	/**
	 * Delete a single TODO
	 * 
	 * @param todo
	 */
	public void deleteTodo(long todoId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TODO, KEY_ID + " = ?",
				new String[] { String.valueOf(todoId) });
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
				Todo t = new Todo();
				t.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				todoItems.add(t);
			} while (c.moveToNext());
		}

		return todoItems;
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
