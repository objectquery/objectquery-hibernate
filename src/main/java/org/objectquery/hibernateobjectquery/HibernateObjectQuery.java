package org.objectquery.hibernateobjectquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.objectquery.ObjectQuery;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.generic.ObjectQueryException;

public class HibernateObjectQuery {

	public static HQLQueryGenerator hqlGenerator(ObjectQuery<?> objectQuery) {
		if (objectQuery instanceof GenericObjectQuery<?>)
			return new HQLQueryGenerator((GenericObjectQuery<?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}

	public static Query buildQuery(ObjectQuery<?> objectQuery, Session session) {
		HQLQueryGenerator gen = hqlGenerator(objectQuery);
		Query qu = session.createQuery(gen.getQuery());
		Map<String, Object> pars = gen.getParameters();
		for (Map.Entry<String, Object> ent : pars.entrySet()) {
			if (ent.getValue() instanceof Collection<?>) {
				if (ent.getValue() instanceof List<?>)
					qu.setParameterList(ent.getKey(), (List<?>) ent.getValue());
				else
					qu.setParameterList(ent.getKey(), new ArrayList<Object>((Collection<?>) ent.getValue()));
			} else
				qu.setParameter(ent.getKey(), ent.getValue());
		}
		return qu;
	}

	public static Object execute(ObjectQuery<?> objectQuery, Session session) {
		return buildQuery(objectQuery, session).list();
	}

}
