package com.radovan.play.repositories.impl;

import com.radovan.play.entity.VehicleEntity;
import com.radovan.play.repositories.VehicleRepository;
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
public class VehicleRepositoryImpl implements VehicleRepository {

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
    public Optional<VehicleEntity> findById(Long vehicleId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> query = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);
            query.where(cb.equal(root.get("id"), vehicleId));
            List<VehicleEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        });
    }

    @Override
    public VehicleEntity save(VehicleEntity vehicleEntity) {
        return withSession(session -> {
            if(vehicleEntity.getId()==null){
                session.persist(vehicleEntity);
            }else {
                session.merge(vehicleEntity);
            }

            session.flush();
            return vehicleEntity;
        });
    }

    @Override
    public void deleteById(Long vehicleId) {
        withSession(session -> {
            VehicleEntity vehicleEntity = session.find(VehicleEntity.class,vehicleId);
            if(vehicleEntity!=null){
                session.remove(vehicleEntity);
            }

            return null;
        });
    }

    @Override
    public List<VehicleEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> query = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);
            query.select(root);
            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public Long count() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);
            query.select(cb.count(root));
            return session.createQuery(query).getSingleResult();
        });
    }

    @Override
    public Optional<VehicleEntity> findByRegistrationNumber(String registrationNumber) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> query = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);
            query.where(cb.equal(root.get("registrationNumber"), registrationNumber));
            List<VehicleEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        });
    }

    @Override
    public List<VehicleEntity> findByRegistrationNumberContaining(String keyword) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> query = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);
            query.select(root)
                    .where(cb.like(root.get("registrationNumber"), "%" + keyword + "%"));
            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public List<VehicleEntity> findAllByOwnerId(Long ownerId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<VehicleEntity> query = cb.createQuery(VehicleEntity.class);
            Root<VehicleEntity> root = query.from(VehicleEntity.class);

            query.select(root)
                    .where(cb.equal(root.get("owner").get("id"), ownerId));

            return session.createQuery(query).getResultList();
        });
    }


}
