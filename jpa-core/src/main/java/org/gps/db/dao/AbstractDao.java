/*
 * Copyright (c) 2024, Paul Gundarapu.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gps.db.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gps.db.Context;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation of the {@link Dao}.
 *
 * @param <T>
 */
@Repository
@Slf4j
@Getter
@Setter
public abstract class AbstractDao<T extends Serializable> implements Dao<T> {

    protected final Context context;

    public AbstractDao(Context context) {
        this.context = context;
    }

	@PersistenceContext
	private EntityManager entityManager;

    /**
     * Returns JPA {@link EntityManager}.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    protected EntityManager getEntityManager() {
	    return entityManager;
    }

    /**
     * Entity class the Dao implementation is concerned with.
     */
    public abstract Class<T> getEntityClass();

    /**
     * Retrieves all entities.
     */
	@Override
    @Transactional(readOnly = true)
	public List<T> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
        Root<T> rootEntry = cq.from(getEntityClass());
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = getEntityManager().createQuery(all);
        return allQuery.getResultList();
    }

	@Override
    @Transactional(readOnly = true)
	public <K extends Serializable> Boolean isExists(K value) {
        Field field = context.getPrimaryKeyField(getEntityClass());
        if (field == null) {
            throw new IllegalStateException(String.format("Field with @PrimaryKey annotation not found for Entity: %s. " +
                            "Or please (re-)scan the package containing the entityClass.",
                    getEntityClass()));
        }
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Boolean> query = cb.createQuery(Boolean.class);
        Root<T> root = query.from(getEntityClass());
        query.select(cb.literal(true));
        Predicate predicate = cb.equal(root.get(field.getName()), value);
        query.where(predicate);

        TypedQuery<Boolean> typedQuery = getEntityManager().createQuery(query);
        return typedQuery.getResultList().stream().findFirst().orElse(false);
	}

    @Transactional
    @Override
    public void persist(T entity) {
        log.debug(" persist(). EntityName: {}", entity.getClass().getName());
	    EntityManager entityManager = getEntityManager();
	    if(!isDetached(entity) && !entityManager.contains(entity)) {
	        entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Transactional
    @Override
    public void persist(Collection<T> tCollection) {
        for(T t : tCollection) {
            persist(t);
        }
    }

    /**
     * Deletes the entity.
     */
    @Transactional
    @Override
    public void delete(T entity) {
        log.debug("delete entity -> {}", entity.getClass().getName());
    	EntityManager entityManager = getEntityManager();
    	if(!isDetached(entity) && !entityManager.contains(entity)) {
    	    entityManager.remove(entity);
        } else {
    	    entityManager.remove(findByEntity(entity));
        }
    }

    @Transactional
    public void saveOrUpdate(T t) {
        persist(t);
    }

    /**
     * Checks if the Entity is detached from the current {@link EntityManager}.
     */
    public abstract boolean isDetached(T entity);

    /**
     * Find the entity from the DB.
     */
    public abstract T findByEntity(T entity);
}
