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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.gps.db.Context;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * An abstract implementation of {@link Dao} for {@link jakarta.persistence.Entity} that has
 * {@link org.gps.db.PrimaryKey}
 *
 */
@Repository
@Setter
@Getter
public abstract class AbstractPrimaryKeyBasedDao<K extends Serializable, T extends Serializable>
		extends AbstractDao<T>
		implements PrimaryKeyBasedDao<K, T> {

    protected Class<T> typeParamEntityClass;

	public AbstractPrimaryKeyBasedDao(Context context) {
		super(context);
	}

	@Transactional(readOnly = true)
	@Override
    public T findByPrimaryKey(K primaryKey) {
        return getEntityManager().find(getEntityClass(), primaryKey);
    }

	@Transactional(readOnly = true)
	@Override
	public Long countTotal() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(getEntityClass())));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
	public boolean isDetached(T entity) {
		Object primaryKeyValue = context.getPrimaryKeyValue(entity);
		return primaryKeyValue != null
				&& !getEntityManager().contains(entity)  // must not be managed now
				&& findByEntity(entity) != null;  // must not have been removed
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
	@SuppressWarnings("unchecked")
	public T findByEntity(T entity) {
		Object primaryKeyValue = context.getPrimaryKeyValue(entity);
		return (T) getEntityManager().find(entity.getClass(), primaryKeyValue);
	}
}
