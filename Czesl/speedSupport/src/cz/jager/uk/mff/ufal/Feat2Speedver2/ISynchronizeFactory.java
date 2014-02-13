package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.002
 */

/*
 * Abstract factory for creating of synchronization objects
 */
public interface ISynchronizeFactory
 {
  /* creates a new synchronization object */
  ISynchronize synchronize ();

  /* creates a new whole synchronized file object */
  ISynchronizedFile file (String fileName, ISynchronizedFileContent[] head, ISynchronizedFileFolder[] folders);
  ISynchronizedFile file (String fileName, ISynchronizedFileContent cmdFile, ISynchronizedFileFolder[] folders);
  ISynchronizedFile file (String fileName, ISynchronizedFileContent cmdFile, ISynchronizedFileFolder content);
  ISynchronizedFile file (String fileName, String cmdFile, String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile);

  /*
   * Creates a new synchronized file folder object
   * Default value for readOnly is false, for author is null
   */
  ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, boolean isReadOnly, boolean isWorkOnFile, String author);
  ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, boolean isReadOnly);
  ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, String author);
  ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content);
  ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly, boolean isWorkOnFile, String author);
  ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly, String author);
  ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly);
  ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, String author);
  ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile);

  /* creates a new disc text based file object */
  ISynchronizedFileContent fileTextContent (String fileExtension, String content);

  /* creates a new command file object */
  ISynchronizedFileContent fileCmdContent (String content);

  /* creates a new disc binary file object */
  ISynchronizedFileContent fileBinaryContent (String fileExtension, byte[] content);
 }
