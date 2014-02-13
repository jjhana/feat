package org.purl.jh.pml;

public interface IFormsLayer<F extends Form> {
    Iterable<F> getForms();
}
