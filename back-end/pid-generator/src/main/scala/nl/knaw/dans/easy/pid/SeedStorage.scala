package nl.knaw.dans.easy.pid

import java.io.File
import org.apache.commons.io.FileUtils._
import scala.util.Try
import java.io.IOException
import scala.util.Success
import scala.util.Failure
import org.hibernate.cfg.Configuration
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.HibernateException
import javax.persistence.Entity
import scala.beans.BeanProperty
import org.slf4j.LoggerFactory

sealed trait SeedStorage {
  /**
   * Calculates the next PID seed from the previously stored one and makes
   * sure that it is persisted. Returns a Failure if there is no next PID seed or
   * if the new seed could not be persisted
   */
  def calculateAndPersist(f: Long => Option[Long]): Try[Long]
}

@Entity
class Seed() {
  @BeanProperty
  var pidType: String = null

  @BeanProperty
  var value: Long = Long.MinValue
}

case class RanOutOfSeeds() extends Exception

case class DbBasedSeedStorage(key: String, first: Long, hibernateConfig: File) extends SeedStorage {
  val log = LoggerFactory.getLogger(classOf[DbBasedSeedStorage])
  val conf = new Configuration().configure(hibernateConfig);
  val regBuilder = new StandardServiceRegistryBuilder()
  regBuilder.applySettings(conf.getProperties());
  val serviceRegistry = regBuilder.build();
  val sessionFactory = conf.buildSessionFactory(serviceRegistry);

  override def calculateAndPersist(f: Long => Option[Long]): Try[Long] = {
    val session = sessionFactory.getCurrentSession
    session.beginTransaction()
    try {
      session.get(classOf[Seed], key) match {
        case seed: Seed =>
          f(seed.value) match {
            case Some(next) =>
              val seed = new Seed
              seed.pidType = key
              seed.value = next
              session.merge(seed)
              session.getTransaction.commit()
              Success(next)
            case None => Failure(RanOutOfSeeds())
          }
        case _ =>
          log.warn("NO PREVIOUS PID FOUND. THIS SHOULD ONLY HAPPEN ONCE!! INITIALIZING WITH INITIAL SEED FOR {}", key)
          log.info("Initializing seed with value {}", first)
          val seed = new Seed
          seed.pidType = key
          seed.value = first
          session.save(seed)
          session.getTransaction.commit()
          Success(first)
      }
    } catch {
      case e: HibernateException =>
        log.error("Database error", e)
        session.getTransaction.rollback()
        Failure(e)
    }
  }
}
