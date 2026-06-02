package com.gamevault.config;

import com.gamevault.util.AppConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    private HibernateUtil() {}

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration cfg = new Configuration().configure();
                String dbPath = AppConfig.get("db.path", "gamevault.db");
                cfg.setProperty("hibernate.connection.url", "jdbc:sqlite:" + dbPath);
                cfg.setProperty("hibernate.show_sql", AppConfig.get("hibernate.show_sql", "false"));
                cfg.setProperty("hibernate.hbm2ddl.auto", AppConfig.get("hibernate.hbm2ddl.auto", "update"));
                sessionFactory = cfg.buildSessionFactory();
                log.info("Hibernate initialisé (db: {})", dbPath);
            } catch (Exception e) {
                log.error("Échec de l'initialisation Hibernate", e);
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("Hibernate fermé");
        }
    }
}
