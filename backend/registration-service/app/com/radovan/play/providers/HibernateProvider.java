package com.radovan.play.providers;

import com.google.inject.Provider;
import com.radovan.play.utils.HibernateUtil;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;
import play.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

public class HibernateProvider implements Provider<SessionFactory> {

    private final HibernateUtil hibernateUtil;
    private final SessionFactory sessionFactory;

    @Inject
    public HibernateProvider(ApplicationLifecycle lifecycle) {
        this.hibernateUtil = new HibernateUtil();
        this.sessionFactory = hibernateUtil.getSessionFactory();

        // registrujemo shutdown hook
        lifecycle.addStopHook(() -> {
            hibernateUtil.shutdown();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public SessionFactory get() {
        return sessionFactory;
    }
}
