/*
 * Copyright (c) "2024", Paul Gundarapu.
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

package org.gps.dao;

import org.gps.db.Context;
import org.gps.db.dao.AbstractPrimaryKeyBasedDao;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * {@link HibernatePrimaryKeyBasedDao} provides Hibernate based JPA capabilities on {@link AbstractPrimaryKeyBasedDao}
 */
@Repository
public abstract class HibernatePrimaryKeyBasedDao<K extends Serializable, T extends Serializable>
        extends AbstractPrimaryKeyBasedDao<K, T> {

    public HibernatePrimaryKeyBasedDao(Context context) {
        super(context);
    }

    /**
     * Returns Hibernate {@link org.hibernate.Session} from the JPA Factory.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }
}
