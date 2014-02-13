package cz.jager.uk.mff.ufal.Feat2Speedver2;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.001
 */

/*
 * Main interface of synchronization
 */
public interface ISynchronize
 {
  /*
   * calls of standard synchronization between Feat application and Speed server.
   * Synchronized are both outbox and inbox folders (in this order)
   */
  void synchronize (IFeatInterface feat);

  /* reads only this one file from Speed server */
  void readFile (IFeatInterface feat, String speedId);

  /* reads only file id's from Speed user inbox */
  String[] readInboxSpeedIdList (IFeatInterface feat);
 }
