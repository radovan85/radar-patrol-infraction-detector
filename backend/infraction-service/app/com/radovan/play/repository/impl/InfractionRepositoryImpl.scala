package com.radovan.play.repository.impl

import com.radovan.play.entity.InfractionEntity
import com.radovan.play.repository.InfractionRepository
import com.radovan.play.services.PrometheusService
import jakarta.inject.{Inject, Singleton}
import jakarta.persistence.criteria.{CriteriaBuilder, CriteriaQuery, Predicate, Root}
import org.hibernate.{Session, SessionFactory}

import scala.jdk.CollectionConverters._

@Singleton
class InfractionRepositoryImpl extends InfractionRepository{

  private var sessionFactory:SessionFactory = _
  private var prometheusService:PrometheusService = _

  @Inject
  private def initialize(sessionFactory: SessionFactory,prometheusService: PrometheusService):Unit = {
    this.sessionFactory = sessionFactory
    this.prometheusService = prometheusService
  }

  private def withSession[T](block: Session => T): T = {
    prometheusService.updateDatabaseQueryCount()
    val session = sessionFactory.openSession()
    val transaction = session.beginTransaction()

    try {
      val result = block(session)
      transaction.commit()
      result
    } catch {
      case e: Exception =>
        transaction.rollback()
        throw e
    } finally {
      session.close()
    }
  }

  override def save(infractionEntity: InfractionEntity): InfractionEntity = {
    withSession { session =>
      if (infractionEntity.getId() == null) {
        session.persist(infractionEntity)
      } else {
        session.merge(infractionEntity)
      }
      session.flush()
      infractionEntity
    }
  }

  override def findById(infractionId: Long): Option[InfractionEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[InfractionEntity] = cb.createQuery(classOf[InfractionEntity])
      val root: Root[InfractionEntity] = cq.from(classOf[InfractionEntity])
      val predicates: Array[Predicate] = Array(cb.equal(root.get("id"), infractionId))
      cq.where(predicates: _*)
      val results = session.createQuery(cq).getResultList.asScala.toList
      results.headOption
    }
  }

  override def findAll: Array[InfractionEntity] = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[InfractionEntity] = cb.createQuery(classOf[InfractionEntity])
      val root: Root[InfractionEntity] = cq.from(classOf[InfractionEntity])
      cq.select(root)
      session.createQuery(cq).getResultList.asScala.toArray
    }
  }

  override def deleteAllByRadarId(radarId: Long): Unit = {
    withSession { session =>
      val cb: CriteriaBuilder = session.getCriteriaBuilder
      val cq: CriteriaQuery[InfractionEntity] = cb.createQuery(classOf[InfractionEntity])
      val root: Root[InfractionEntity] = cq.from(classOf[InfractionEntity])
      val predicate: Predicate = cb.equal(root.get("radarId"), radarId)
      cq.select(root).where(Array(predicate): _*)
      val infractions = session.createQuery(cq).getResultList.asScala
      infractions.foreach(session.remove)
      session.flush()
    }
  }

}
