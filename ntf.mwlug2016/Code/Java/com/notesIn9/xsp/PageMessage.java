package com.notesIn9.xsp;

import java.util.HashMap;
import java.util.Map;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PageMessage extends com.notesIn9.base.AbstractObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private StringBuilder body;
	private StyleClass style;
	boolean valid;

	private Map<String, String> fieldMessages;

	public static enum StyleClass {
		SUCCESS("alert alert-success"), INFO("alert alert-info"), WARNING("alert alert-warning"), DANGER("alert alert-danger");

		private final String bsClass;

		private StyleClass(String tempClass) {
			this.bsClass = tempClass;
		}

		public String getStyle() {
			
			return this.bsClass;
		}

		public static StyleClass getValue(final Object key) {
			try {
				
				final String ucKey = StringUtil.toString(key);
				return (StringUtil.isEmpty(ucKey)) ? null : StyleClass.valueOf(ucKey);
			} catch (final Exception e) {
				// do nothing
			}

			return null;
		}

	}

	// END ENUM

	public PageMessage() {
		// Set some Defaults
		this.title = "Information.";
		this.body = new StringBuilder();
		this.style = PageMessage.StyleClass.INFO;

		this.fieldMessages = new HashMap<String, String>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void appendMessage(String temp) {
		console("*** Appending temp");
		this.body.append(temp);
		this.body.append(System.getProperty("line.separator"));
		this.valid = true;
	}

	public void appendMessage(String fieldName, String message) {

		String temp;

		if (this.fieldMessages.containsKey(fieldName)) {
			temp = this.fieldMessages.get(fieldName);
			temp += System.getProperty("line.separator");
			temp += message;

		} else {

			temp = message;

		}

		this.fieldMessages.put(fieldName, temp);

	}

	public String getBody() {
		return this.body.toString();
	}

	public String getStyle() {

		return this.style.getStyle();

	}
	
	public StyleClass getStyleClass() {
		return this.style;
	}

	public void setStyle(StyleClass style) {
		this.style = style;
	}

	public boolean isValid() {

		return this.valid;

	}

	public void setError() {

		this.setStyle(StyleClass.DANGER);
		this.title = "Error!";

	}

	public void setSuccess() {

		this.setStyle(StyleClass.SUCCESS);
		this.title = "Success!";

	}

	public Map<String, String> getFieldMessages() {
		return fieldMessages;
	}

	public void setFieldMessages(Map<String, String> fieldMessages) {
		this.fieldMessages = fieldMessages;
	}
	
	
	public void importMessages(PageMessage inMessage) {
		
		this.body = inMessage.body;
		this.fieldMessages = inMessage.getFieldMessages();
		this.style = inMessage.getStyleClass();
		this.title = inMessage.getTitle();
		this.valid = inMessage.isValid();
		
		
	}
	
	
	public static PageMessage getPageMessage() { 
		// If just working in java then you don't really need anything in the faces-config
		// but since I'm using a custom control and SSJS I'm using a true managed bean in the faces-config
		
		PageMessage result = null; 
		result = (PageMessage) ExtLibUtil.resolveVariable("pageMessage");
			
		if (null == result) { 
			result = new PageMessage(); 
			ExtLibUtil.getRequestScope().put("pageMessage", result);
		}
		
		
		return result;
	}
	
	
}
