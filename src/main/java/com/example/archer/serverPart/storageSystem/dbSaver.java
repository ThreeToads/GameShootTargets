package com.example.archer.serverPart.storageSystem;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class dbSaver {
    public void save(dbPlayer player) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        }
    }

    public void update(dbPlayer player) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(player);
            transaction.commit();
        }
    }
}
