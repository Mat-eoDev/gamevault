package com.gamevault.repository;

import com.gamevault.config.HibernateUtil;
import com.gamevault.model.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GameRepository {

    private static final Logger log = LoggerFactory.getLogger(GameRepository.class);

    public void save(Game game) {
        runInTransaction(session -> {
            if (game.getId() == null) {
                session.persist(game);
            } else {
                session.merge(game);
            }
        });
    }

    public void delete(Game game) {
        runInTransaction(session -> {
            Game managed = session.contains(game) ? game : session.merge(game);
            session.remove(managed);
        });
    }

    public Optional<Game> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Game.class, id));
        } catch (Exception e) {
            log.error("Erreur lors de la recherche du jeu id={}", id, e);
            return Optional.empty();
        }
    }

    public List<Game> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Game ORDER BY title", Game.class).list();
        } catch (Exception e) {
            log.error("Erreur lors du chargement de la collection", e);
            return Collections.emptyList();
        }
    }

    public List<Game> search(String query) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String pattern = "%" + query.toLowerCase() + "%";
            return session.createQuery(
                    "FROM Game WHERE LOWER(title) LIKE :q OR LOWER(platform) LIKE :q OR LOWER(developer) LIKE :q ORDER BY title",
                    Game.class
            ).setParameter("q", pattern).list();
        } catch (Exception e) {
            log.error("Erreur lors de la recherche '{}'", query, e);
            return Collections.emptyList();
        }
    }

    private void runInTransaction(SessionConsumer action) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            log.error("Transaction échouée", e);
            throw new RuntimeException("Erreur base de données", e);
        }
    }

    @FunctionalInterface
    private interface SessionConsumer {
        void accept(Session session);
    }
}
