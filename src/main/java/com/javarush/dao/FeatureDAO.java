package com.javarush.dao;

import com.javarush.domain.enums.Feature;
import org.hibernate.SessionFactory;

public class FeatureDAO extends GenericDAO<Feature> {
    public FeatureDAO(SessionFactory sessionFactory) {
        super(Feature.class, sessionFactory);
    }
}
