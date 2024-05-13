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

package org.gps.db;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

@Data
public class Context {

    private final Map<String, Field> cache;

    public Context(final Map<String, Field> cache) {
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    public <K extends Serializable, T extends Serializable> K getPrimaryKeyValue(T entity) {
        String className = entity.getClass().getName();
        if (!cache.containsKey(className)) {
            return null;
        }
        Field field = cache.get(className);
        field.setAccessible(true);
        try {
            return (K) field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Serializable> Field getPrimaryKeyField(Class<T> entityClass) {
        String className = entityClass.getName();
        if (!cache.containsKey(className)) {
            return null;
        }
        return cache.get(className);
    }
}
