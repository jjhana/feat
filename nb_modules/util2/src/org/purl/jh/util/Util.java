package org.purl.jh.util;

import java.io.File;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 * @todo to various classes of util
 */
public final class Util {
    private Util() {}

    public static final String cLetters = "abcdefghijklmnopqrs";

    public static boolean narrowed(String aForm) {
//      return aForm.toLowerCase().startsWith("a");
        return true;
//        if (aForm.length() < 1) return false;
//        char char0 = Character.toLowerCase(aForm.charAt(0));
////        return cLetters.indexOf(char0) != -1;
//        return char0 == 'o'; // && (char1 == 'a' || char1 == 'o');
//        return true;
//      return aForm.tnuoLowerCase().startsWith("paradox");
//        return aForm.toLowerCase().startsWith("poloh");
//      return aForm.toLowerCase().startsWith("p");
//      return aForm.toLowerCase().startsWith("p");
//        if (aForm.length() < 1) return false;
//        char char0 = Character.toLowerCase(aForm.charAt(0));
//        char char1 = Character.toLowerCase(aForm.charAt(1));
    }


    public static String sepp(String aTitle) {
        return Strings.newLine +
            "========================================================================" + Strings.newLine +
            aTitle + Strings.newLine +
            "========================================================================" + Strings.newLine;
    }

// -----------------------------------------------------------------------------
// Line processing
// -----------------------------------------------------------------------------

    // TODO fix //* will not be read as // + text but as / + comment
    public static String preprocessLine(String aLine) {
        for(;;) {
            int comment1Idx = aLine.indexOf("//");
            int commentIdx  = aLine.indexOf("/*");

            // "//" precedes "/* ... */"
            if (comment1Idx != -1 && (commentIdx == -1 || comment1Idx < commentIdx) ) {
                return aLine.substring(0, comment1Idx);
            }

            if (commentIdx == -1) return aLine;

            int endCommentIdx = aLine.indexOf("*/");
            if (endCommentIdx != -1 && (endCommentIdx+2 < aLine.length()) )
                aLine = aLine.substring(0, commentIdx) + aLine.substring(endCommentIdx+2);
            else
                aLine = aLine.substring(0, commentIdx);
        }
    }


    public static int getNumber(StringTokenizer aTok, LineNumberReader aReader, String aFileId, String aTokenId) throws java.util.NoSuchElementException {
        try {
            return new Integer(getString(aTok, aReader, aFileId, aTokenId)).intValue();
        }
        catch (java.lang.NumberFormatException e) {
            System.out.println("Error in " + aFileId + " at line: " + aReader.getLineNumber() + ": " + aTokenId + " token should be a number; ignoring the whole line");
            throw new java.util.NoSuchElementException();
        }
    }


    public static String getString(StringTokenizer aTok, LineNumberReader aReader, String aFileId, String aTokenId) {
        try {
            return aTok.nextToken();
        }
        catch (java.util.NoSuchElementException e) {
            System.out.println("Error in " + aFileId + " at line: " + aReader.getLineNumber() + ": " + aTokenId + " token missing; ignoring the whole line");
            throw e;
        }
    }

// -----------------------------------------------------------------------------
// Number helpers
// -----------------------------------------------------------------------------

    public static boolean isNumber(String aNr) {
        try {
            Double.valueOf(aNr);
            return true;
        }
        catch (java.lang.NumberFormatException e) {
            return false;
        }
    }

    public static double log2(double aArg) {
        return log(aArg,2);
    }

    public static double log10(double aArg) {
        return log(aArg,10);
    }

    public static double log(double aArg, double aBase) {
        return Math.log(aArg)/Math.log(aBase);
    }

    public static int positive(int aNr) {
        return (aNr > 1) ? aNr : 1;
    }

    public static float perc(int aA, int aB) {
        return ((float)aA) / ((float)aB) * 100.0f;
    }

    public static String formatPercentages(double aFrac) {
        return "" + (Math.round(aFrac*1000.0)/10.0) + "%";
    }

    public static String formatPercentages(int aA, int aB) {
        return String.format("%5.1f%%", perc(aA,aB));
    }

    public static String formatPercentages(int aA, int aB, int aDecDigits) {
        return String.format("%" + (4 + aDecDigits) + '.' + aDecDigits + "f%%", perc(aA,aB));
    }

