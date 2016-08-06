package com.notesIn9.model.Model;

import java.util.Date;

import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.utils.Factory;

import com.ibm.dtfj.javacore.parser.framework.Section;
import com.notesIn9.model.util.PersonField;

public class Person extends com.notesIn9.base.AbstractObject {

	private static final long serialVersionUID = 1L;

	private String id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String email;
	private Date birthDay;

	private boolean valid;

	public Person() {

	}

	public Person load(Document doc) {

		if (null == doc) {
			// This item was not found
			this.setValid(false);

		} else {

			if (this.loadValues(doc)) {
				this.setValid(true);
			}
		}

		return this;

	}

	public Person load(String id) {

		Document doc = this.getDocument();

		if (null == doc) {
			// This item was not found
			this.setValid(false);

		} else {

			if (this.loadValues(doc)) {
				this.setValid(true);
			}
		}

		return this;

	}

	protected boolean loadValues(Document doc) {

		this.id = doc.getItemValueString("number");
		this.firstName = doc.getItemValueString("firstName");
		this.middleName = doc.getItemValueString("middleName");
		this.address = doc.getItemValueString("address");
		this.city = doc.getItemValueString("city");
		this.state = doc.getItemValueString("state");
		this.zip = doc.getItemValueString("zip");
		this.country = doc.getItemValueString("email");
		if (!doc.getItemValueString("birthday").isEmpty()) {
			this.birthDay = (Date) doc.getItemValue("birthday").get(0);
		}

		return true;

	}

	public Document getDocument() {

		Session session = Factory.getSession();
		Database currentDB = session.getCurrentDatabase();

		Database fakeNames = session.getDatabase(currentDB.getServer(),
				"fakenames.nsf");
		View vItems = fakeNames.getView("byId");

		Document doc = vItems.getFirstDocumentByKey(id, true);

		return doc;
	}

	public boolean save() {
		
		Document doc = this.getDocument();
		
		if (null == doc) {
			return false;
		} else {
			
			return this.saveValues(doc);
		
		}
		

	}
	
	protected boolean saveValues(Document doc) {
		
		doc.replaceItemValue("firstName", this.getFirstName());
		doc.replaceItemValue("middleName", this.getMiddleName());
		doc.replaceItemValue("lastName", this.getLastName());
		doc.replaceItemValue("address", this.getAddress() );
		doc.replaceItemValue("city", this.getCity());
		doc.replaceItemValue("state", this.getState());
		doc.replaceItemValue("zip", this.getZip());
		doc.replaceItemValue("country", this.getCountry());
		doc.replaceItemValue("birthDay", this.getBirthDay() );
		doc.replaceItemValue("number", this.getId());
		
		return true;
		
		
		
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Object getValue(PersonField key) {
		switch (key) {
		case FIRST_NAME:
			return this.getFirstName();
		case MIDDLE_NAME:
			return this.getMiddleName();
		case LAST_NAME:
			return this.getLastName();
		case ADDRESS:
			return this.getAddress();
		case CITY:
			return this.getCity();
		case STATE:
			return this.getState();
		case ZIP:
			return this.getZip();
		case COUNTRY:
			return this.getCountry();
		case EMAIL:
			return this.getEmail();
		case BIRTHDAY:
			return this.getBirthDay();

			// all other instances

		default:
			return null;
		}
	}

}
