package org.objectquery.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.objectquery.BaseQuery;
import org.objectquery.DeleteQuery;
import org.objectquery.InsertQuery;
import org.objectquery.SelectQuery;
import org.objectquery.UpdateQuery;
import org.objectquery.generic.GenericBaseQuery;
import org.objectquery.generic.ObjectQueryException;

public class HibernateObjectQuery {

	public static HQLQueryGenerator hqlGenerator(BaseQuery<?> objectQuery) {
		if (objectQuery instanceof GenericBaseQuery<?>)
			return new HQLQueryGenerator((GenericBaseQuery<?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}

	public static Query buildQuery(BaseQuery<?> objectQuery, Session session) {
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

	public static Object execute(SelectQuery<?> objectQuery, Session session) {
		return buildQuery(objectQuery, session).list();
	}

	public static int execute(DeleteQuery<?> dq, Session session) {
		return buildQuery(dq, session).executeUpdate();
	}

	public static int execute(InsertQuery<?> ip, Session session) {
		return buildQuery(ip, session).executeUpdate();
	}

	public static int execute(UpdateQuery<?> query, Session session) {
		return buildQuery(query, session).executeUpdate();
	}

}
