package org.example;

import org.hibernate.cfg.Configuration;

public class SessionFactory {

    private static org.hibernate.SessionFactory sessionFactory = null;

    public static org.hibernate.SessionFactory getSessionFactory() {

        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        return sessionFactory;
    }
}
