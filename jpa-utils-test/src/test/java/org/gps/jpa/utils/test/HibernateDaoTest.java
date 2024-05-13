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
import org.gps.jpa.utils.SampleApp;
import org.gps.jpa.utils.dao.PlanetDao;
import org.gps.jpa.utils.entity.Planet;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SampleApp.class)
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
public class HibernateDaoTest {

    @Autowired
    private PlanetDao planetDao;

    @After
    public void tearDown() {
        for (Planet planet : planetDao.findAll()) {
            planetDao.delete(planet);
        }
    }

    @Test
    @Transactional
    public void testHibernateDao() {
        Session session = planetDao.getSession();
        Assert.assertNotNull(session);

        Planet planet = new Planet();
        planet.setName("Jupiter");
        double distance = 444 * Math.pow(10, 6);
        planet.setDistanceFromEarth(distance);

        session.persist(planet);

        Assert.assertNotNull(planet.getUid());

        Planet retrieved = session.find(Planet.class, planet.getUid());
        Assert.assertEquals(planet.getName(), retrieved.getName());
        Assert.assertEquals(planet.getDistanceFromEarth(), retrieved.getDistanceFromEarth(), 0.0);
    }
}