    public static String formatFrac(int aA, int aB) {
        return formatFrac( (aA*1.0) / (aB*1.0) );
    }

    public static String formatFrac(double aFrac) {
        return "" + round3(aFrac);
    }

    public static double round3(double aFrac) {
        return (Math.round(aFrac * 1000.0)*1.0)/1000.0;
    }


// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

    /**
     * Returns the folder containing the jar file of the specified object.
     *
     * Tested only for classes in jars not standalone class files.
     */
    public static File getClassFolder(Object aObj) {
        URL jarUrl = aObj.getClass().getProtectionDomain().getCodeSource().getLocation();
        URI jarUri;
        try {
            jarUri = jarUrl.toURI();
        }
        catch(URISyntaxException e) {
            throw new RuntimeException(e); // should not happen, url should be correct
        }

        return new File(jarUri).getParentFile();
    }

    /**
     * After calling this method, unix line separators will be used.
     * Sets the system property <code>line.separator</code> to '\n'
     */
    public static void setUnixLineSeparators() {
        Properties prop = System.getProperties();
        prop.setProperty("line.separator", "\n");
        System.setProperties(prop);
    }

//    return Util.compareTo(mLemma, mParadigmId, o.mLemma, o.mParadigmId);
//    public static int compareTo(Comparable aFirst, Comparable aSecond, Comparable bFirst, Comparable bSecond) {
//        int tmp = aFirst.compareTo(bFirst);
//        return (tmp == 0) ? aSecond.compareTo(bSecond) : tmp;
//    }

    // todo to strings
    public static boolean isEmpty(final String aStr) {
        return aStr == null || aStr.isEmpty();
    }


    public static <T> T last(final List<T> aList) {
        return aList.get(aList.size()-1);
    }

    /**
     * Finds the first element of a list present in a set.
     *
     * @param <T> type of the list and set elements
     * @param aList list to search for the element
     * @param aSet set the found element must be present in
     * @return index of the first element in the list that is also present
     * in the set
     */
    public static <T> int findFirstIn(final List<T> aList, final Set<T> aSet) {
        for (int i = 0; i < aList.size(); i++) {
            if ( aSet.contains(aList.get(i)) ) return i;
        }

        return -1;
    }

    /**
     * Finds the last element of a list present in a set.
     *
     * @param <T> type of the list and set elements
     * @param aList list to search for the element
     * @param aSet set the found element must be present in
     * @return index of the last element in the list that is also present
     * in the set
     */
    public static <T> int findLastIn(final List<T> aList, final Set<T> aSet) {
        for (int i = aList.size()-1; i >= 0; i--) {
            if ( aSet.contains(aList.get(i)) ) return i;
        }

        return -1;
    }


    public static <E> Set<E> union(final Set<E> aResult, Set<E> ... aCols) {
        for (Set<E> set : aCols) {
            aResult.addAll(set);
        }
        return aResult;
    }


    public static <K,V> Collection<V> inters(MultiMap<K,V> aMultiMap, K ... aKeys) {
        if (aKeys.length == 0) return Collections.<V>emptyList();

        final Set<V> set = new HashSet<V>(aMultiMap.get(aKeys[0]));

        for (int i = 1; i < aKeys.length; i++) {
            set.retainAll(aMultiMap.get(aKeys[i]));
        }

        return set;
    }

    // todo
    public static <K,V> Collection<V> inters(MultiMap<K,V> aMultiMap, Iterable<K> aNodes) {
        throw new UnsupportedOperationException();
    }


    public static <K,V> Collection<V> union(MultiMap<K,V> aMultiMap, K ... aNodes) {
        final Set<V> set = new HashSet<V>();

        for (K node : aNodes) {
            set.addAll(aMultiMap.get(node));
        }

        return set;
    }

    public static <K,V> Collection<V> union(MultiMap<K,V> aMultiMap, Iterable<K> aNodes) {
        final Set<V> set = new HashSet<V>();

        for (K node : aNodes) {
            set.addAll(aMultiMap.get(node));
        }

        return set;
    }


    /**
     * Checks if two objects are equal, taking care of null values.
     *
     * @param o1 object to compare
     * @param o2 object to compare
     * @return true if both objects are equal or both parameters are null.
     */
    public static boolean eq(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

}
