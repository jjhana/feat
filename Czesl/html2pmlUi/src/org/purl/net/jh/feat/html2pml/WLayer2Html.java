package org.purl.net.jh.feat.html2pml;

import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.feat.NbData.WLayerDataObject;

/**
 * Saves w-layer as a plain/html-text.
 * 
 * Only plain text is printed (no formatting, no transcribing codes).
 * 
 * @author jirka
 */
public class WLayer2Html {
    private final WLayerDataObject dobj;
    private final PrintWriter w;
    
    private boolean confHtml = false;  // todo configurable
    private boolean confAlwaysSpace = true;  // todo configurable
    
    
    public WLayer2Html(WLayerDataObject aDObj, PrintWriter aW) {
        dobj = aDObj;
        w = aW;
    }
    
    public void go() {
        WLayer wlayer = dobj.getData();
        
        for(WDoc doc : wlayer.col()) {
            for(WPara para : filterParas(doc.getParas())) {
                if (confHtml) w.println("<p>");
                
                List<WForm> wforms = para.getForms();
                for (int i = 0; i < wforms.size(); i++) {
                    String wform = clean(wforms.get(i));
                    
                    // fixing incorrect tokenization for <.>
                    if (wform.endsWith("<") && i+2 < wforms.size() && ".".equals(clean(wforms.get(i+1))) && ">".equals(clean(wforms.get(i+2))) ) {
                        wform = wform.substring(0,wform.length()-1);
                    }
                    w.print(wform);
                    
                    if (confAlwaysSpace  || wforms.get(i).hasSpaceAfter()) w.print(" ");
                    
                }
                for (WForm form : para.getForms()) {
                }
                w.println( confHtml ? "</p>" : "");
            }
            w.println();
            w.println();
        }
    }
    
    private Iterable<WPara> filterParas(List<WPara> paras) {
        if (paras.isEmpty()) return paras;
        
        WPara para = paras.get(0);
        
        int nUnderscores = 0;
        for(WForm form : para.getForms()) {
            if ("_".equals(form.getToken())) nUnderscores++;
        }
        if (nUnderscores > 1) return paras.subList(1, paras.size());
        
        return paras;
    }
    
    

    
    
    
    /**
         <.> -> null
         <°> -> null
         <...> -> null     * 
         { } -> null  (aby se zachoval pocet tokenu)
       * abc|def -> abc (bez zavorek)
        {abc} -> abc
        {abc|..} -> abc
        {abc}<dt> -> abc (na tom se to neevaluuje, ale je to tam nechano kvuli kontextu
        XXX -- možná vyhodit? neemenduje se
        {abc}<ni> -> abc (ale možná se <ni> nedostane na R0)
     * @param aForm
     * @return 
     */
    public static String clean(final WForm aForm) {
        return clean(aForm.getToken());
    }
    
    public static String clean(String token) {
        if (Arrays.asList("<.>", "<°>", "<°>", 
                "&lt;.&gt;", "&lt;°&gt;", "}&lt;dt&gt;").contains(token) || 
                Pattern.compile("\\<.*\\>").matcher(token).matches() ||
                Pattern.compile("\\&lt\\;.*\\&gt\\;").matcher(token).matches()) {
            return "";
        }
        else {
            token = token.replaceAll("\\A([^{}|\\s]+)\\|.*\\Z", "$1");      //abc|def -> abc
            token = token.replaceAll("\\{\\s\\}", "");                      // { } -> null
            token = token.replaceAll("\\{([^\\|\\s\\{\\}]+)\\}", "$1");     // {abc} -> abc
            token = token.replaceAll("\\{([^\\|\\s\\{\\}]+)\\|[^{}]*\\}", "$1");     // {abc|efg} -> abc
            token = token.replaceAll("\\A\\{([^}]+)\\Z", "$1");     // {abc -> abc  // error in conversion
//            token = token.replaceAll("(.*)<°>\\Z", "$1");     // abc<°> -> abc  // error in tokenization
//            token = token.replaceAll("(.*)<.>\\Z", "$1");     // abc<.> -> abc  // error in tokenization
            token = token.replaceAll("\\{(.*)\\}<[^><]*>\\Z", "$1");           // {abc}<...> -> abc  // error in tokenization
            token = token.replaceAll("\\{(.*)\\}&lt;[^\\&]*&gt;\\Z", "$1");     // {abc}<...> -> abc  // error in tokenization
            token = token.replaceAll("(.*)\\}<[^><]*>\\Z", "$1");           // abc}<...> -> abc  // error in tokenization
            token = token.replaceAll("(.*)\\}&lt;[^\\&]*&gt;\\Z", "$1");     // abc}<...> -> abc  // error in tokenization
            token = token.replaceAll("(.*)<[^><]*>\\Z", "$1");           // abc<...> -> abc  // error in tokenization
            token = token.replaceAll("(.*)&lt;[^\\&]*&gt;\\Z", "$1");     // abc<...> -> abc  // error in tokenization
        }
        
        return token;
    }

    
}
