package org.objectquery.hibernate;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.objectquery.QueryEngine;

public class QueryEngineTest {

	@Test
	public void testFactory() {
		QueryEngine<Session> instance = QueryEngine.instance(Session.class);
		Assert.assertTrue(instance instanceof HibernateQueryEngine);
	}

	@Test
	public void testDefalutFactory() {
		QueryEngine<Session> instance = QueryEngine.defaultInstance();
		Assert.assertTrue(instance instanceof HibernateQueryEngine);
	}
}
