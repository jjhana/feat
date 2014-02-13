package org.purl.net.jh.nbutil.io;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.openide.windows.InputOutput;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.err.Throwables;

/**
 * Error logger printing to the NB console, intended to be used in batch-processing.
 * If the error record contains an exception, it is expected to be a FormatError,
 * otherwise it is reported as an internal error in the program.
 *
 * @todo should be done via handler (but shielded from std loggers as NB would consider it an error)
 * @author jirka
 */
public class NbOutLogger extends java.util.logging.Logger {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(NbOutLogger.class);

    public final static Color errColor = Color.RED.darker();
    public final static Color warningColor = Color.ORANGE.darker();
    public final static Color infoColor = Color.BLACK;
    public final static Color finalSuccesColor = Color.GREEN.darker();
    public final static Color finalErrColor = Color.RED;

    private final InputOutput io;


    public NbOutLogger(InputOutput io, String name, String resourceBundleName) {
        super(name, resourceBundleName);
        this.io = io;
    }

    /**
     * Trims a list if it is over a specified limit.
     * @param <T>
     * @param aList
     * @param aMax
     * @return
     * @todo util.Cols
     */
    public static <T> List<T> limit(List<T> aList, int aMax) {
        return aList.subList(0, Math.min(aMax, aList.size()));
    }

    /**
     * Returns a sample of the exception's stack trace.
     * @param aEx expection whose stack trace should be returned
     * @param aMaxLines maximum number of lines in the returned sample
     * @return
     * @todo util
     */
    public static List<StackTraceElement> stack(Throwable aEx, int aMaxLines) {
        return limit( Arrays.asList(aEx.getStackTrace()), aMaxLines);
    }

    @Override
    public void log(LogRecord record) {
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            if (record.getThrown() == null) {
                IoUtils.println(io, record.getMessage(), null, true, errColor);
            }
            // FormatError is used for all user caused errors
            else if (record.getThrown() instanceof FormatError) {
                IoUtils.println(io, record.getMessage(), null, true, errColor);
                IoUtils.println(io, record.getThrown().getMessage(), null, true, errColor);
            }
            // other type of error, since it is not handled above, it is considered unexpected
            else {
                IoUtils.println(io, "Internal Error - Please report", null, true, errColor);
                IoUtils.println(io, "   " + record.getMessage(), null, true, errColor);
                IoUtils.println(io, Throwables.stackTrace2String(record.getThrown()), null, true, errColor);
            }
        } else if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            IoUtils.println(io, record.getMessage(), warningColor);
        } else {
            IoUtils.println(io, record.getMessage(), infoColor);
        }
    }


}
