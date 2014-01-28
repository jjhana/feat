package cz.jager.uk.mff.ufal.Feat2Speedver2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.NamedNodeMap;

/**
 * <p>Title: Feat2Spead</p>
 * @author Petr Jager
 * @version 1.01.0.003
 */

public class SynchronizeFactory implements ISynchronizeFactory
 {
  private SynchronizeFactory () { }

  private static ISynchronizeFactory ms_Instance = null;

  public static ISynchronizeFactory getFactory ()
   {
    if (ms_Instance == null) ms_Instance = new SynchronizeFactory ();
    return (ms_Instance);
   }

  //########################################################################### ISynchronize ###########################################################################
  /* creates a new synchronization object */
  public ISynchronize synchronize () { return (new Synchronize ()); }

  private static class Synchronize implements ISynchronize
   {
    public Synchronize () { }

    // constants
    private static final String soapProto = SOAPConstants.DEFAULT_SOAP_PROTOCOL;
    private static final String nameSpace = "http://speed2featVer2.ufal.jager.cz/";

    private IFeatInterface feat = null;

    // SOAP
    private SOAPFactory soap = null;
    private SOAPConnection conn = null;
    private MessageFactory msgFact = null;
    private URL endPoint = null;

    private String sessionId = null;

    /*
     * calls of standard synchronization between Feat application and Speed server.
     * Synchronized are both outbox and inbox folders (in this order)
     */
    public void synchronize (IFeatInterface feat)
     {
      this.feat = feat;

      try
       {
        initSOAP (feat.getSpeedUrl ());
        createSession (feat.getUserName (), feat.getUserPassword ());

        try
         {
           if (feat.isInboxCheckEnabled ()) feat.checkInbox (checkInbox ());
           if (feat.isOutboxSyncEnabled ()) uploadAllFiles ();
           if (feat.readMaxInboxFileCount () > 0) downloadAllFiles ();
         }
        finally { closeSession (); }
       }
      catch (Exception e) { feat.showErrorMessage (e.getMessage ()); }
      finally { doneSOAP (); }
     }

    /* reads only this one file from Speed server */
    public void readFile (IFeatInterface feat, String speedId)
     {
      this.feat = feat;

      try
       {
        initSOAP (feat.getSpeedUrl ());
        createSession(feat.getUserName (), feat.getUserPassword ());

        try { peekFile (speedId); fileRead (speedId); }
        finally { closeSession(); }
       }
      catch (Exception e) { feat.showErrorMessage (e.getMessage ()); }
      finally { doneSOAP (); }
     }

    /* reads only file id's from Speed user inbox */
    public String[] readInboxSpeedIdList (IFeatInterface feat)
     {
      this.feat = feat;

      try
       {
        initSOAP (feat.getSpeedUrl ());
        createSession (feat.getUserName (), feat.getUserPassword ());

        try { return (getUserInboxHeader (feat.isReadAnySpeedFileEnabled ())); }
        finally { closeSession (); }
       }
      catch (Exception e) { feat.showErrorMessage (e.getMessage ()); }
      finally { doneSOAP (); }

      return (null);
     }

        /**
         * Sends all files to the server.
         *
         * @throws IOException thrown on I/O error
         * @throws SOAPException thrown on SOAP error
         */
    private void uploadAllFiles () throws IOException, SOAPException
     {
      ISynchronizedFile f;

      while ((f = feat.getNextOutboxFile()) != null)
       { saveFile (f); feat.outboxFileProcessed (f.getFileName ()); }
     }

        /**
         * Retrieves all files from the server and saves them.
         *
         * @throws IOException thrown on I/O error
         * @throws SOAPException thrown on SOAP error
         */
    private void downloadAllFiles () throws IOException, SOAPException
     {
      int maxFilesCount = feat.readMaxInboxFileCount ();
      if (maxFilesCount < 1) return;
      String ida[] = getUserInboxHeader (feat.isReadAnySpeedFileEnabled ());

      if (ida.length < maxFilesCount) maxFilesCount = ida.length;
      feat.setInboxSize (maxFilesCount);

      for (int i = 0; i < maxFilesCount; i++)
       {
        String id = ida [i];
        peekFile (id);
        fileRead (id);
       }
     }

        /**
         * Initializes SOAP infrastructure.
         *
         * @param urlString URL string
         * @throws MalformedURLException thrown if URL is malformed
         * @throws SOAPException thrown on SOAP error
         */
    private void initSOAP (String urlString) throws MalformedURLException, SOAPException
     {
      soap = SOAPFactory.newInstance (soapProto);
      conn = SOAPConnectionFactory.newInstance ().createConnection ();
      endPoint = new URL (urlString);
      msgFact = MessageFactory.newInstance (soapProto);
     }

        /**
         * Cleans-up SOAP infrastructure.
         */
    private void doneSOAP ()
     {
      sessionId = null;
      msgFact = null;
      endPoint = null;

      if (conn != null)
       {
        try { conn.close(); }
        catch (SOAPException e) { /* no special action needed */ }
        finally { conn = null; }
       }

      soap = null;
     }

        /**
         * Creates a SOAP message.
         *
         * It creates an empty message which complies the specification of the
         * appropriate communication.
         *
         * @return SOAP message
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if SOAP infrastructure is not initialized
         */
    private SOAPMessage createMessage () throws SOAPException
     {
      if (msgFact == null) throw new IllegalStateException ("SOAP infrastructure not initialized");

      SOAPMessage msg = msgFact.createMessage ();

      msg.setProperty (SOAPMessage.WRITE_XML_DECLARATION, "true");
      SOAPElement body = msg.getSOAPBody ();
      SOAPElement env = body.getParentElement ();

      msg.getSOAPHeader ().detachNode ();

      env.setPrefix ("soap");
      env.removeNamespaceDeclaration ("SOAP-ENV");
      env.addNamespaceDeclaration ("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      env.addNamespaceDeclaration ("xsd", "http://www.w3.org/2001/XMLSchema");

      body.setPrefix ("soap");

      return (msg);
     }

        /**
         * Creates a request element (a direct child of the body element).
         *
         * @param msg SOAP message
         * @param name element name (ie. webmethod name)
         * @return SOAP request element
         * @throws SOAPException thrown on SOAP error
         * @throws NullPointerException thrown if msg is null
         */
    private SOAPElement createRequestElement (SOAPMessage msg, String name) throws SOAPException
     {
      return msg.getSOAPBody ().addBodyElement (soap.createName (name, "", nameSpace));
     }

        /**
         * Returns at most one child element.
         *
         * @param e SOAP element
         * @param name child name
         * @return SOAP element; null if no such element
         * @throws SOAPException thrown on SOAP error
         * @throws NullPointerException thrown if e is null
         */
    private SOAPElement getChildElement (SOAPElement e, String name) throws SOAPException
     {
      Iterator it = e.getChildElements (soap.createName (name, "", nameSpace));

      if (it.hasNext ()) return ((SOAPElement) it.next ()); else return (null);
     }

        /**
         * Calls a SOAP webmethod, parses its return value and checks for errors.
         *
         * @param req prepared request message
         * @param methodName method name
         * @return result element
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if SOAP infrastructure is not initialized
         * @throws NullPointerException thrown if methodName is null
         */
    private SOAPElement callWebMethod (SOAPMessage req, String methodName) throws SOAPException
     {
      if (conn == null) throw new IllegalStateException ("SOAP infrastructure not initialized");
      if (methodName == null) throw new NullPointerException ("methodName must not be null");

      SOAPMessage resp = conn.call (req, endPoint);

      SOAPElement e = getChildElement (resp.getSOAPBody (), methodName + "Response");
      if (e == null) throw new SOAPException (methodName + " response is empty");

      e = getChildElement (e, methodName + "Result");
      if (e == null) throw new SOAPException (methodName + " result is empty");

      SOAPElement e2 = getChildElement (e, "status");
      if (e2 == null) throw new SOAPException (methodName + " status is empty");

      int status = Integer.parseInt (e2.getValue ());

      if (status != 0)
       {
        e2 = getChildElement (e, "errMsg");
        if (e2 == null) throw new SOAPException (methodName + " failed: no specific error message available");

        if (status == 1) throw new SOAPException (e2.getValue());
        feat.addWarning (e2.getValue ());
       }

      return (e);
     }

        /**
         * Returns an input stream for a text value in a SOAP element.
         *
         * @param parent parent SOAP element
         * @param name name of the child element
         * @return input stream for the string; null if the child not present
         * @throws SOAPException thrown on SOAP error
         * @throws NullPointerException thrown if parent is null
         */
    private String getInputString (SOAPElement parent, String name) throws SOAPException
     {
      SOAPElement e = getChildElement(parent, name);
      return ((e != null)? e.getValue (): null);
     }

        /**
         * Creates a new session.
         *
         * @param userName user name
         * @param userPassword password
         * @throws SOAPException thrown on SOAP error
         */
    private void createSession (String userName, String userPassword) throws SOAPException
     {
      SOAPMessage req = createMessage ();
      SOAPElement e = createRequestElement (req, "createSession");
      e.addChildElement ("userName").addTextNode (userName);
      e.addChildElement ("passWd").addTextNode (userPassword);

      e = callWebMethod (req, "createSession");

      SOAPElement e2 = getChildElement (e, "sessionId");
      if (e2 == null) throw new SOAPException ("createSession failed: status is OK but no sessionId sent");

      sessionId = e2.getValue();
     }

        /**
         * Closes the current session.
         *
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if no session is open
         */
    private void closeSession () throws SOAPException
     {
      if (sessionId == null) throw new IllegalStateException("no session is open");

      SOAPMessage req = createMessage ();
      SOAPElement e = createRequestElement (req, "closeSession");
      e.addChildElement ("sessionId").addTextNode (sessionId);

      callWebMethod (req, "closeSession");

      sessionId = null;
     }

    private void saveFile (ISynchronizedFile file) throws SOAPException
     {
      if (file == null) return;
      if (sessionId == null) throw new IllegalStateException ("no session is open");

      SOAPMessage req = createMessage ();
      SOAPElement e = createRequestElement (req, "saveFile");

      e.addChildElement ("sessionId").addTextNode (sessionId);
      SOAPElement fileEl = e.addChildElement ("file");
      fileEl.addChildElement ("name").addTextNode (file.getFileName ());
      createSaveFileContent (fileEl.addChildElement ("head"), file.getHead ());
      createSaveFileFolder (fileEl.addChildElement ("folders"), file.getFolders ());

      callWebMethod (req, "saveFile");
     }

    private void createSaveFileFolder (SOAPElement lstEl, ISynchronizedFileFolder[] fld) throws SOAPException
     {
      if (fld != null)
       {
        int i;

        for (i = 0; i < fld.length; i++)
         if (fld [i] != null && fld [i].isWorkOnFile ())
          {
           SOAPElement e = lstEl.addChildElement ("WebSynchronizedFileFolder");
           e.addChildElement ("isReadOnly").addTextNode (Boolean.toString (fld [i].isReadOnly ()));
           e.addChildElement ("isWorkOnFile").addTextNode (Boolean.toString (fld [i].isWorkOnFile ()));
           if (fld [i].getAuthor () != null) e.addChildElement ("author").addTextNode (fld [i].getAuthor ());
           createSaveFileContent (e.addChildElement ("content"), fld [i].getContent ());
          }
       }
     }

    private void createSaveFileContent (SOAPElement lstEl, ISynchronizedFileContent[] content) throws SOAPException
     {
      if (content != null)
       {
        int i;

        for (i = 0; i < content.length; i++)
         if (content [i] != null && content [i].getFileType () == ISynchronizedFileContent.txtFileType)
          {
           SOAPElement e = lstEl.addChildElement ("WebSynchronizedFileContent");
           e.addChildElement ("extension").addTextNode (content [i].getFileExtension ());
           e.addChildElement ("isTextBased").addTextNode (Boolean.toString (true));
           e.addChildElement ("textContent").addTextNode (content [i].getTxtBasedFileContent ());
          }
       }
     }

        /**
         * Returns the array of file names in inbox.
         *
         * @return array of file names in inbox
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if no session is open
         */
    private String[] checkInbox () throws SOAPException
     {
      if (sessionId == null) throw new IllegalStateException("no session is open");

      SOAPMessage req = createMessage();
      SOAPElement e = createRequestElement(req, "checkInbox");

      e.addChildElement("sessionId").addTextNode(sessionId);

      e = callWebMethod(req, "checkInbox");

      List list = new ArrayList();
      Iterator it = e.getChildElements(); //soap.createName("inboxTaskIdList"));
      while (it.hasNext())
       {
        SOAPElement e2 = (SOAPElement) it.next();

        if (e2.getNodeName().equals("inboxFileNameList"))
         {
          Iterator itValue = e2.getChildElements();
          while (itValue.hasNext())
           {
            Node valueNode = (Node) itValue.next ();
            list.add(valueNode.getValue());
           }
         }
       }

      return ((String[]) (list.toArray(new String[list.size ()])));
     }

        /**
         * Returns the array of task IDs to retrieve from the server.
         *
         * @param is2ReadAnyFile whether it is to read any file
         * @return array of task IDs
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if no session is open
         */
    private String[] getUserInboxHeader (boolean is2ReadAnyFile) throws SOAPException
     {
      if (sessionId == null) throw new IllegalStateException ("no session is open");

      SOAPMessage req = createMessage ();
      SOAPElement e = createRequestElement (req, "getUserInboxHeader");

      e.addChildElement ("sessionId").addTextNode (sessionId);
      e.addChildElement ("is2ReadAnyFile").addTextNode (Boolean.toString (is2ReadAnyFile));

      e = callWebMethod (req, "getUserInboxHeader");

      List list = new ArrayList ();
      Iterator it = e.getChildElements (); //soap.createName("inboxTaskIdList"));
      while (it.hasNext ())
       {
        SOAPElement e2 = (SOAPElement) it.next ();

        if (e2.getNodeName ().equals ("inboxTaskIdList"))
         {
          Iterator itValue = e2.getChildElements ();
          while (itValue.hasNext())
           {
            Node valueNode = (Node) itValue.next ();
            list.add (valueNode.getValue ());
           }
         }
       }

      return ((String[]) (list.toArray(new String[list.size ()])));
     }

        /**
         * Peeks a file from the server.
         *
         * @param taskId task ID
         * @throws IOException thrown on I/O error
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if no session is open
         * @throws NullPointerException thrown if taskId is null
         */
    private void peekFile (String taskId) throws IOException, SOAPException
     {
      if (sessionId == null) throw new IllegalStateException ("no session is open");
      if (taskId == null) throw new NullPointerException ("taskId must not be null");

      SOAPMessage req = createMessage();
      SOAPElement e = createRequestElement(req, "peekFile");
      SOAPElement e2;

      e.addChildElement ("sessionId").addTextNode (sessionId);
      e.addChildElement ("taskId").addTextNode (taskId);

      e = callWebMethod (req, "peekFile");

      {
       e2 = getChildElement (e, "status");
       if (e2 != null) if (Integer.parseInt (e2.getValue ()) != 0) return;
      }

      e = getChildElement(e, "syncFile");
      if (e == null) throw new SOAPException ("poken file is empty");

      e2 = getChildElement (e, "name");
      if (e2 == null) throw new SOAPException ("file name is empty");
      String fileName = e2.getValue ();

      ISynchronizedFileContent[] head = parseFileContents (e, "head");

      ISynchronizedFileFolder[] folders = parseFileFolders (e, "folders");

      ISynchronizedFile f = getFactory ().file (fileName, head, folders);
      feat.write2Inbox (f);
     }

    private ISynchronizedFileFolder[] parseFileFolders (SOAPElement fatherEl, String elemName) throws SOAPException
     {
      List list = new ArrayList ();
      Iterator mainIt = fatherEl.getChildElements ();

      while (mainIt.hasNext ())
       {
        SOAPElement lstEl = (SOAPElement) mainIt.next ();

        if (lstEl.getNodeName ().equals (elemName))
         {
          Iterator it = lstEl.getChildElements ();

          while (it.hasNext ())
           {
            SOAPElement chEl = (SOAPElement) it.next ();
            if (chEl.getNodeName ().equals ("WebSynchronizedFileFolder"))
             {
              boolean isReadOnly;
              boolean isWorkOnFile;
              String author;
              ISynchronizedFileContent[] content;

              isReadOnly = false; isWorkOnFile = true; author = null; content = null;

              SOAPElement e = getChildElement (chEl, "isReadOnly");
              if (e != null) isReadOnly = parseBoolean (e.getValue ());

              e = getChildElement (chEl, "isWorkOnFile");
              if (e != null) isWorkOnFile = parseBoolean (e.getValue ());

              e = getChildElement (chEl, "author");
              if (e != null) author = e.getValue ();

              content = parseFileContents (chEl, "content");

              if (content != null) list.add (getFactory ().fileFolder (content, isReadOnly, isWorkOnFile, author));
             }
           }
         }
       }

      return ((list.size () > 0)? (ISynchronizedFileFolder[]) list.toArray (new ISynchronizedFileFolder [list.size ()]): null);
     }

    private static boolean parseBoolean (String val)
     {
      if (val != null)
       if (val.equalsIgnoreCase ("true") || val.equalsIgnoreCase ("t") || val.equalsIgnoreCase ("yes") || val.equalsIgnoreCase ("y")) return (true);
       else
        try { int i; i = Integer.parseInt (val);  return (i != 0); }
        catch (NumberFormatException e) { }

      return (false);
     }

    private ISynchronizedFileContent[] parseFileContents (SOAPElement fatherEl, String elemName) throws SOAPException
     {
      List list = new ArrayList ();
      Iterator mainIt = fatherEl.getChildElements ();

      while (mainIt.hasNext ())
       {
        SOAPElement lstEl = (SOAPElement) mainIt.next ();

        if (lstEl.getNodeName ().equals (elemName))
         {
          Iterator it = lstEl.getChildElements ();

          while (it.hasNext ())
           {
            SOAPElement chEl = (SOAPElement) it.next ();
            if (chEl.getNodeName ().equals ("WebSynchronizedFileContent"))
             {
              String extension;
              boolean isTextBased;
              String txtContent;

              extension = txtContent = null; isTextBased = true;

              SOAPElement e = getChildElement (chEl, "extension");
              if (e != null) extension = e.getValue ();

              e = getChildElement (chEl, "isTextBased");
              if (e != null) isTextBased = parseBoolean (e.getValue ());

              e = getChildElement (chEl, "textContent");
              if (e != null) txtContent = e.getValue ();

              if (isTextBased && extension != null && txtContent != null)
               list.add (getFactory ().fileTextContent (extension, txtContent));
             }
           }
         }
       }

      return ((list.size () > 0)? (ISynchronizedFileContent[]) list.toArray (new ISynchronizedFileContent [list.size ()]): null);
     }

        /**
         * Notifies the server that a file was read.
         *
         * @param taskId task ID
         * @throws SOAPException thrown on SOAP error
         * @throws IllegalStateException thrown if no session is open
         * @throws NullPointerException thrown if taskId is null
         */
    private void fileRead (String taskId) throws SOAPException
     {
      if (sessionId == null) throw new IllegalStateException ("no session is open");
      if (taskId == null) throw new NullPointerException ("taskId must not be null");

      SOAPMessage req = createMessage();
      SOAPElement e = createRequestElement(req, "fileRead");

      e.addChildElement ("sessionId").addTextNode (sessionId);
      e.addChildElement ("taskId").addTextNode (taskId);

      callWebMethod (req, "fileRead");
     }
   }


  //########################################################################### ISynchronizedFile ###########################################################################
  /* creates a new whole synchronized file object */
  public ISynchronizedFile file (String fileName, ISynchronizedFileContent[] head, ISynchronizedFileFolder[] folders)
   { return (new SynchronizedFile (fileName, head, folders)); }

  public ISynchronizedFile file (String fileName, ISynchronizedFileContent cmdFile, ISynchronizedFileFolder[] folders)
   {
    ISynchronizedFileContent[] head = ((cmdFile != null)? new ISynchronizedFileContent [1]: null);
    if (head != null) { head [0] = cmdFile; return (file (fileName, head, folders)); } else return (null);
   }

  public ISynchronizedFile file (String fileName, ISynchronizedFileContent cmdFile, ISynchronizedFileFolder content)
   {
    ISynchronizedFileFolder[] folders = ((content != null)? new ISynchronizedFileFolder [1]: null);
    if (folders != null) { folders [0] = content; return (file (fileName, cmdFile, folders)); } else return (null);
   }

  public ISynchronizedFile file (String fileName, String cmdFile, String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile)
   {
    ISynchronizedFileContent cmdContent = ((cmdFile != null)? fileCmdContent (cmdFile): null);
    ISynchronizedFileFolder folder = fileFolder (htmlFile, xmlWFile, xmlAFile, xmlBFile);

    if (cmdContent != null && folder != null) return (file (fileName, cmdContent, folder)); else return (null);
   }

  private static class SynchronizedFile implements ISynchronizedFile
   {
    public SynchronizedFile (String fileName, ISynchronizedFileContent[] head, ISynchronizedFileFolder[] folders)
     { m_FileName = fileName; m_Head = head; m_Folders = folders; }

    /* returns file name without extension (e.g. "BHA_97_AKY") */
    public String getFileName () { return (m_FileName); }

    /*
     * Returns synchronization command file
     */
    public ISynchronizedFileContent[] getHead () { return (m_Head); }

    /*
     * Returns synchronization file folders.
     * There is just one folder for anotation purposes. At the case of adjudication there are two or three folders
     */
    public ISynchronizedFileFolder[] getFolders () { return (m_Folders); }

    private String m_FileName;
    private ISynchronizedFileContent[] m_Head;
    private ISynchronizedFileFolder[] m_Folders;
   }


  //########################################################################### ISynchronizedFileFolder ###########################################################################
  /*
   * Creates a new synchronized file folder object
   * Default value for readOnly is false, for author is null
   */
  public ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, boolean isReadOnly, boolean isWorkOnFile, String author)
   { return (new SynchronizedFileFolder (content, isReadOnly, isWorkOnFile, author)); }

  public ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, boolean isReadOnly, String author)
   { return (fileFolder (content, isReadOnly, true, author)); }

  public ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, boolean isReadOnly) { return (fileFolder (content, isReadOnly, true, null)); }
  public ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content, String author) { return (fileFolder (content, false, true, author)); }
  public ISynchronizedFileFolder fileFolder (ISynchronizedFileContent[] content) { return (fileFolder (content, false, true, null)); }

  public ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly, boolean isWorkOnFile, String author)
   {
    int len = 0;

    if (htmlFile != null) len++;
    if (xmlWFile != null) len++;
    if (xmlAFile != null) len++;
    if (xmlBFile != null) len++;

    ISynchronizedFileContent[] content = ((len > 0)? new ISynchronizedFileContent[len]: null);

    if (content != null)
     {
      len = 0;
      if (htmlFile != null) { content [len] = fileTextContent ("html", htmlFile); len++; }
      if (xmlWFile != null) { content [len] = fileTextContent ("w.xml", xmlWFile); len++; }
      if (xmlAFile != null) { content [len] = fileTextContent ("a.xml", xmlAFile); len++; }
      if (xmlBFile != null) { content [len] = fileTextContent ("b.xml", xmlBFile); len++; }

      return (fileFolder (content, isReadOnly, isWorkOnFile, author));
     }

    return (null);
   }

  public ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly, String author)
   { return (fileFolder (htmlFile, xmlWFile, xmlAFile, xmlBFile, isReadOnly, true, author)); }

  public ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, boolean isReadOnly)
   { return (fileFolder (htmlFile, xmlWFile, xmlAFile, xmlBFile, isReadOnly, true, null)); }

  public ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile, String author)
   { return (fileFolder (htmlFile, xmlWFile, xmlAFile, xmlBFile, false, true, author)); }

  public ISynchronizedFileFolder fileFolder (String htmlFile, String xmlWFile, String xmlAFile, String xmlBFile)
   { return (fileFolder (htmlFile, xmlWFile, xmlAFile, xmlBFile, false, true, null)); }


  private static class SynchronizedFileFolder implements ISynchronizedFileFolder
   {
    public SynchronizedFileFolder (ISynchronizedFileContent[] content, boolean isReadOnly, boolean isWorkOnFile, String author)
     { m_IsReadOnly = isReadOnly; m_IsWorkOnFile = isWorkOnFile; m_Author = author; m_Content = content; }

    /*
     * Wheather is this folder read only
     * For adjudication both source files are read only
     */
    public boolean isReadOnly () { return (m_IsReadOnly); }

    /*
     * Wheather this is a file to be changed (if is this allowed by read-only flag)
     *
     * For anotation this method returns true for any folder
     *
     * For adjudication this method returns true only for result file.
     * And at the first time of adjudication file load into feat there is no work file. There are only two not on work files
     */
    public boolean isWorkOnFile () { return (m_IsWorkOnFile); }

    /*
     * Only informative field, could be null
     * For adjudication authors of source files are anotators
     */
    public String getAuthor () { return (m_Author); }

    /* folder content */
    public ISynchronizedFileContent[] getContent () { return (m_Content); }

    private boolean m_IsReadOnly;
    private boolean m_IsWorkOnFile;
    private String m_Author;
    private ISynchronizedFileContent[] m_Content;
   }


  //########################################################################### ISynchronizedFileContent ###########################################################################

  /* creates a new disc text based file object */
  public ISynchronizedFileContent fileTextContent (String fileExtension, String content) { return (new SynchronizedFileContent (fileExtension, content)); }

  /* creates a new command file object */
  public ISynchronizedFileContent fileCmdContent (String content) { return (fileTextContent ("cmd.xml", content)); }

  /* creates a new disc binary file object */
  public ISynchronizedFileContent fileBinaryContent (String fileExtension, byte[] content) { return (new SynchronizedFileContent (fileExtension, content)); }

  private static class SynchronizedFileContent implements ISynchronizedFileContent
   {
    public SynchronizedFileContent (String ext, String content)
     { m_FileExtension = ext; m_FileType = txtFileType; m_TextBasedFileContent = content; m_BinFileContent = null; }

    public SynchronizedFileContent (String ext, byte[] content)
     { m_FileExtension = ext; m_FileType = binFileType; m_TextBasedFileContent = null; m_BinFileContent = content; }

    /* returns file extension - for example "html" */
    public String getFileExtension () { return (m_FileExtension); }

    /* returns file type - possible values are txtFileType and binFileType */
    public int getFileType () { return (m_FileType); }

    /* returns text based file contente - for example for txt, html, xml */
    public String getTxtBasedFileContent () { return (m_TextBasedFileContent); }

    /* returns binary file content - for example image */
    public byte[] getBinFileContent () { return (m_BinFileContent); }

    private String m_FileExtension;
    private int m_FileType;
    private String m_TextBasedFileContent;
    private byte[] m_BinFileContent;
   }
 }
