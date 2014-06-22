package org.objectquery.hibernate;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.objectquery.SelectMapQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.hibernate.domain.Person;
import org.objectquery.hibernate.domain.PersonDTO;

public class TestSelectMapQuery {

	@Test
	public void testSimpleSelectMap() {
		SelectMapQuery<Person, PersonDTO> query = new GenericSelectQuery<Person, PersonDTO>(Person.class, PersonDTO.class);
		query.prj(query.target().getName(), query.mapper().getName());

		Assert.assertThat(HibernateObjectQuery.hqlGenerator(query).getQuery(),
				CoreMatchers.is("select A.name as name from org.objectquery.hibernate.domain.Person A"));

	}

}
