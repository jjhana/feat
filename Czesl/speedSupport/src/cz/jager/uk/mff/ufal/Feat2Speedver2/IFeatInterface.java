package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.001
 */

/*
 * Interface is implemented by application programmer
 */
public interface IFeatInterface
 {
  /* URL address of Speed server */
  String getSpeedUrl ();

  /* user name for login on Speed server */
  String getUserName ();

  /* user password for login on Speed server */
  String getUserPassword ();

  /**
   * Should the synchronization download check files from in inbox?
   * Typically returns true.
   */
  boolean isInboxCheckEnabled ();

  /**
   * Returns maximum number of files - how much file could be read in one synchronization
   * Value -1 means read any file
   * Value 0 means read no file
   *
   * Typically returns 10.
   */
  int readMaxInboxFileCount ();

  /**
   * Download all files in the server's inbox again (includes files which
   * have been already downloaded, but have not been uploaded yet).
   * Can be used when a user moves to a new computer.
   *
   * Typically false.
   */
  boolean isReadAnySpeedFileEnabled ();

  /**
   * Should the synchronization upload the content of the outbox to the server?
   * Typically true.
   */
  boolean isOutboxSyncEnabled ();

  /* shows synchronization error message on user interface */
  void showErrorMessage (String msg);

  /* adds synchronization warning message on user interface */
  void addWarning (String msg);

  /**
   * Only files in this list are allowed to be in the inbox.
   * Typically, if a file is in the inbox but is not in this list, it was uploaded
   * from another copy of the inbox on a different computer.
   *
   * Note that not all files in this list must be in the inbox, some are new and
   * will be downloaded during this synchronization and some are already in the
   * inbox (they will be uploaded during this synchronization, but marked as
   * 'duplicates')
   */
  void checkInbox (String fileNameArr[]);

  /**
   * Number of documents prepared for download. Can be used to display progress bar.
   */
  void setInboxSize (int speadInboxSize);

  /* question if there is a correct file already read in inbox */
  boolean isFileInInbox (String fileName);

  /* writes this file into inbox */
  void write2Inbox (ISynchronizedFile file) throws java.io.IOException;

  /* Returns next file from outbox to be uploaded to Speed server
   * Return value <null> means there is no other file in outbox
   */
  ISynchronizedFile getNextOutboxFile ();

  /* deletes this file from outbox */
  void outboxFileProcessed (String fileName);
 }
