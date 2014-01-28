package org.purl.jh.speedsupport.speedPanel;

/**
 * Speed command.
 *
 * Possible commands are listed in the command file, one of them must be chosen
 * before the document is moved to Outbox.
 *
 * @author jirka
 */
public interface Command {

    public String getId();
    public String getName();
    public String getTitle();
}
