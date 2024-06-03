package com.example.GameShoots.serverPart.storageSystem;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class dbSaver {

    // Метод для сохранения нового игрока в базу данных
    public void save(dbPlayer player) {
        // Открываем сессию с использованием фабрики сессий Hibernate
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        }
    }

    // Метод для обновления существующего игрока в базе данных
    public void update(dbPlayer player) {
        // Открываем сессию с использованием фабрики сессий Hibernate
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            // Начинаем транзакцию
            Transaction transaction = session.beginTransaction();
            // Обновляем объект игрока в сессии
            session.merge(player);
            // Коммитим транзакцию, чтобы изменения были сохранены в базе данных
            transaction.commit();
        }
    }
}

