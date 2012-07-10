/*
 * The MIT License
 *
 * Copyright 2012 mpower.
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
package org.boazglean.kathab.api;

import java.sql.*;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author mpower
 */
@Test
@Slf4j
public class H2DatabaseTest {

    private Connection connection;

    @BeforeMethod
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:;TRACE_LEVEL_FILE=4");
    }

    @AfterMethod
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void testH2Connect() throws Exception {
        String sql = ""
                + "begin;\n"
                + "CREATE TABLE sample_table (sample_table_id INTEGER, x_cord INTEGER, y_cord INTEGER, uuid UUID);\n"
                + "INSERT INTO sample_table (sample_table_id, x_cord, y_cord, uuid) VALUES (0, 0, 0, 0);\n"
                + "commit;\n"
                + "";
        String select = ""
                + "select * from sample_table;\n"
                + "";
        Statement ddl = connection.createStatement();
        assertFalse(ddl.execute(sql));
        ddl.close();
        Statement dql = connection.createStatement();
        ResultSet query = dql.executeQuery(select);
        ResultSetMetaData queryData = query.getMetaData();
        while(query.next()) {
            for(int columnIndex = 1; columnIndex <= queryData.getColumnCount(); ++columnIndex) {
                log.info("Column Name, index: {}, name: {}", columnIndex, queryData.getColumnName(columnIndex));
                log.info("Column Label, index: {}, label: {}", columnIndex, queryData.getColumnLabel(columnIndex));
                log.info("Column Type, index: {}, type: {}", columnIndex, queryData.getColumnType(columnIndex));
                log.info("Column Class Name, index: {}, name: {}", columnIndex, queryData.getColumnClassName(columnIndex));
                log.info("Column Type Name, index: {}, typeName: {}", columnIndex, queryData.getColumnTypeName(columnIndex));
                log.info("Column Object, index: {}, object: {}", columnIndex, query.getObject(columnIndex));
                log.info("Column Object Type, index: {}, object type: {}", columnIndex, query.getObject(columnIndex).getClass());
            }
            
        }
        log.info("byte [] class name: {}", new byte[] {}.getClass());
    }
}
