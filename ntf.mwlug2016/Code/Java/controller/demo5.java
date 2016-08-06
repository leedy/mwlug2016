package controller;

import java.util.ArrayList;
import java.util.List;

public class demo5 extends com.notesIn9.base.PageController {


	private static final long serialVersionUID = 1L;
	private List<String> defaultColumns;

	public demo5() {
		
	}
	
	public List<String> getDefaultColumns() {
		this.console("Default Columns");
		List<String> myList = new ArrayList<String>();
		myList.add("Superman");
		myList.add("Batman");
		myList.add("Green Lantern");
		myList.add("Flash");
		myList.add("Aquaman");
		
		return myList;
		
	}
	
	public void setDefaultColumns(List<String> defaultColumns) {
		this.defaultColumns = defaultColumns;
	}
	
	
}
