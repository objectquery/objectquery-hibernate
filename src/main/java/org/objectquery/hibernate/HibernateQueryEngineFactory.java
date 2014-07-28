package org.objectquery.hibernate;

import org.hibernate.Session;
import org.objectquery.QueryEngine;
import org.objectquery.QueryEngineFactory;

public class HibernateQueryEngineFactory implements QueryEngineFactory {

	@Override
	public <S> QueryEngine<S> createQueryEngine(Class<S> targetSession) {
		if (Session.class.equals(targetSession))
			return createDefaultQueryEngine();
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> QueryEngine<T> createDefaultQueryEngine() {
		return (QueryEngine<T>) new HibernateQueryEngine();
	}

}
