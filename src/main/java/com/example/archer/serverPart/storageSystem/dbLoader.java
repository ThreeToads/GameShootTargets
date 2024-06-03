package com.example.archer.serverPart.storageSystem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class dbLoader {
    public dbPlayer findByNickname(String nickname) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            final Query<dbPlayer> query = session.createQuery(
                    "from dbPlayer where nickname=:nick",
                    dbPlayer.class
            );
            query.setParameter("nick", nickname);
            return query.uniqueResult();
        }
    }

    public List<dbPlayer> getTopPlayers(int count) {
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            final var query = session.createQuery(
                    "from dbPlayer player order by player.rank desc",
                    dbPlayer.class
            );
            query.setMaxResults(count);
            return query.getResultList();
        }
    }
}
