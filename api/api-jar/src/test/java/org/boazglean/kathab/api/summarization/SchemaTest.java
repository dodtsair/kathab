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
package org.boazglean.kathab.api.summarization;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 *
 * @author mpower
 */
public class SchemaTest {
    
    private Connection connection;
    
    @BeforeMethod
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + this.getClass().getSimpleName());
        Schema schema = new Schema();
        schema.initSchema(connection);
    }
    
    @AfterMethod
    public void teardown() throws Exception {
        try {
            connection.close();
        }
        catch(Throwable ex) {};
    }
    
    @Test
    public void initSchema() {
        Schema schema = new Schema();
        schema.initSchema(connection);
    }
    
    @Test
    public void initSchemaFail() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Schema schema = new Schema();
        when(mockConnection.createStatement()).thenThrow(new SQLException("Generated for testing"));
        Logger logger = (Logger) LoggerFactory.getLogger(schema.getClass());
        logger.setLevel(Level.OFF);
        schema.initSchema(mockConnection);
    }

    @Test
    public void initSchemaFailInfoOn() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Schema schema = new Schema();
        when(mockConnection.createStatement()).thenThrow(new SQLException("Generated for testing"));
        Logger logger = (Logger) LoggerFactory.getLogger(schema.getClass());
        logger.setLevel(Level.ALL);
        schema.initSchema(mockConnection);
    }
}
