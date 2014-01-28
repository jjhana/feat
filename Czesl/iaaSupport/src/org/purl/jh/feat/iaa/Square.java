package org.purl.jh.feat.iaa;

import org.purl.jh.util.err.Err;

/** IAA info for a single tag, contingency table */
public class Square {
    // no_no  yes_no
    // no_yes yes_yes
    int yes_yes = 0;
    int yes_no = 0;
    int no_yes = 0;
    int no_no = 0;

    public void tick(boolean a, boolean b) {
        if (a && b) {
            yes_yes++;
        } else if (a) {
            yes_no++;
        } else if (b) {
            no_yes++;
        } else {
            throw Err.iErr();
        }
    }

    public void setNoNo(int aNoNo) {
        no_no = aNoNo;
    }

    int sum() {
        return yes_yes + yes_no + no_yes + no_no;
    }

    // no_no  yes_no
    // no_yes yes_yes
    int sumRow1() {
        return no_no + yes_no;
    }

    int sumRow2() {
        return no_yes + yes_yes;
    }

    int sumCol1() {
        return no_no + no_yes;
    }

    int sumCol2() {
        return yes_no + yes_yes;
    }

    public double getKappa() {
        if (sum() == 0) return Double.NaN;
        
        double o = o();
        double e = e();
        return (o - e) / (1.0 - e); // Cohen's kappa
        // Cohen's kappa
    }

    public double o() {
        return d(no_no + yes_yes) / d(sum()); // observed proportion of agreement
        // observed proportion of agreement
    }

    public double e() {
        final double n = d(sum());
        return (d(sumRow1()) / n * d(sumCol1()) / n) + (d(sumRow2()) / n * d(sumCol2()) / n); // expected proportion of chance agreement
        // expected proportion of chance agreement
    }

    private double d(int aInt) {
        return 1.0 * aInt;
    }
    
}
