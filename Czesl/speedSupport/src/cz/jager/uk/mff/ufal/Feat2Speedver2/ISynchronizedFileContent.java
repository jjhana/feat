package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.002
 */

/*
 * One disc file content
 */
public interface ISynchronizedFileContent
 {
  public static final int txtFileType = 1;
  public static final int binFileType = 2;

  /* returns file extension - for example "html" */
  String getFileExtension ();

  /* returns file type - possible values are txtFileType and binFileType */
  int getFileType ();

  /* returns text based file contente - for example for txt, html, xml */
  String getTxtBasedFileContent ();

  /* returns binary file content - for example image */
  byte[] getBinFileContent ();
 }
