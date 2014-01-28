package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.001
 */

/*
 * Whole bundle content - that means that there is any representation of that file
 */
public interface ISynchronizedFile
 {
  /* returns file name without extension (e.g. "BHA_97_AKY") */
  String getFileName ();

  /*
   * Returns synchronization command file
   */
  ISynchronizedFileContent[] getHead ();

  /*
   * Returns synchronization file folders.
   * There is just one folder for anotation purposes. At the case of adjudication there are two or three folders
   */
  ISynchronizedFileFolder[] getFolders ();
 }
