package com.notesIn9.model;

import java.util.Set;
import java.util.TreeSet;

import org.openntf.domino.Document;


public class PersonGroup extends com.notesIn9.base.AbstractObject  {
	
	
	
	private static final long serialVersionUID = 1L;
	
	private Set<Person> people;
	
	

	public PersonGroup() {
		
		this.people = new TreeSet<Person>();
		
		
	}

	public void addPerson(Document doc) {
		
		// convert to object
		Person temp = new Person();
		temp.load(doc);
		this.addPerson(temp);
		
	}
	
	public void addPerson(Person person) {
		
		// convert to object
		if (this.getPeople().add(person)) {
			
		} else {
			System.out.println(person.getId() + " already in Set");
		}
		
	}
	
	public void removePerson(Person person) {
		this.getPeople().remove(person);
	}
	
	
	

	public Set<Person> getPeople() {
		return people;
	}

	public void setPeople(Set<Person> people) {
		this.people = people;
	}
	
	public int getCount() {
		
		
		return this.getPeople().size();
		
	}
	
	
	
	

}
