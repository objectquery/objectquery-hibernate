package org.objectquery.hibernate;

import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.SelectMapQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.hibernate.domain.Person;
import org.objectquery.hibernate.domain.PersonDTO;

public class TestPersistentSelectMapQuery {
	private Session session;

	@Before
	public void beforeTest() {
		session = PersistentTestHelper.getFactory().openSession();
		session.getTransaction().begin();
	}

	@Test
	public void testSimpleSelectMap() {
		SelectMapQuery<Person, PersonDTO> query = new GenericSelectQuery<Person, PersonDTO>(Person.class, PersonDTO.class);
		query.eq(query.target().getName(), "tom");
		query.prj(query.target().getName(), query.mapper().getName());

		List<PersonDTO> res = HibernateObjectQuery.execute(query, session);
		assertThat(res.size(), CoreMatchers.is(1));
		assertThat(res.get(0).getName(), CoreMatchers.is("tom"));
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
