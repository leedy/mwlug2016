package com.notesIn9.base;

import java.io.Serializable;

public class AbstractObject implements Serializable {


	private static final long serialVersionUID = 1L;
	
	protected void console(final String debugText) {
		
		// add logic here to not print on production servers.
		
		System.out.println(this.getClass().getName() + " : " + debugText);
	}
	
	

}
