package com.example.GameShoots.serverPart.storageSystem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class dbLoader {

    // Метод для поиска игрока по никнейму
    public dbPlayer findByNickname(String nickname) {
        // Открываем сессию с использованием фабрики сессий Hibernate
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            // Создаём запрос для поиска игрока по никнейму
            final Query<dbPlayer> query = session.createQuery(
                    "from dbPlayer where nickname=:nick", // HQL запрос
                    dbPlayer.class
            );
            // Устанавливаем параметр "nick" в запросе
            query.setParameter("nick", nickname);
            // Возвращаем уникальный результат запроса (ожидается один игрок с таким никнеймом)
            return query.uniqueResult();
        }
    }

    // Метод для получения списка топ-игроков
    public List<dbPlayer> getTopPlayers(int count) {
        // Открываем сессию с использованием фабрики сессий Hibernate
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            // Создаём запрос для получения игроков, отсортированных по рангу
            final var query = session.createQuery(
                    "from dbPlayer player order by player.rank desc", // запрос
                    dbPlayer.class
            );
            // Устанавливаем максимальное количество возвращаемых результатов
            query.setMaxResults(count);
            // Возвращаем список результатов запроса
            return query.getResultList();
        }
    }
}

