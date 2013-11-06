package org.objectquery.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.objectquery.DeleteQuery;
import org.objectquery.InsertQuery;
import org.objectquery.QueryEngine;
import org.objectquery.SelectQuery;
import org.objectquery.UpdateQuery;

public class HibernateQueryEngine extends QueryEngine<Session> {

	@Override
	public <RET extends List<?>> RET execute(SelectQuery<?> query, Session engineSession) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int execute(DeleteQuery<?> dq, Session engineSession) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean execute(InsertQuery<?> ip, Session engineSession) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int execute(UpdateQuery<?> query, Session engineSession) {
		// TODO Auto-generated method stub
		return 0;
	}

}
