/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.util.err;

import java.io.IOException;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author j
 */
public class ThrowablesTest {
    
    public ThrowablesTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }
    

    @org.junit.Test
    public void testGetCause() {
        Throwable e = new IOException();
        
        assertEquals(e, Throwables.getCause(e, IOException.class));
        
        assertEquals(e, Throwables.getCause(new RuntimeException("A", e), IOException.class));
        assertEquals(e, Throwables.getCause(new IllegalArgumentException(new RuntimeException("A", e)), IOException.class));
        
        e = new IOException("A", new NullPointerException());
        
        assertEquals(e, Throwables.getCause(e, IOException.class));
        
        assertEquals(e, Throwables.getCause(new RuntimeException("A", e), IOException.class));
        assertEquals(e, Throwables.getCause(new IllegalArgumentException(new RuntimeException("A", e)), IOException.class));
    }

    @org.junit.Test
    public void testStackTrace2String() {
    }
}
