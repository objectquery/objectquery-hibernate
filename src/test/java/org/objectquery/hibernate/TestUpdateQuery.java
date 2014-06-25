package org.objectquery.hibernate;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.UpdateQuery;
import org.objectquery.generic.GenericUpdateQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.hibernate.domain.Home;
import org.objectquery.hibernate.domain.Other;
import org.objectquery.hibernate.domain.Person;

public class TestUpdateQuery {

	private Session session;

	@Before
	public void beforeTest() {
		session = PersistentTestHelper.getFactory().openSession();
		session.getTransaction().begin();
	}

	@Test
	public void testSimpleUpdate() {
		Other home = new Other();
		home.setText("old-address");
		session.merge(home);

		UpdateQuery<Other> query = new GenericUpdateQuery<Other>(Other.class);
		query.set(query.target().getText(), "new-address");
		query.eq(query.target().getText(), "old-address");
		int res = HibernateObjectQuery.execute(query, session);
		assertEquals(1, res);
	}

	@Test
	public void testSimpleUpdateGen() {
		UpdateQuery<Home> query = new GenericUpdateQuery<Home>(Home.class);
		query.set(query.target().getAddress(), "new-address");
		query.eq(query.target().getAddress(), "old-address");
		HQLQueryGenerator q = HibernateObjectQuery.hqlGenerator(query);
		assertEquals("update org.objectquery.hibernate.domain.Home set address = :address where address  =  :address1", q.getQuery());
	}

	@Test(expected = ObjectQueryException.class)
	public void testSimpleNestedUpdate() {
		UpdateQuery<Person> query = new GenericUpdateQuery<Person>(Person.class);
		query.set(query.target().getHome().getAddress(), "new-address");
		query.eq(query.target().getHome().getAddress(), "old-address");
		HibernateObjectQuery.execute(query, session);
	}

	@Test(expected = ObjectQueryException.class)
	public void testSimpleNestedUpdateGen() {
		UpdateQuery<Person> query = new GenericUpdateQuery<Person>(Person.class);
		query.set(query.target().getHome().getAddress(), "new-address");
		query.eq(query.target().getHome().getAddress(), "old-address");

		HibernateObjectQuery.hqlGenerator(query);
	}

	@Test
	public void testMultipleNestedUpdate() {
		Other home = new Other();
		home.setText("2old-address");
		session.merge(home);

		UpdateQuery<Other> query = new GenericUpdateQuery<Other>(Other.class);
		query.set(query.target().getText(), "new-address");
		query.set(query.box(query.target().getPrice()), 1d);
		query.eq(query.target().getText(), "2old-address");
		int res = HibernateObjectQuery.execute(query, session);
		assertEquals(1, res);
	}

	@Test
	public void testMultipleNestedUpdateGen() {
		UpdateQuery<Home> query = new GenericUpdateQuery<Home>(Home.class);
		query.set(query.target().getAddress(), "new-address");
		query.set(query.box(query.target().getPrice()), 1d);
		query.eq(query.target().getAddress(), "old-address");

		HQLQueryGenerator q = HibernateObjectQuery.hqlGenerator(query);
		assertEquals("update org.objectquery.hibernate.domain.Home set address = :address,price = :price where address  =  :address1", q.getQuery());
	}

	@After
	public void afterTest() {
		if (session != null) {
			session.getTransaction().commit();
			session.close();
		}
		session = null;
	}

}
