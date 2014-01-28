package org.purl.jh.util.io;

import java.io.File;
import java.io.FilenameFilter;


/**
 * Filters out all but files with a specific suffix
 * case insensitive
 */
public class EndingFilter implements FilenameFilter  {

   /**
    * Accepted filenames must have this endign.
    */
   private String mEnding;

   /**
    * @param aEnding  string the filename must end with.
    */
   public EndingFilter(String aEnding) {
      mEnding  = aEnding.toLowerCase();
   }

   /**
    * Checks if a file has an appropriate ending.
    *
    * @param aDir   the directory in which the file was found
    * @param aName  the name of the file
    *
    * @return  true iff the name should be included in the file list; 
    *           false otherwise.
    */
   public boolean accept(File aDir, String aName) {
      if ( !( new File(aDir, aName).isFile() ) ) return false;

      return aName.toLowerCase().endsWith(mEnding);
   }
}

