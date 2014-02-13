package org.purl.jh.util.col;

import java.util.Arrays;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Jirka
 */
public class BoolMatrix implements java.io.Serializable {

    /**
     * Array for internal storage of elements.
     * @todo use long's bits
     * @serial internal array storage.
     */
    private final boolean[][] data;
    /** Row and column dimensions.
    @serial row dimension.
    @serial column dimension.
     */
    private final int rsize, csize;

    /** Construct an m-by-n matrix of zeros.
    @param m    Number of rows.
    @param n    Number of colums.
     */
    public BoolMatrix(int m, int n) {
        this.rsize = m;
        this.csize = n;
        data = new boolean[m][n];
    }

    public BoolMatrix(final String aSpec) {
        final String[] s = Strings.cColonPattern.split(aSpec);

        rsize = s.length;

        csize = (rsize == 0) ? 0 : s[0].length();

        data = new boolean[rsize][csize];

        for (int r = 0; r < rsize; r++) {
            final String row = s[r];
            Err.fAssert(row.length() == csize, "All rows must be the same length. (%s)", aSpec);

            for (int c = 0; c < csize; c++) {
                char x = row.charAt(c);
                data[r][c] = (x == '1');
            }
        }
    }

    /** Make a deep copy of a matrix
     */
    public BoolMatrix copy() {
        final BoolMatrix o = new BoolMatrix(rsize, csize);
        final boolean[][] odata = o.data;

        for (int i = 0; i < rsize; i++) {
            for (int j = 0; j < csize; j++) {
                odata[i][j] = data[i][j];
            }
        }

        return o;
    }

    /** Get row dimension.
     * @return the number of rows.
     */
    public int getRowDimension() {
        return rsize;
    }

    /** Get column dimension.
     * @return the number of columns.
     */
    public int getColumnDimension() {
        return csize;
    }

    /** Get a single element.
    @param i    Row index.
    @param j    Column index.
    @return     A(i,j)
    @exception  ArrayIndexOutOfBoundsException
     */
    public boolean get(int i, int j) {
        return data[i][j];
    }

    /** Get a submatrix. */
//   public void setMatrix (int i0, int i1, int[] c, Matrix X) {
    /** Set a single element.
    @param i    Row index.
    @param j    Column index.
    @param s    A(i,j).
    @exception  ArrayIndexOutOfBoundsException
     */
    public void set(int i, int j, boolean a) {
        data[i][j] = a;
    }

    public BoolMatrix transpose() {
        final BoolMatrix o = new BoolMatrix(csize, rsize);
        for (int i = 0; i < rsize; i++) {
            for (int j = 0; j < csize; j++) {
                o.data[j][i] = data[i][j];
            }
        }
        return o;
    }

    /**
     * Checks if a submatrix has all cells false.
     *
     * @param aRow1
     * @param aRow2 exclusive
     * @param aCol1
     * @param aCol2 exclusive
     * @return
     */
    public boolean allFalse(final int aRow1, final int aRow2, final int aCol1, final int aCol2) {
//        System.out.printf("All false? %d:%d x %d:%d", aRow1, aRow2, aCol1, aCol2);
        for (int r = aRow1; r < aRow2; r++) {
            for (int c = aCol1; c < aCol2; c++) {
                if (data[r][c]) {
//                    System.out.println(" No!");
                    return false;
                }
            }
        }
//        System.out.println(" Yes!");
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rsize; i++) {
            for (int j = 0; j < csize; j++) {
                sb.append(data[i][j] ? '1' : '0');
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null || getClass() != obj.getClass() ) {
            return false;
        }

        return Arrays.equals(this.data, ((BoolMatrix) obj).data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }



}
