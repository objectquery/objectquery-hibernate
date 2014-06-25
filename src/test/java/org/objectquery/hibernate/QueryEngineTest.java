package org.objectquery.hibernate;

import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.junit.Test;
import org.objectquery.QueryEngine;

public class QueryEngineTest {

	@Test
	public void testFactory() {
		QueryEngine<Session> instance = QueryEngine.instance(Session.class);
		assertTrue(instance instanceof HibernateQueryEngine);
	}

	@Test
	public void testDefalutFactory() {
		QueryEngine<Session> instance = QueryEngine.defaultInstance();
		assertTrue(instance instanceof HibernateQueryEngine);
	}
}
