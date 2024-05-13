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

package org.gps.jpa.utils.test;import lombok.extern.slf4j.Slf4j;
import org.gps.db.Context;
import org.gps.jpa.utils.SampleApp;
import org.gps.jpa.utils.dao.PersonDao;
import org.gps.jpa.utils.entity.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SampleApp.class)
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
public class BasicDaoTest {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private Context context;

    @After
    public void tearDown() {
        for (Person person : personDao.findAll()) {
            personDao.delete(person);
        }
    }

    @Test
    public void testPersist() {
        Person person = new Person();
        person.setName("Jason Bourne");
        personDao.persist(person);

        Assert.assertNotNull(person.getId());

        Person retrieved = personDao.findByPrimaryKey(person.getId());
        Assert.assertEquals(person.getId(), retrieved.getId());
        Assert.assertEquals(person.getName(), retrieved.getName());
    }

    @Test
    public void testDelete() {
        Person person = new Person();
        person.setName("James Bond");
        personDao.persist(person);

        Long id = person.getId();
        Assert.assertNotNull(id);

        personDao.delete(person);

        boolean exists = personDao.isExists(id);
        Assert.assertFalse(exists);

        Person retrieved = personDao.findByPrimaryKey(id);
        Assert.assertNull(retrieved);
    }

    @Test
    public void testUpdate() {
        Person person = new Person();
        person.setName("Marilyn Monroe");
        personDao.persist(person);

        Assert.assertNotNull(person.getId());

        String alias = "Norma Jeane Mortenson";
        person.setName(alias);
        personDao.saveOrUpdate(person);

        Person retrieved = personDao.findByPrimaryKey(person.getId());
        Assert.assertEquals(alias, retrieved.getName());
    }

    @Test
    public void countTotalTest() {
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setName("person_" + i);
            personDao.persist(person);
            Assert.assertNotNull(person.getId());
        }

        long count = personDao.countTotal();
        Assert.assertEquals(100L, count);
    }

    @Test
    public void testContext() {
        Assert.assertNotNull(context.getCache());
        for (String key : context.getCache().keySet()) {
            Field field = context.getCache().get(key);
            log.info("{} --> {}", key, field);
        }
    }
}
