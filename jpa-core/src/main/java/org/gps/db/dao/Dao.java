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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Base Dao definition.
 */
public interface Dao<T extends Serializable> {

    /**
     * Count total number of records.
     */
    Long countTotal();

    /**
     * Finds all the resources.
     *
     */
    List<T> findAll();

    /**
     * Checks if the entity with primary-key exists.
     */
    <K extends Serializable> Boolean isExists(K value);

    /**
     * Persists the entity.
     *
     */
    void persist(T t);

    /**
     * Persists the collection of entities.
     */
    void persist(Collection<T> tCollection);

    /**
     * Deletes the entity.
     *
     */
    void delete(T entity);
}
