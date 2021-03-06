package com.samvandenberge.todo.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Helper {

	/**
	 * Get datetime
	 * 
	 * @return
	 */
	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
