package org.purl.jh.util.io;

import java.io.File;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author jirka
 */
public class FileFilters {
    private final static java.util.ResourceBundle bundle = ResourceBundle.getBundle("org.purl.jh.util.io.Bundle");

    //private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(FileFilters.class);
    public static FileFilter endingFilter(final String aEnding, final String aDescrResourceBundleKey) {
        return filter(".*\\." + aEnding,   aDescrResourceBundleKey);
    }

    public static FileFilter filter(final String aRegex, final String aDescr) {
        return new FileFilter() {
            final Pattern pattern = Pattern.compile(aRegex);

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || pattern.matcher(f.getName()).matches();
            }

            @Override
            public String getDescription() {
                return aDescr;
            }
        };
    }

    public final static FileFilter all  = filter(".*\\.[^\\.]*",   bundle.getString("descr-all"));
    public final static FileFilter txt  = endingFilter("txt",      bundle.getString("descr-txt"));
    public final static FileFilter java = endingFilter("java",     bundle.getString("descr-java"));
    public final static FileFilter xml  = endingFilter("xml",      bundle.getString("descr-xml"));
}
