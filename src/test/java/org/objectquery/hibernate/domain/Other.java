package org.objectquery.hibernate.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Other {

	@Id
	private long id;
	private String text;
	private double price;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
