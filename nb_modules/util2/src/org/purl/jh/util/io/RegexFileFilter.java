package org.purl.jh.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;


/**
 * Filters out all but files satisfying a specific pattern.
 * All directories are excluded.
 */
public class RegexFileFilter implements FilenameFilter  {

   /**
    * Accepted filenames must satisfy this pattern.
    */
   private final Pattern pattern;

   /**
    * @param aPattern pattern that the file names must satisfy.
    */
   public RegexFileFilter(String aPattern) {
      pattern  = Pattern.compile(aPattern);
   }

   /**
    * @param aPattern pattern that the file names must satisfy.
    */
   public RegexFileFilter(Pattern aPattern) {
      pattern  = aPattern;
   }

   /**
    * Checks if a file satisfies the pattern.
    *
    * @param aDir   the directory in which the file was found
    * @param aName  the name of the file
    *
    * @return  true iff the name should be included in the file list;
    *           false otherwise.
    */
   public boolean accept(File aDir, String aName) {
      if ( !( new File(aDir, aName).isFile() ) ) return false;

      return pattern.matcher(aName).matches();
   }
}

