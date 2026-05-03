package dao;

import config.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class GenericHibernateDao<T, ID extends Serializable>
        implements GenericDao<T, ID> {

    private final Class<T> entityClass;

    protected GenericHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Template Method: ejecuta operaciones transaccionales
     */
    protected <R> R executeInTransaction(TransactionOperation<R> operation) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            R result = operation.execute(session);
            transaction.commit();
            return result;
        } catch (Exception ex) {
            rollback(transaction);
            throw new RuntimeException("Error en operación transaccional", ex);
        }
    }

    /**
     * Interfaz funcional para operaciones transaccionales
     */
    @FunctionalInterface
    protected interface TransactionOperation<R> {
        R execute(Session session) throws Exception;
    }

    @Override
    public T save(T entity) {
        return executeInTransaction(session -> {
            session.persist(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return executeInTransaction(session -> {
            return session.merge(entity);
        });
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(session -> {
            T managedEntity = session.contains(entity) ?
                    entity : session.merge(entity);
            session.remove(managedEntity);
            return null;
        });
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

