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

package org.gps.db.scan;

import lombok.extern.slf4j.Slf4j;
import org.gps.db.Context;
import org.gps.db.PrimaryKey;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Component
@Slf4j
public class Scanner {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private final Config config;

    public Scanner(Config config) {
        this.config = config;
    }

    public Scanner() {
        this.config = new Config();
    }

    @SuppressWarnings("unchecked")
    public Context scan(String... basePackages) throws IOException, ClassNotFoundException {
        if (basePackages == null || basePackages.length == 0) {
            return new Context(Collections.EMPTY_MAP);
        }

        Map<String, Field> cache = new HashMap<>();
        Set<Class<?>> classes = getClassesFromPackage(basePackages);
        for (Class<?> clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            List<Field> pkFields = new ArrayList<>();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    pkFields.add(field);
                    cache.put(clazz.getName(), field);
                }
            }
            if (pkFields.size() > 1) {
                if (config.isFailFast()) {
                    throw new IllegalStateException(String.format("Warning: Class %s has more than one PrimaryKey annotated field.", clazz));
                }
                log.warn("Warning: Class {} has more than one PrimaryKey annotated field.", clazz.getName());
            }
        }

        return new Context(cache);
    }

    public Set<Class<?>> getClassesFromPackage(String... basePackages) throws IOException, ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.setMetadataReaderFactory(new CachingMetadataReaderFactory());

        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage: basePackages) {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource: resources) {
                MetadataReader reader = scanner.getMetadataReaderFactory().getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }
}
