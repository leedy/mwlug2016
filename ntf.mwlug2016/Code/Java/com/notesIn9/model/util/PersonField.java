package com.notesIn9.model.util;

import java.util.Date;

import com.ibm.commons.util.StringUtil;


public enum PersonField {
	ID("ID"),
	FIRST_NAME("First Name"),
	MIDDLE_NAME("Middle Name"),
	LAST_NAME("Last Name"),
	ADDRESS("Address"),
	CITY("City"),
	STATE("State"),
	ZIP("Zip"),
	COUNTRY("Country"),
	EMAIL("Email"),
	BIRTHDAY(Date.class, "Birthday"),
	ADDINFO("Info");
	
	
	private final String	_label;
	private final Class<?>	_type;
	
	private PersonField() {
		this._label = StringUtil.getProperCaseString(this.name());
		this._type = String.class;
		
	}
	

	private PersonField(final String label) {
		this._label = (StringUtil.isEmpty(label)) ? StringUtil.getProperCaseString(this.name()) : label;
		this._type = String.class;
	}


	private PersonField(final Class<?> type) {
		this._type = (null == type) ? String.class : type;
		this._label = StringUtil.getProperCaseString(this.name());
	}


	private PersonField(final Class<?> type, final String label) {
		this._type = (null == type) ? String.class : type;
		this._label = (StringUtil.isEmpty(label)) ? StringUtil.getProperCaseString(this.name()) : label;
	}
	
	public Class<?> getType() {
		return this._type;
	}


	public long getFlag() {
		return (long) Math.pow(2, this.ordinal());
	}


	public boolean isFlagged(final long flags) {
		final long flag = this.getFlag();
		return ((flags & flag) == flag);
	}


	public static long getStandardFields() {
		return PersonField.getFlag(ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME);
	}
	
	public static long getFlag(final PersonField... instances) {
		long result = 0;
		for (final PersonField instance : instances) {
			result += instance.getFlag();
		}
		return result;
	}


	public String getLabel() {
		return this._label;
	}
	
	/**
	 * Gets the ItemField instance for the specified key
	 * 
	 * @param key
	 *            Key specifying which instance should be returned.
	 * 
	 * @return ItemField instance, or null if key is invalid.
	 */
	public static PersonField getValue(final Object key) {
		if (null == key) { return null; }
		if (key instanceof PersonField) { return (PersonField) key; }

		final String ucKey =  key.toString().toUpperCase();
		if (StringUtil.isEmpty(ucKey)) { return null; }

		try {
			final PersonField result = PersonField.valueOf(ucKey);
			if (null != result) { return result; }
		} catch (final Exception e) {
			// do nothing
		}

		// check the labels
		for (final PersonField result : PersonField.values()) {
			if (result.getLabel().equalsIgnoreCase(ucKey)) { return result; }
		}

		return null;
	}
	
	
}
