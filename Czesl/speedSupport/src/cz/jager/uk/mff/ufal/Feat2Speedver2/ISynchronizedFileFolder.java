package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.001
 */

/*
 * One synchronized file folder.
 * For anotation there is only one folder for each synchronization file. That folder contains any disc files (*.html, *.a.xml, *.b.xml and *.w.xml)
 * For adjudication there are possibly three folders. One for one source file, second for the second source file, the last for result
 */
public interface ISynchronizedFileFolder
 {
  /*
   * Wheather is this folder read only
   * For adjudication both source files are read only
   */
  boolean isReadOnly ();

  /*
   * Wheather this is a file to be changed (if is this allowed by read-only flag)
   *
   * For anotation this method returns true for any folder
   *
   * For adjudication this method returns true only for result file.
   * And at the first time of adjudication file load into feat there is no work file. There are only two not on work files
   */
  boolean isWorkOnFile ();

  /*
   * Only informative field, could be null
   * For adjudication authors of source files are anotators
   */
  String getAuthor ();

  /* folder content */
  ISynchronizedFileContent[] getContent ();
 }
