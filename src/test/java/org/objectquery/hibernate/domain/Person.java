package org.objectquery.hibernate.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity
public class Person {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	@ManyToMany
	private List<Person> friends;
	@OneToOne
	private Person mom;
	@OneToOne
	private Person dud;
	@OneToOne
	private Home home;
	@OneToOne
	private Dog dog;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Person> getFriends() {
		return friends;
	}

	public void setFriends(List<Person> friends) {
		this.friends = friends;
	}

	public Person getMom() {
		return mom;
	}

	public void setMum(Person mum) {
		this.mom = mum;
	}

	public Person getDud() {
		return dud;
	}

	public void setDud(Person dud) {
		this.dud = dud;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public Dog getDog() {
		return dog;
	}

	public void setDog(Dog dog) {
		this.dog = dog;
	}

}
