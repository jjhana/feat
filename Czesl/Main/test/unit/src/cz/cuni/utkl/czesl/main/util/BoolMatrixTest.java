package cz.cuni.utkl.czesl.main.util;

import org.purl.jh.util.col.BoolMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jirka
 */
public class BoolMatrixTest {

    public BoolMatrixTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testCopy() {
    }

    @Test
    public void testGetRowDimension() {
    }

    @Test
    public void testGetColumnDimension() {
    }

    @Test
    public void testGet() {
    }

    @Test
    public void testSet() {
    }

    @Test
    public void testTranspose() {
    }

    @Test
    public void testAllFalse() {
        final BoolMatrix m  = new BoolMatrix("010:100:001");

        assertFalse(m.allFalse(1, 3, 0, 1));
        assertTrue( m.allFalse(2, 3, 0, 1));


    }

    @Test
    public void testToString() {
    }

}