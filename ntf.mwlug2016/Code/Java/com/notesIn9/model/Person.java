package com.notesIn9.model;

import java.util.Date;

import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.utils.Factory;


import com.notesIn9.model.util.PersonField;

public class Person extends com.notesIn9.base.AbstractObject implements Comparable<Person> {

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
	
	private String addInfo;

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
		
		this.id = id;

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
		this.middleName = doc.getItemValueString("middle");
		this.lastName = doc.getItemValueString("lastName");
		this.address = doc.getItemValueString("address");
		this.city = doc.getItemValueString("city");
		this.state = doc.getItemValueString("state");
		this.zip =  doc.getItemValueString("zip");
		this.country = doc.getItemValueString("country");
//		if (!doc.getItemValueString("birthday").isEmpty()) {
//			this.birthDay = (Date) doc.getItemValue("birthday").get(0);
//		}
		
		Date myDate = doc.getItemValue("birthday", Date.class);
		if (null != myDate ) {
			this.birthDay = myDate;
		}
		
		
	
		this.email = doc.getItemValueString("email");
		this.addInfo = doc.getItemValueString("addInfo");
		
	
		
		

		return true;

	}

	public Document getDocument() {

		Session session = Factory.getSession();
		Database currentDB = session.getCurrentDatabase();

		Database fakeNames = session.getDatabase(currentDB.getServer(),
				"testnames.nsf");
		View vItems = fakeNames.getView("byId");

		Document doc = vItems.getFirstDocumentByKey(this.id, true);

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
		
		doc.replaceItemValue("addInfo", this.addInfo);
		
		
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
	
	

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
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
		case ADDINFO:
			return this.getAddInfo();
		case ID:
			return this.getId();

			// all other instances

		default:
			return "";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	public int compareTo(Person personToCompare) {
		// I think this is the DEFAULT comparison
		// Sorts ItemID's

		return Person.compare(this, personToCompare);
	}
	
	public static int compare(Person arg0, Person arg1) {
		int IS_EQUAL = 0;
		int IS_LESS_THAN = -1;
		int IS_GREATER_THAN = 1;

		if (null == arg0) {

			return (null == arg1) ? IS_EQUAL : IS_LESS_THAN;
		} else if (null == arg1) {

		return IS_GREATER_THAN; } // (null == arg0)

		if (arg0.equals(arg1)) { return IS_EQUAL; }

		int result = Person.compareLastName(arg0, arg1);
		if (IS_EQUAL == result) {
			result = Person.compareFirstName(arg0, arg1);
			if (IS_EQUAL == result) {
				result = Person.compareId(arg0, arg1);
			}
		}

		return result;

	} // compare(Item, Item)

	
	private static int compareLastName(Person arg0, Person arg1) {

		return arg0.getLastName().compareTo(arg1.getLastName());

	}
	
	private static int compareFirstName(Person arg0, Person arg1) {

		return arg0.getFirstName().compareTo(arg1.getFirstName());

	}
	
	private static int compareId(Person arg0, Person arg1) {

		return arg0.getId().compareTo(arg1.getId());

	}
}
