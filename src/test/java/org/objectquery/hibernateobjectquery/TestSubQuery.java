package org.objectquery.hibernateobjectquery;

import org.junit.Assert;
import org.junit.Test;
import org.objectquery.ObjectQuery;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.hibernateobjectquery.domain.Person;

public class TestSubQuery {

	private static String getQueryString(ObjectQuery<Person> query) {
		return HibernateObjectQuery.hqlGenerator(query).getQuery();
	}

	@Test
	public void testSubquerySimple() {
		ObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);

		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "test");
		query.eq(query.target().getDud(), subQuery);

		Assert.assertEquals(
				"select A from org.objectquery.hibernateobjectquery.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernateobjectquery.domain.Person AA0 where AA0.name  =  :AA0_name)",
				getQueryString(query));

	}

	@Test
	public void testBackReferenceSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		query.eq(query.target().getDud(), subQuery);

		Assert.assertEquals(
				"select A from org.objectquery.hibernateobjectquery.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernateobjectquery.domain.Person AA0 where AA0.name  =  A.dog.name)",
				getQueryString(query));
	}

	@Test
	public void testDoubleSubQuery() {

		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		ObjectQuery<Person> doubSubQuery = subQuery.subQuery(Person.class);
		subQuery.eq(subQuery.target().getMum(), doubSubQuery);

		doubSubQuery.eq(doubSubQuery.target().getMum().getName(), subQuery.target().getMum().getName());
		doubSubQuery.eq(doubSubQuery.target().getMum().getName(), query.target().getMum().getName());

		Assert.assertEquals(
				"select A from org.objectquery.hibernateobjectquery.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernateobjectquery.domain.Person AA0 where AA0.name  =  A.dog.name AND AA0.mum  =  (select AA0A0 from org.objectquery.hibernateobjectquery.domain.Person AA0A0 where AA0A0.mum.name  =  AA0.mum.name AND AA0A0.mum.name  =  A.mum.name))",
				getQueryString(query));

	}

	@Test
	public void testMultipleReferenceSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		ObjectQuery<Person> subQuery1 = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		query.eq(target.getMum(), subQuery1);

		Assert.assertEquals(
				"select A from org.objectquery.hibernateobjectquery.domain.Person A where A.dud  =  (select AA0 from org.objectquery.hibernateobjectquery.domain.Person AA0) AND A.mum  =  (select AA1 from org.objectquery.hibernateobjectquery.domain.Person AA1)",
				getQueryString(query));

	}

	@Test
	public void testProjectionSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.prj(subQuery);

		Assert.assertEquals(
				"select (select AA0 from org.objectquery.hibernateobjectquery.domain.Person AA0 where AA0.dog.owner  =  A.dud) from org.objectquery.hibernateobjectquery.domain.Person A",
				getQueryString(query));

	}

}
