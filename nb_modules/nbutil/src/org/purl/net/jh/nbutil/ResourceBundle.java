package org.purl.net.jh.nbutil;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Resource bundle with a fallback for missing keys (returns the key itself).
 * 
 * @author jirka
 */
public class ResourceBundle {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ResourceBundle.class);

    private final Class<?> clazz;
    private final java.util. ResourceBundle bundle;

//    public static ResourceBundle getBundle(String baseName, Locale targetLocale, ClassLoader loader, Control control) {
//        return new ResourceBundle(baseName, org.openide.util.NbBundle.getBundle(baseName, targetLocale, loader, control));
//    }

//    public static ResourceBundle getBundle(Class<?> aClazz, Locale locale, ClassLoader loader) {
//        return new ResourceBundle(aClazz, org.openide.util.NbBundle.getBundle(aClazz, locale, loader));
//    }

//    public static ResourceBundle getBundle(String baseName, Locale targetLocale, Control control) {
//        return new ResourceBundle(baseName, org.openide.util.NbBundle.getBundle(baseName, targetLocale, control));
//    }

//    public static ResourceBundle getBundle(Class<?> clazz, Locale locale) {
//        return new ResourceBundle(baseName, org.openide.util.NbBundle.getBundle(clazz, locale));
//    }

//    public static ResourceBundle getBundle(String baseName, Control control) {
//        return new ResourceBundle(baseName, org.openide.util.NbBundle.getBundle(baseName, control));
//    }

    public static ResourceBundle getBundle(Class<?> aClazz) {
        return new ResourceBundle(aClazz, org.openide.util.NbBundle.getBundle(aClazz));
    }

    protected ResourceBundle(Class<?> aClazz, java.util.ResourceBundle aBundle) {
        clazz = aClazz;
        bundle = aBundle;    
    }
    
    
    public Set<String> keySet() {
        return bundle.keySet();
    }

    public final String[] getStringArray(String key) {
        return bundle.getStringArray(key);
    }

    public final String getString(String key) {
        try {
            return bundle.getString(key);
        }
        catch ( MissingResourceException e) {
            log.severe("Missing resource: class=%s, key=%s", clazz, key);
            return key;
        }
    }

    public final Object getObject(String key) {
        return bundle.getObject(key);
    }

    public Locale getLocale() {
        return bundle.getLocale();
    }

    public Enumeration<String> getKeys() {
        return bundle.getKeys();
    }


    public boolean containsKey(String key) {
        return bundle.containsKey(key);
    }

    public static void clearCache(ClassLoader loader) {
        java.util.ResourceBundle.clearCache(loader);
    }

    public static void clearCache() {
        java.util.ResourceBundle.clearCache();
    }

    
}
