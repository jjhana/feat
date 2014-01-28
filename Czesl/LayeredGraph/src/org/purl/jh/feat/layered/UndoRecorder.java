package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerl.*;
import cz.cuni.utkl.czesl.data.layerx.Position;
import cz.cuni.utkl.czesl.data.layerx.ChangeEvent;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;

/**
 * Undo recorder tied to a single {@link LayeredTopComponent} (via a {@link VModel}).
 * All edit operations assume LForms!
 *
 * @author Jirka
 */
public class UndoRecorder implements DataListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(UndoRecorder.class);
    
    private final UndoManager undoManager;
    private final VModel model;

    @Override
    public void handleChange(DataEvent aE) {
        if (aE instanceof ChangeEvent) handleChange((ChangeEvent)aE);
    }
    
    public void handleChange(final ChangeEvent aE) {
        if (aE.getSrcView() == this) return; // already doing undo

//        switch (aE.id) {
//            case ChangeEvent.cFormEdit:   record_formEdit(aE.form, (String)aE.old); break;
//            case ChangeEvent.cFormAdd:    record_formAdd (aE.form); break;
//            case ChangeEvent.cFormDel:    record_formDel ((LForm)aE.form, aE.anchor); break;
//            case ChangeEvent.cFormMove:   record_formMove((LForm)aE.form, (Position)aE.old); break;
//
//            case ChangeEvent.cEdgeAdd: record_edgeAdd(aE.edge         ); break;
//            case ChangeEvent.cEdgeDel: record_edgeDel(aE.edge         ); break;
//            case ChangeEvent.cLegAdd:  record_legAdd (aE.edge, aE.form); break;
//            case ChangeEvent.cLegDel:  record_legDel (aE.edge, aE.form); break;
//
//            case ChangeEvent.cErrorAdd:    record_errorAdd(aE.error ); break;
//            case ChangeEvent.cErrorAttrChange: record_errorX  (aE.error ); break;
//            case ChangeEvent.cErrorDel:    record_errorDel(aE.error ); break;
//
//            case ChangeEvent.cErrorLinkAdd:
//                record_errorLinkAdd(aE.error, aE.edge); break;
//            case ChangeEvent.cErrorLinkDel:
//                record_errorLinkDel(aE.error, aE.edge); break;
//
//            case ChangeEvent.cSentenceMerge: record_sentenceMerge(aE.sentence1, aE.sentence2); break;
//            case ChangeEvent.cSentenceSplit: record_sentenceSplit(aE.sentence1, aE.sentence2); break;
//            case ChangeEvent.cSentenceDel:   record_sentenceDel  (aE.sentence1              ); break;
//            case ChangeEvent.cSentenceCopy:  record_sentenceCopy (aE.sentence1, aE.sentence2); break;
//        }

            if (aE.id.equals(ChangeEvent.cFormEdit))   record_formEdit(aE.form, (String)aE.old);
            else if (aE.id.equals(ChangeEvent.cFormAdd))    record_formAdd (aE.form); 
            else if (aE.id.equals(ChangeEvent.cFormDel))    record_formDel ((LForm)aE.form, aE.anchor); 
            else if (aE.id.equals(ChangeEvent.cFormMove))   record_formMove((LForm)aE.form, (Position)aE.old); 

            else if (aE.id.equals(ChangeEvent.cEdgeAdd)) record_edgeAdd(aE.edge         ); 
            else if (aE.id.equals(ChangeEvent.cEdgeDel)) record_edgeDel(aE.edge         ); 
            else if (aE.id.equals(ChangeEvent.cLegAdd))  record_legAdd (aE.edge, aE.form); 
            else if (aE.id.equals(ChangeEvent.cLegDel))  record_legDel (aE.edge, aE.form); 

            else if (aE.id.equals(ChangeEvent.cErrorAdd))    record_errorAdd(aE.error ); 
            else if (aE.id.equals(ChangeEvent.cErrorAttrChange)) record_errorX  (aE.error ); 
            else if (aE.id.equals(ChangeEvent.cErrorDel))    record_errorDel(aE.error ); 

            else if (aE.id.equals(ChangeEvent.cErrorLinkAdd))
                record_errorLinkAdd(aE.error, aE.edge); 
            else if (aE.id.equals(ChangeEvent.cErrorLinkDel))
                record_errorLinkDel(aE.error, aE.edge); 

            else if (aE.id.equals(ChangeEvent.cSentenceMerge)) record_sentenceMerge(aE.sentence1, aE.sentence2); 
            else if (aE.id.equals(ChangeEvent.cSentenceSplit)) record_sentenceSplit(aE.sentence1, aE.sentence2); 
            else if (aE.id.equals(ChangeEvent.cSentenceDel))   record_sentenceDel  (aE.sentence1              ); 
            else if (aE.id.equals(ChangeEvent.cSentenceCopy))  record_sentenceCopy (aE.sentence1, aE.sentence2); 
    
    }

    public static abstract class MyUndoableEdit extends AbstractUndoableEdit {
        @Override
        public boolean canRedo() {
            return false;
        }
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            undoX();
        }

        abstract void undoX();


    }

    /**
     *
     * Registers this recorder as a listener of the model.
     * 
     * @param aUndoManager
     * @param aModel
     */
    UndoRecorder(final UndoManager aUndoManager, final VModel aModel) {
        undoManager = aUndoManager;
        model = aModel;
        model.addChangeListener(this);
    }

    private void record(final UndoableEdit aE) {
        undoManager.undoableEditHappened(new UndoableEditEvent(this, aE));
    }

    private void record_cannotUndo() {
        record(new AbstractUndoableEdit() {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                 return false;
            }
        });
    }

    // todo need old form
    private void record_formEdit(final FForm aForm, final String aOldStr) {
        record(new MyUndoableEdit() {
            void undoX() {
                aForm.getLayer().formEdit(aForm, aOldStr, UndoRecorder.this, null);
            }
        });
    }

    private void record_formAdd(final FForm aForm) {
        record(new MyUndoableEdit() {
            void undoX() {
                aForm.getLayer().formDel(aForm, UndoRecorder.this, null);
            }
        });
    }


    private void record_formDel(final LForm aForm, final Position aAnchor) {
        record(new MyUndoableEdit() {
            void undoX() {
                aForm.getLayer().formAdd(aForm, aAnchor, UndoRecorder.this, null);
            }
        });
    }

    private void record_formMove(final LForm aForm, final Position aOldPos) {
        record(new MyUndoableEdit() {
            void undoX() {
                log.info("undoX - record_formMove: form=%s, oldpos=%s", aForm, aOldPos );
                aForm.getLayer().formMove(aForm, aOldPos, UndoRecorder.this, null);
            }
        });
    }

    private void record_edgeAdd(final Edge edge) {
        record(new MyUndoableEdit() {
            void undoX() {
                edge.getLayer().edgeDel(edge, UndoRecorder.this, null);
            }
        });
    }

    private void record_edgeDel(final Edge edge) {
        record(new MyUndoableEdit() {
            void undoX() {
                edge.getLayer().edgeAdd(edge, UndoRecorder.this, null);
                // todo error links are not restored??

            }
        });
    }

    private void record_legAdd(final Edge edge, final FForm form) {
        record(new MyUndoableEdit() {
            void undoX() {
                edge.getLayer().legDel(edge, form, UndoRecorder.this, null);
            }
        });
    }

    private void record_legDel(final Edge edge, final FForm form) {
        record(new MyUndoableEdit() {
            void undoX() {
                edge.getLayer().legAdd(edge, form, UndoRecorder.this, null);
            }
        });
    }


    private void record_errorAdd(final Errorr error) {
        record( new MyUndoableEdit() {
            void undoX() {
                ((LLayer)error.getLayer()).errorDel(error, UndoRecorder.this, null);
            }
        });
    }

    private void record_errorX(final Errorr error) {
        log.info("record_errorX -- cannot undo");
        record_cannotUndo();
        // todo need which change
            //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void record_errorDel(final Errorr error) {   // nee
        record( new MyUndoableEdit() {
            void undoX() {
                ((LLayer)error.getLayer()).errorAdd(error, UndoRecorder.this, null);
            }
        });
    }

    private void record_errorLinkAdd(final Errorr error, final Edge edge) {
        record(new MyUndoableEdit() {
            void undoX() {
                ((LLayer)error.getLayer()).errorLinkDel(error, edge, UndoRecorder.this, null);
            }
        });
    }

    private void record_errorLinkDel(final Errorr error, final Edge edge) {
        record(new MyUndoableEdit() {
            void undoX() {
                ((LLayer)error.getLayer()).errorLinkAdd(error, edge, UndoRecorder.this, null);
            }
        });
    }

    private void record_sentenceMerge(final Sentence sentence1, final Sentence sentence2) {
        record(new MyUndoableEdit() {
            void undoX() {
                final LForm form2 = sentence2.get(0);
                final Position  anchor = Position.before(form2);
                sentence1.getLayer().sentenceSplit(anchor, UndoRecorder.this, null);
            }
        });
    }

    private void record_sentenceSplit(final Sentence sentence1, final Sentence sentence2) {
        record(new MyUndoableEdit() {
            void undoX() {
                sentence1.getLayer().sentenceMerge(sentence1, UndoRecorder.this, null);
            }
        });
    }

    private void record_sentenceDel(final Sentence sentence1) {
// todo
//        final int pos = sentence1.getParent().getSentences().indexOf(sentence1);
//        final Sentence prevSent = (pos == 0) ? null : sentence1.getParent().getSentences().get(pos - 1);
//        record(new MyUndoableEdit() {
//            void undoX() {
//                model.sentenceAdd(sentence1, prevSent, UndoRecorder.this, null);
//                
//                Lists.addAfter(sentence1.getParent().getSentences(), sentence1, sentence1);
//            }
//        });
//        // add sentence
//        // return forms
            
        log.info("record_sentenceDel -- cannot undo");
        record_cannotUndo();
    }

    private void record_sentenceCopy(final Sentence sentence1, final Sentence sentence2) {
        log.info("record_sentenceCopy -- cannot undo");
        record_cannotUndo();
    }
}
