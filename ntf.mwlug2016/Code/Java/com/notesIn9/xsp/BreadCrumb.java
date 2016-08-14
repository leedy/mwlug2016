package com.notesIn9.xsp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BreadCrumb {

	private static final long serialVersionUID = 1L;
	
	
	private HashMap<String, String> history;
	
	
	
	public BreadCrumb() {
		
		history = new LinkedHashMap<String, String>();
		
		
	}

	
	public void addLink(String title, String url) {

	HashMap<String, String> newMap = new LinkedHashMap<String, String>();
	
	newMap.put(title, url);
	
	int count = 0;
	
	for (Map.Entry<String, String> entry : history.entrySet()) {
	    //String key = entry.getKey();
	    //Object value = entry.getValue();
	    // ...
		count ++;
		
		if (count > 10) {
			break;
		}
		
		newMap.put(entry.getKey(), entry.getValue());
		
	}
	
	this.history = newMap;
	
	}


	public HashMap<String, String> getHistory() {
		return history;
	}


	public void setHistory(HashMap<String, String> history) {
		this.history = history;
	}
	
	public boolean showHistory() {
		if (this.history.size() > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	
}
