package com.gamevault.repository;

import com.gamevault.config.HibernateUtil;
import com.gamevault.model.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class GameRepository {

    private static final Logger log = LoggerFactory.getLogger(GameRepository.class);

    public void save(Game game) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (game.getId() == null) {
                session.persist(game);
            } else {
                session.merge(game);
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            log.error("Impossible d'enregistrer le jeu", e);
            throw e;
        }
    }

    public void delete(Game game) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Game managedGame = session.get(Game.class, game.getId());
            if (managedGame != null) {
                session.remove(managedGame);
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            log.error("Impossible de supprimer le jeu", e);
            throw e;
        }
    }

    public Optional<Game> findById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Game game = session.get(Game.class, id);
            tx.commit();
            return Optional.ofNullable(game);
        } catch (RuntimeException e) {
            rollback(tx);
            log.error("Impossible de trouver le jeu avec l'id {}", id, e);
            return Optional.empty();
        }
    }

    public List<Game> findAll() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            List<Game> games = session.createQuery("FROM Game ORDER BY title", Game.class).list();
            tx.commit();
            return games;
        } catch (RuntimeException e) {
            rollback(tx);
            log.error("Impossible de charger la liste des jeux", e);
            return List.of();
        }
    }

    public List<Game> search(String query) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String normalizedQuery = "%" + query.toLowerCase() + "%";
            List<Game> games = session.createQuery(
                    "FROM Game g "
                            + "WHERE LOWER(g.title) LIKE :q "
                            + "OR LOWER(g.platform) LIKE :q "
                            + "OR LOWER(g.developer) LIKE :q "
                            + "ORDER BY g.title",
                    Game.class)
                    .setParameter("q", normalizedQuery)
                    .list();
            tx.commit();
            return games;
        } catch (RuntimeException e) {
            rollback(tx);
            log.error("Impossible de rechercher des jeux avec la requête '{}'", query, e);
            return List.of();
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
