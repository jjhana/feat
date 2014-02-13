package org.purl.jh.pml.event;

/**
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public interface DataListener extends java.util.EventListener {
    void handleChange(DataEvent<?> aE);
}
