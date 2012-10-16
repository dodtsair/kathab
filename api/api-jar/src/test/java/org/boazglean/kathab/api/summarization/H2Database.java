/*
 * The MIT License
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

package org.boazglean.kathab.api.summarization;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * User: mpower
 * Date: 10/15/12
 * Time: 9:38 AM
 */
@Slf4j
public class H2Database {

    private Server server;
    private JdbcDataSource jdbcSource;

    @BeforeClass
    public void setupDebugServer() throws Exception {
        server = Server.createTcpServer("-tcpDaemon", "-tcpAllowOthers");
        server.start();
    }

    @AfterClass
    public void shutdownDebugServer() throws Exception {
        server.stop();
    }
    @BeforeMethod
    public void setup(Method method) throws Exception {
        String dbName = method.getDeclaringClass().getSimpleName();

        jdbcSource = new JdbcDataSource();
        // DB_CLOSE_DELAY during the test we are not going to always have a connection open, so make sure the db stays
        // even when there is no connection
        jdbcSource.setURL("jdbc:h2:tcp://localhost/mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        jdbcSource.setUser(dbName);
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        Schema schema = new Schema();
        schema.initSchema(connection);
    }

    @Test
    public void test() throws Exception {
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        PreparedStatement statement = connection.prepareStatement(
                "SELECT range.slice as slice, count(TIMESTMP) as eventCount from (select X * 2 as slice FROM SYSTEM_RANGE(0,99)) as range " +
                "left join (select C1 as slice, C2 as TIMESTMP from VALUES (1, 2), (2, 4), (3, 6), (4, 8)) as data on data.slice = range.slice group by range.slice order by range.slice"
        );
        ResultSet result = statement.executeQuery();
        ResultSetMetaData metaData = result.getMetaData();
        log.info("Query result: {}", result);
        StringBuilder columns = new StringBuilder("Columns: ");
        for(int index = 1; index <= metaData.getColumnCount(); ++index) {
            columns.append(result.getMetaData().getColumnName(index)).append(", ");
        }
        while(result.next()) {
            StringBuilder row = new StringBuilder("Row: {");
            for(int index = 1; index <= metaData.getColumnCount(); ++index) {
                row.append(result.getMetaData().getColumnName(index)).append(": ");
                row.append(result.getString(index)).append(", ");
            }
            row.append("}");
            log.info(row.toString());
        }
        log.info(columns.toString());
    }
}
