package com.radovan.spring.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.RadarEntity;
import com.radovan.spring.repository.RadarRepository;
import com.radovan.spring.services.PrometheusService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class RadarRepositoryImpl implements RadarRepository {

	private SessionFactory sessionFactory;
	private PrometheusService prometheusService;

	@Autowired
	private void initialize(SessionFactory sessionFactory, PrometheusService prometheusService) {
		this.sessionFactory = sessionFactory;
		this.prometheusService = prometheusService;
	}

	private <T> T withSession(Function<Session, T> function) {
		prometheusService.updateDatabaseQueryCount();
		try (Session session = sessionFactory.openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				T returnValue = function.apply(session);
				tx.commit();
				return returnValue;
			} catch (Exception exc) {
				tx.rollback();
				throw exc;
			}
		}
	}

	@Override
	public List<RadarEntity> findAllByName(String keyword) {
		return withSession(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<RadarEntity> query = cb.createQuery(RadarEntity.class);
			Root<RadarEntity> root = query.from(RadarEntity.class);

			// WHERE LOWER(name) LIKE LOWER('%keyword%')
			query.select(root).where(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));

			return session.createQuery(query).getResultList();
		});
	}

	@Override
	public RadarEntity save(RadarEntity radarEntity) {
		return withSession(session -> {
			if (radarEntity.getId() == null) {
				session.persist(radarEntity);
			} else {
				session.merge(radarEntity);
			}

			session.flush();
			return radarEntity;
		});
	}

	@Override
	public Optional<RadarEntity> findById(Long radarId) {
		return withSession(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<RadarEntity> query = cb.createQuery(RadarEntity.class);
			Root<RadarEntity> root = query.from(RadarEntity.class);
			query.where(cb.equal(root.get("id"), radarId));
			List<RadarEntity> resultList = session.createQuery(query).getResultList();
			return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
		});
	}

	@Override
	public void deleteById(Long radarId) {
		withSession(session -> {
			RadarEntity radarEntity = session.find(RadarEntity.class, radarId);
			if (radarEntity != null) {
				session.remove(radarEntity);
			}

			return null;
		});

	}

	@Override
	public List<RadarEntity> findAll() {
		return withSession(session -> {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<RadarEntity> query = cb.createQuery(RadarEntity.class);
			Root<RadarEntity> root = query.from(RadarEntity.class);
			query.select(root);
			return session.createQuery(query).getResultList();
		});
	}

	@Override
	public List<RadarEntity> findAllActive() {
	    return withSession(session -> {
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<RadarEntity> query = cb.createQuery(RadarEntity.class);
	        Root<RadarEntity> root = query.from(RadarEntity.class);

	        query.select(root).where(cb.equal(root.get("status"), (byte) 1));

	        return session.createQuery(query).getResultList();
	    });
	}


}
