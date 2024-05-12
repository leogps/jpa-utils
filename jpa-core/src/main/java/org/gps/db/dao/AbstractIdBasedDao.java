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

import lombok.Getter;
import lombok.Setter;
import org.gps.db.IdBasedEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * An abstract implementation of {@link Dao} for {@link org.gps.db.IdBasedEntity}
 *
 */
@Setter
@Getter
public abstract class AbstractIdBasedDao<T extends IdBasedEntity> extends AbstractDao<T> implements IdBasedDao<T> {

    protected Class<T> typeParamEntityClass;

    @Transactional(readOnly = true)
    public T findById(Long id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    /**
	 * Counts total number of records when no filter applied.
	 */
	@Transactional(readOnly = true)
	protected Long countTotal() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(getEntityClass())));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
	public boolean isDetached(T entity) {
		return entity.getId() != null  // must not be transient
				&& !getEntityManager().contains(entity)  // must not be managed now
				&& findByPrimaryKey(entity) != null;  // must not have been removed
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
	@SuppressWarnings("unchecked")
	public T findByPrimaryKey(T entity) {
		return (T) getEntityManager().find(entity.getClass(), entity.getId());
	}
}
