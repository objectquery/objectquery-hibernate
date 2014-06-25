package org.objectquery.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.objectquery.hibernate.domain.Dog;
import org.objectquery.hibernate.domain.Home;
import org.objectquery.hibernate.domain.Home.HomeType;
import org.objectquery.hibernate.domain.Person;

public class PersistentTestHelper {

	private static SessionFactory sessionFactory;

	private static void initData() {
		Session session;
		session = sessionFactory.openSession();

		session.getTransaction().begin();

		Home tomHome = new Home();
		tomHome.setAddress("homeless");
		tomHome.setType(HomeType.HOUSE);
		tomHome = (Home) session.merge(tomHome);

		Person tom = new Person();
		tom.setName("tom");
		tom.setHome(tomHome);
		tom = (Person) session.merge(tom);

		Home dudHome = new Home();
		dudHome.setAddress("moon");
		dudHome.setType(HomeType.HOUSE);
		dudHome = (Home) session.merge(dudHome);

		Person tomDud = new Person();
		tomDud.setName("tomdud");
		tomDud.setHome(dudHome);
		tomDud = (Person) session.merge(tomDud);

		Person tomMum = new Person();
		tomMum.setName("tommum");
		tomMum.setHome(dudHome);

		tomMum = (Person) session.merge(tomMum);

		Home dogHome = new Home();
		dogHome.setAddress("royal palace");
		dogHome.setType(HomeType.KENNEL);
		dogHome.setPrice(1000000);
		dogHome.setWeight(30);

		dogHome = (Home) session.merge(dogHome);

		Dog tomDog = new Dog();
		tomDog.setName("cerberus");
		tomDog.setOwner(tom);
		tomDog.setHome(dogHome);
		tomDog = (Dog) session.merge(tomDog);

		tom.setDud(tomDud);
		tom.setMum(tomMum);
		tom.setDog(tomDog);
		tomDud.setDog(tomDog);

		session.persist(tomDud);
		session.persist(tom);
		session.getTransaction().commit();
		session.close();

	}

	public static SessionFactory getFactory() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration().configure().buildSessionFactory();
			initData();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					sessionFactory.close();
				}
			});
		}
		return sessionFactory;
	}

}
