package com.radovan.play.repositories.impl;

import com.radovan.play.entity.OwnerEntity;
import com.radovan.play.repositories.OwnerRepository;
import com.radovan.play.services.PrometheusService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Singleton
public class OwnerRepositoryImpl implements OwnerRepository {

    private SessionFactory sessionFactory;
    private PrometheusService prometheusService;

    @Inject
    private void initialize(SessionFactory sessionFactory, PrometheusService prometheusService) {
        this.sessionFactory = sessionFactory;
        this.prometheusService = prometheusService;
    }

    // Generic method for handling transactions with SessionFactory
    private <T> T withSession(Function<Session, T> function) {
        prometheusService.updateDatabaseQueryCount();
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = function.apply(session);
                tx.commit();
                return result;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public Optional<OwnerEntity> findById(Long ownerId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<OwnerEntity> query = cb.createQuery(OwnerEntity.class);
            Root<OwnerEntity> root = query.from(OwnerEntity.class);
            query.where(cb.equal(root.get("id"), ownerId));
            List<OwnerEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        });
    }

    @Override
    public Optional<OwnerEntity> findByEmail(String email) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<OwnerEntity> query = cb.createQuery(OwnerEntity.class);
            Root<OwnerEntity> root = query.from(OwnerEntity.class);
            query.where(cb.equal(root.get("email"), email));
            List<OwnerEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        });
    }

    @Override
    public OwnerEntity save(OwnerEntity ownerEntity) {
        return withSession(session -> {
            if(ownerEntity.getId()==null){
                session.persist(ownerEntity);
            }else {
                session.merge(ownerEntity);
            }

            session.flush();
            return ownerEntity;
        });
    }

    @Override
    public void deleteById(Long ownerId) {
        withSession(session -> {
            OwnerEntity ownerEntity = session.find(OwnerEntity.class,ownerId);
            if(ownerEntity!=null){
                session.remove(ownerEntity);
            }

            return null;
        });
    }

    @Override
    public List<OwnerEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<OwnerEntity> query = cb.createQuery(OwnerEntity.class);
            Root<OwnerEntity> root = query.from(OwnerEntity.class);
            query.select(root);
            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public Long count() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<OwnerEntity> root = query.from(OwnerEntity.class);
            query.select(cb.count(root));
            return session.createQuery(query).getSingleResult();
        });
    }

}
