package com.javarush.dao;

import com.javarush.domain.entity.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class CityDAO extends GenericDAO<City> {
    public CityDAO(SessionFactory sessionFactory) {
        super(City.class, sessionFactory);
    }

    public City getCityByName(String name) {
        String hql = "select c from City c where c.name=:name";
        Query<City> query = getCurrentSession().createQuery(hql, City.class);
        query.setParameter("name", name);
        query.setMaxResults(1);
        return (City) query.getSingleResult();
    }
}
