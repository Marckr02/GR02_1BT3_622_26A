package dao;

import config.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class GenericHibernateDao<T, ID extends Serializable> implements GenericDao<T, ID> {

    private final Class<T> entityClass;

    protected GenericHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception ex) {
            rollback(transaction);
            throw ex;
        }
    }

    @Override
    public T update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T managedEntity = session.merge(entity);
            transaction.commit();
            return managedEntity;
        } catch (Exception ex) {
            rollback(transaction);
            throw ex;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T managedEntity = session.contains(entity) ? entity : session.merge(entity);
            session.remove(managedEntity);
            transaction.commit();
        } catch (Exception ex) {
            rollback(transaction);
            throw ex;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(entityClass, id));
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from " + entityClass.getSimpleName();
            return session.createQuery(hql, entityClass).getResultList();
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}

