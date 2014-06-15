package org.objectquery.hibernate;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.BaseSelectQuery;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.OrderType;
import org.objectquery.generic.ProjectionType;
import org.objectquery.hibernate.domain.Home;
import org.objectquery.hibernate.domain.Person;

public class TestPersistentSelect {
	private Session session;

	@Before
	public void beforeTest() {
		session = PersistentTestHelper.getFactory().openSession();
		session.getTransaction().begin();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleSelect() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");

		List<Person> res = HibernateObjectQuery.buildQuery(qp, session).list();

		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleSelectWithutCond() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		List<Person> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(3, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPathValue() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDud().getHome(), target.getMom().getHome());
		List<Person> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getDud().getHome(), res.get(0).getMom().getHome());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPathParam() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDud().getName(), "tomdud");
		List<Person> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getDud().getName(), "tomdud");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectCountThis() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target, ProjectionType.COUNT);
		List<Object> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(3L, res.get(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPrjection() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.prj(target.getHome());
		qp.eq(target.getName(), "tom");
		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals("tom", res.get(0)[0]);
		Assert.assertEquals("homeless", ((Home) res.get(0)[1]).getAddress());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrder() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.order(target.getName());
		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(3, res.size());
		Assert.assertEquals("tom", res.get(0));
		Assert.assertEquals("tomdud", res.get(1));
		Assert.assertEquals("tommum", res.get(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrderDesc() {
		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.order(target.getName(), OrderType.DESC);
		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(3, res.size());
		Assert.assertEquals("tommum", res.get(0));
		Assert.assertEquals("tomdud", res.get(1));
		Assert.assertEquals("tom", res.get(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectSimpleConditions() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");
		qp.like(target.getName(), "tom");
		qp.notLike(target.getName(), "tom");
		qp.gt(target.getName(), "tom");
		qp.lt(target.getName(), "tom");
		qp.gtEq(target.getName(), "tom");
		qp.ltEq(target.getName(), "tom");
		qp.notEq(target.getName(), "tom");
		qp.likeNc(target.getName(), "tom");
		qp.notLikeNc(target.getName(), "tom");
		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(0, res.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectINCondition() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();

		List<String> pars = new ArrayList<String>();
		pars.add("tommy");
		qp.in(target.getName(), pars);
		qp.notIn(target.getName(), pars);

		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(0, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectContainsCondition() {

		GenericSelectQuery<Person, Object> qp0 = new GenericSelectQuery<Person, Object>(Person.class);
		Person target0 = qp0.target();
		qp0.eq(target0.getName(), "tom");

		List<Person> res0 = HibernateObjectQuery.buildQuery(qp0, session).list();
		Assert.assertEquals(1, res0.size());
		Person p = res0.get(0);

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.contains(target.getFriends(), p);
		qp.notContains(target.getFriends(), p);

		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(0, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectFunctionGrouping() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(target.getAddress());

		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(res.size(), 3);
		Assert.assertEquals(res.get(0)[1], 0d);
		Assert.assertEquals(res.get(1)[1], 0d);
		Assert.assertEquals(res.get(2)[1], 1000000d);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrderGrouping() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.order(qp.box(target.getPrice()), ProjectionType.MAX, OrderType.ASC);

		List<Home> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(3, res.size());
		Assert.assertEquals(0d, res.get(0).getPrice(), 0);
		Assert.assertEquals(0d, res.get(1).getPrice(), 0);
		Assert.assertEquals(1000000d, res.get(2).getPrice(), 0);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrderGroupingPrj() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(qp.box(target.getPrice()), ProjectionType.MAX, OrderType.DESC);

		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(3, res.size());
		Assert.assertEquals((Double) res.get(0)[1], 1000000d, 0);
		Assert.assertEquals((Double) res.get(1)[1], 0d, 0);
		Assert.assertEquals((Double) res.get(2)[1], 0d, 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectGroupHaving() {
		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(qp.box(target.getPrice()), ProjectionType.MAX, OrderType.DESC);
		qp.having(qp.box(target.getPrice()), ProjectionType.MAX).eq(1000000d);

		List<Object[]> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals((Double) res.get(0)[1], 1000000d, 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectBetweenCondition() {
		BaseSelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.between(qp.box(target.getPrice()), 100000D, 2000000D);

		List<Home> res = HibernateObjectQuery.buildQuery(qp, session).list();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getPrice(), 1000000d, 0);
	}

	@After
	public void afterTest() {
		if (session != null) {
			session.getTransaction().commit();
			session.close();
		}
		session = null;
	}
}
