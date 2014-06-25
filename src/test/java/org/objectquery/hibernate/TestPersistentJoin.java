package org.objectquery.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.JoinType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.hibernate.domain.Person;

public class TestPersistentJoin {
	private Session session;

	@Before
	public void beforeTest() {
		session = PersistentTestHelper.getFactory().openSession();
		session.getTransaction().begin();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimpleJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(Person.class);
		query.eq(query.target().getMom(), joined);

		List<Person> persons = HibernateObjectQuery.buildQuery(query, session).list();
		assertEquals(1, persons.size());
	}

	@Test(expected = ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testTypedJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(Person.class, JoinType.LEFT);
		query.eq(query.target().getMom(), joined);

		List<Person> persons = HibernateObjectQuery.buildQuery(query, session).list();
		assertEquals(1, persons.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTypedPathJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(query.target().getMom(), Person.class, JoinType.LEFT);
		query.eq(joined.getName(), "tommum");

		List<Person> persons = HibernateObjectQuery.buildQuery(query, session).list();
		assertEquals(1, persons.size());
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
