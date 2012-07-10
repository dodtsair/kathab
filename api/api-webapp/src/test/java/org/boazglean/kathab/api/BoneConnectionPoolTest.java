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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import com.jolbox.bonecp.BoneCPDataSource;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 *
 * @author mpower
 */
@Test
@Slf4j
public class BoneConnectionPoolTest {

    private BoneCPDataSource source;

    @BeforeClass
    public void setUp() throws SQLException {
        source = new BoneCPDataSource();
        source.setJdbcUrl("jdbc:h2:mem:" + BoneConnectionPoolTest.class.getSimpleName() + ";DB_CLOSE_DELAY=-1;TRACE_LEVEL_FILE=4");
        Connection connection = source.getConnection();
        String sql = ""
                + "begin;\n"
                + "CREATE TABLE sample_table (sample_table_id INTEGER, x_cord INTEGER, y_cord INTEGER, uuid UUID);\n"
                + "INSERT INTO sample_table (sample_table_id, x_cord, y_cord, uuid) VALUES (0, 0, 0, 0);\n"
                + "commit;\n"
                + "";
        Statement ddl = connection.createStatement();
        assertFalse(ddl.execute(sql));
        ddl.close();
        connection.close();
    }

    @AfterClass
    public void tearDown() throws SQLException {
        if (source != null) {
            source.close();
        }
    }

    public void testBonePool() throws Exception {
        int count = 1000;
        Connection connection;
        long after;
        long before = System.currentTimeMillis();
        for (int current = 0; current < count; ++current) {
            connection = source.getConnection();
            connection.close();
        }
        after = System.currentTimeMillis();
        log.info("Connection speed test, count: {}, time: {}, cpms: {}", new Object[]{count, after - before, count / (after - before)});
    }

    public void testLogbackSchema() throws Exception {
        URL ddlUrl = this.getClass().getClassLoader().getResource("ch/qos/logback/classic/db/script/h2.sql");
        URLConnection connectionUrl = ddlUrl.openConnection();
        int length = connectionUrl.getContentLength();
        byte[] buffer = new byte[length];
        InputStream ddlStream = connectionUrl.getInputStream();
        ddlStream.read(buffer);
        String ddl = new String(buffer);

        Connection connection = source.getConnection();
        Statement ddlStatement = connection.createStatement();
        assertFalse(ddlStatement.execute(ddl));
        ddlStatement.close();
        connection.close();

        DBAppender appender = new DBAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        DataSourceConnectionSource dbConnectionSource = new DataSourceConnectionSource();
        dbConnectionSource.setDataSource(source);
        dbConnectionSource.discoverConnectionProperties();
        appender.setConnectionSource(dbConnectionSource);

        Logger logger = (Logger) LoggerFactory.getLogger(BoneConnectionPoolTest.class.getName() + ".testLogbackSchema");
        logger.setLevel(Level.ALL);
        logger.addAppender(appender);
        appender.start();
        logger.info("test");

        connection = source.getConnection();
        Statement selectStatement = connection.createStatement();
        ResultSet query = selectStatement.executeQuery("select * from logging_event;");
        ResultSetMetaData queryData = query.getMetaData();
        while (query.next()) {
            for (int columnIndex = 1; columnIndex <= queryData.getColumnCount(); ++columnIndex) {
                log.info("Column Name, index: {}, name: {}", columnIndex, queryData.getColumnName(columnIndex));
                log.info("Column Label, index: {}, label: {}", columnIndex, queryData.getColumnLabel(columnIndex));
                log.info("Column Type, index: {}, type: {}", columnIndex, queryData.getColumnType(columnIndex));
                log.info("Column Class Name, index: {}, name: {}", columnIndex, queryData.getColumnClassName(columnIndex));
                log.info("Column Type Name, index: {}, typeName: {}", columnIndex, queryData.getColumnTypeName(columnIndex));
                log.info("Column Object, index: {}, object: {}", columnIndex, query.getObject(columnIndex));
                log.info("Column Object Type, index: {}, object type: {}", columnIndex, query.getObject(columnIndex) != null ? query.getObject(columnIndex).getClass() : null);
            }

        }
    }

    public void getGeneratedKeys() throws Exception {
        DatabaseMetaData meta = source.getConnection().getMetaData();
        meta.getCatalogs();
        Boolean supported;
        try {
            //
            // invoking JDBC 1.4 method by reflection
            //
            supported = ((Boolean) DatabaseMetaData.class.getMethod(
                    "supportsGetGeneratedKeys", (Class[]) null).invoke(meta,
                    (Object[]) null)).booleanValue();
        } catch (Throwable e) {
            log.error("Checking to see if get generated keys is supported", e);
            supported = false;
        }
        assertTrue(supported);
    }
}
