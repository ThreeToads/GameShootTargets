package com.example.GameShoots.serverPart.storageSystem;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionFactory {

    // Статическое поле для хранения единственного экземпляра SessionFactory
    private static SessionFactory sessionFactory;

    // Приватный конструктор для предотвращения создания экземпляров этого класса
    private HibernateSessionFactory() {}

    // Метод для получения SessionFactory
    public static SessionFactory getSessionFactory() {
        // Проверяем, инициализировано ли поле sessionFactory
        if (sessionFactory == null) {
            try {
                // Создаём объект Configuration и загружаем настройки из hibernate.cfg.xml
                Configuration configuration = new Configuration().configure();

                // Добавляем аннотированный класс dbPlayer
                configuration.addAnnotatedClass(dbPlayer.class);

                // Создаём ServiceRegistry с настройками из Configuration
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());

                // Создаём SessionFactory из Configuration и ServiceRegistry
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                // Обработка исключений при инициализации SessionFactory
                System.out.println("Exception: " + e.getMessage());
            }
        }
        // Возвращаем единственный экземпляр SessionFactory
        return sessionFactory;
    }
}

