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

package org.gps.db.test;

import lombok.Data;
import org.gps.db.Context;
import org.gps.db.PrimaryKey;
import org.gps.db.scan.Scanner;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

public class ScannerTest {

    @Test
    public void testScan() throws IOException, ClassNotFoundException {
        Context context = new Scanner().scan("org.gps.db.test");
        for (String className : context.getCache().keySet()) {
            System.out.println(className);
        }

        MyEntity entity = new MyEntity();
        entity.setId(1234L);
        Object value = context.getPrimaryKeyValue(entity);
        Assert.assertEquals(1234L, value);
    }
}

@Data
class MyEntity implements Serializable {

    @PrimaryKey
    private Long id;
}