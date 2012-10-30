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

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
        schema.initSchema(mockConnection);
    }

    @Test
    public void testAccessors() {
        Schema schema = new Schema();
        String trivialSelect = "select 1";
        ClassLoader loader = mock(ClassLoader.class);

        assertTrue(schema.getDdl().indexOf("logging_event") != -1);

        schema.setDdl(trivialSelect);
        assertEquals(schema.getDdl(), trivialSelect);

    }

    @Test
    public void testEquals() {
        String trivialSelect = "select 1";
        ClassLoader loader = mock(ClassLoader.class);
        Schema schema = new Schema();
        Schema other = new Schema();
        Schema mocked = mock(Schema.class);

        assertTrue(schema.equals(schema));
        assertTrue(schema.equals(other));

        other.setDdl(null);
        assertFalse(schema.equals(other));
        schema.setDdl(null);
        assertTrue(schema.equals(other));
        other.setDdl(trivialSelect);
        assertFalse(schema.equals(other));
        schema.setDdl("");
        assertFalse(schema.equals(other));
        schema.setDdl(trivialSelect);
        assertTrue(schema.equals(other));

        assertFalse((schema.equals(new Object())));

        when(mocked.canEqual(schema)).thenReturn(false);

        assertFalse(schema.equals(mocked));

    }

    @Test
    public void testNegativeSchemaConstruction() {
        Schema schema = new Schema("test shouldn't find this");
        assertEquals(schema.getDdl(), "");
    }

    @Test
    public void testSchemaConstructionException() throws Exception{
        ClassLoader loader = mock(ClassLoader.class);

        when(loader.getResource(anyString())).thenReturn(new URL("file:///doesnotexit/"));
        Schema schema = new Schema("resource name does not matter", loader);

        assertEquals(schema.getDdl(), "");

    }

    @Test
    public void testInitSchemaException() throws Exception {
        Schema schema = new Schema();
        Connection connection = mock(Connection.class);

        when(connection.createStatement()).thenThrow(new SQLException("Generated for tests"));

        schema.initSchema(connection);
    }

    @Test
    public void testInitSchemaExecuteException() throws Exception {
        Schema schema = new Schema();
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Generated for tests"));

        schema.initSchema(connection);

        verify(statement).close();

    }

    @Test
    public void testInitSchemaNoStatement() throws Exception {
        boolean exception = false;
        Schema schema = new Schema();
        Connection connection = mock(Connection.class);

        when(connection.createStatement()).thenReturn(null);

        try {
            schema.initSchema(connection);
        }
        catch (NullPointerException e) {
            exception = true;
        }
        assertTrue(exception);

    }

    @Test
    public void testToString() {
        Schema schema = new Schema();

        assertNotNull(schema.toString());

        schema.setDdl(null);

        assertNotNull(schema.toString());
    }


    @Test
    public void testHashcode() {
        Schema schema = new Schema();

        schema.hashCode();

        schema.setDdl(null);

        schema.hashCode();
    }
}
