package org.objectquery.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.hibernate.domain.Person;

public class TestSubQuery {

	private static String getQueryString(SelectQuery<Person> query) {
		return HibernateObjectQuery.hqlGenerator(query).getQuery();
	}

	@Test
	public void testSubquerySimple() {
		SelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);

		SelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "test");
		query.eq(query.target().getDud(), subQuery);

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernate.domain.Person AA0 where AA0.name  =  :AA0_name)",
				getQueryString(query));

	}

	@Test
	public void testBackReferenceSubquery() {
		GenericSelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person target = query.target();
		SelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		query.eq(query.target().getDud(), subQuery);

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernate.domain.Person AA0 where AA0.name  =  A.dog.name)",
				getQueryString(query));
	}

	@Test
	public void testDoubleSubQuery() {

		GenericSelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person target = query.target();
		SelectQuery<Person> subQuery = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		SelectQuery<Person> doubSubQuery = subQuery.subQuery(Person.class);
		subQuery.eq(subQuery.target().getMom(), doubSubQuery);

		doubSubQuery.eq(doubSubQuery.target().getMom().getName(), subQuery.target().getMom().getName());
		doubSubQuery.eq(doubSubQuery.target().getMom().getName(), query.target().getMom().getName());

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernate.domain.Person AA0 where AA0.name  =  A.dog.name AND AA0.mom  =  (select AA0A0 from org.objectquery.hibernate.domain.Person AA0A0 where AA0A0.mom.name  =  AA0.mom.name AND AA0A0.mom.name  =  A.mom.name))",
				getQueryString(query));

	}

	@Test
	public void testMultipleReferenceSubquery() {
		GenericSelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person target = query.target();
		SelectQuery<Person> subQuery = query.subQuery(Person.class);
		SelectQuery<Person> subQuery1 = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		query.eq(target.getMom(), subQuery1);

		Assert.assertEquals(
				"select A from org.objectquery.hibernate.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernate.domain.Person AA0) AND A.mom  =  (select AA1 from org.objectquery.hibernate.domain.Person AA1)",
				getQueryString(query));

	}

	@Test
	public void testProjectionSubquery() {
		GenericSelectQuery<Person> query = new GenericSelectQuery<Person>(Person.class);
		Person target = query.target();
		SelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.prj(subQuery);

		Assert.assertEquals(
				"select (select AA0 from org.objectquery.hibernate.domain.Person AA0 where AA0.dog.owner  =  A.dud) from org.objectquery.hibernate.domain.Person A",
				getQueryString(query));

	}

}
