package com.notesIn9.model;



import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.DocumentCollection;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.utils.Factory;


public class State extends com.notesIn9.base.AbstractObject {

	private static final long serialVersionUID = 1L;

	private PersonGroup people;
	private String id;  //abbreviation really
	
	
	public State(String name) {
		
		this.id = name.toUpperCase();
		
		
	}
	
	public void loadPeople() {
		
		Session session = Factory.getSession();
		Database currentDB = session.getCurrentDatabase();

		Database fakeNames = session.getDatabase(currentDB.getServer(),"testnames.nsf");
		View people = fakeNames.getView("byState");
		DocumentCollection collect = people.getAllDocumentsByKey(this.id);
		
		this.people = new PersonGroup();
	
		
		
		for (Document doc : collect) {
			
			Person person = new Person();
			person.load(doc);
			
			this.getPeople().addPerson(person);
			
			
		}
		
		System.out.println("Should be  : " + collect.getCount());
		
	}

	public PersonGroup getPeople() {
		return people;
	}

	public void setPeople(PersonGroup people) {
		this.people = people;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	
}
