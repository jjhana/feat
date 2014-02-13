package org.purl.net.jh.nbutil;

import javax.swing.JPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Utilities for notifying the user.
 * @author jirka
 *
 * @todo add help for each
 * @todo support non-THrowable details
 */
public class Notify {

    public static void eAssert(boolean aTest, String aMsgTemplate, Object ... aParams) {
        if (aTest) return;
        error(aMsgTemplate, aParams);
    }

    public static void error(String aMsgTemplate, Object ... aParams) {
        final String msg = String.format(aMsgTemplate, aParams);
        final NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }

    public static void error(Throwable aThrowable, String aMsgTemplate, Object ... aParams) {
//        final String msg = String.format(aMsgTemplate, aParams);
//        final NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
//        DialogDisplayer.getDefault().notify(nd);

        final String msg = String.format(aMsgTemplate, aParams);
        ErrorNotifyPanel panel = new ErrorNotifyPanel();
        panel.setMsg(aMsgTemplate, aParams);
        panel.setDetails(aThrowable.getMessage()); // todo

        final NotifyDescriptor nd = new NotifyDescriptor.Message(panel, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }

}
