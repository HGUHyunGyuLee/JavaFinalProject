package edu.handong.csee.java.utils;

import java.util.List;

public class Library<T>{

	private List<T> items; 
	 
	public Library(List<T> items){
		this.items = items;
	}
	public Library(){
		//empty constructor
	}
 
	public Object issueItem(int i){
		return items.get(i);
	}
	public Object issueList(){
		return items;
	}
 
	public void addItem(T item){
		items.add(item);
	}
	public int length() {
		return items.size();
	}

}
