package org.objectquery.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.JoinType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.hibernate.domain.Person;

public class TestJoinQuery {

	@Test
	public void testSimpleJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person joined = query.join(Person.class);
		query.eq(query.target().getMom(), joined);

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A,org.objectquery.hibernate.domain.Person AB0 where A.mom  =  AB0",
				HibernateObjectQuery.hqlGenerator(query).getQuery());
	}

	@Test(expected = ObjectQueryException.class)
	public void testTypedJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person joined = query.join(Person.class, JoinType.LEFT);
		query.eq(query.target().getMom(), joined);

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A LEFT JOIN org.objectquery.hibernate.domain.Person AB0 where A.mom  =  AB0",
				HibernateObjectQuery.hqlGenerator(query).getQuery());
	}

	@Test
	public void testTypedPathJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person joined = query.join(query.target().getMom(), Person.class, JoinType.LEFT);
		query.eq(joined.getName(), "test");

		Assert.assertEquals("select A from org.objectquery.hibernate.domain.Person A LEFT JOIN A.mom AB0 where AB0.name  =  :AB0_name",
				HibernateObjectQuery.hqlGenerator(query).getQuery());
	}

}
