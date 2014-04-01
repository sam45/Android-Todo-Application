package com.samvandenberge.todoalarmpad.model;

/**
 * Todo object
 * @author Sam
 *
 */
public class Todo {
	private int id;
	private String note;
	private String createdAt;
	private int status;
	
	public Todo() {
	}
	
	public Todo(String note, int status) {
		this.note = note;
		this.status = status;
	}
	
	public Todo(int id, String note, int status) {
		this.id = id;
		this.note = note;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
