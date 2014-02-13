package org.purl.jh.speedsupport.data;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.purl.jh.speedsupport.CmdDataObject;
import org.purl.jh.speedsupport.Comment;
import org.purl.jh.speedsupport.speedPanel.Command;
import org.purl.jh.util.col.Mapper;
import org.purl.jh.util.col.MappingList;

/**
 * Not really a pml layer, but should be.
 * @todo optimize getting of properties, the jdom is retrieved and the file is parsed each time
 * @author j
 */
public class CmdLayer implements Node.Cookie {

    private CmdDataObject dobj;
    private List<Command> commands = null;
    private List<Comment> comments = null;

    public CmdLayer(CmdDataObject outer) {
        this.dobj = outer;
    }

    public CmdDataObject getDobj() {
        return dobj;
    }
    
    
//    /**
//     * Returns all (transitively) linked files
//     *
//     * Unfortunately, the cmd file does contain a reference to the main data file,
//     * so we simply return all the files with the same name
//     */
//    public List<FileObject> getFiles() {
//        final String baseName = dobj.getPrimaryFile().getName();
//        final List<FileObject> fobjs = new ArrayList<FileObject>();
//        for (FileObject fobj : dobj.getPrimaryFile().getParent().getChildren()) {
//            if (fobj.getNameExt().startsWith(baseName)) {
//                fobjs.add(fobj);
//            }
//        }
//        return fobjs;
//    }


    private final Mapper<Element, Command> element2command = new Mapper<Element, Command>() {

        @Override
        public Command map(final Element aOrigItem) {
            return new Command() {

                @Override
                public String getId() {
                    return aOrigItem.getAttributeValue("id");
                }

                @Override
                public String getName() {
                    return aOrigItem.getAttributeValue("name");
                }

                @Override
                public String getTitle() {
                    return aOrigItem.getAttributeValue("title");
                }

                @Override
                public boolean equals(Object obj) {
                    if (!(obj instanceof Command)) {
                        return false;
                    }
                    return getId().equals(((Command) obj).getId());
                }

                @Override
                public int hashCode() {
                    return getId().hashCode();
                }
            };
        }
    };

    private final Mapper<Element, Comment> element2comment = new Mapper<Element, Comment>() {

        @Override
        public Comment map(final Element aOrigItem) {
            return new Comment() {
                @Override
                public String getAt() {
                    return aOrigItem.getAttributeValue("at");
                }

                @Override
                public String getUser() {
                    return aOrigItem.getAttributeValue("userName");
                }

                @Override
                public String getText() {
                    return aOrigItem.getTextTrim();
                }
            };
        }
    };
    
//    public String getMode() {
//        return dobj.getData().getJdom().getRootElement().getChild("Operation").getAttributeValue("mode");
//    }

    public List<Comment> getComments() {
         if (comments == null) {
            final Element opEl = dobj.getData().getJdom().getRootElement().getChild("Data");
            comments = new MappingList<Element, Comment>(opEl.getChildren("Comment"), element2comment);
        }
        return comments;
    }

    /**
     * List of possible command.
     * @return
     */
    public List<Command> getCommands() {
        if (commands == null) {
            final Element opEl = dobj.getData().getJdom().getRootElement().getChild("Operation");
            commands = new MappingList<Element, Command>(opEl.getChildren("Command"), element2command);
        }
        return commands;
    }

    public String getFromUserName() {
        final Element root = dobj.getData().getJdom().getRootElement();
        final Element identificationEl = root.getChild("Identification");
        if (identificationEl == null) return null;

        return  identificationEl.getAttributeValue("fromUserName");
    }

    public String getSendTime() {
        final Element root = dobj.getData().getJdom().getRootElement();
        final Element identificationEl = root.getChild("Identification");
        if (identificationEl == null) return null;

        return  identificationEl.getAttributeValue("sendTime");
    }

    /**
     * A command selected for submission. It is one of {@link #getCommands()}.
     * @return
     */
    public Command getCommand() {
        final Element root = dobj.getData().getJdom().getRootElement();
        final Element resultEl = root.getChild("Result");
        if (resultEl == null) return null;

        final String id = resultEl.getAttributeValue("commandId");
        for (Command cmd : getCommands()) {
            if (cmd.getId().equals(id)) {
                return cmd;
            }
        }
        return null;
    }

    public void setCommand(Command aCommand) {
        final Element root = dobj.getData().getJdom().getRootElement();
        Element resultEl = root.getChild("Result");
        if (resultEl == null) {
            resultEl = new Element("Result");
            root.addContent(resultEl);
        }
        resultEl.setAttribute("commandId", aCommand.getId());
        dobj.setModified(true);
    }

    /**
     * Commands comment.
     * @return
     */
    public String getComment() {
        final Element root = dobj.getData().getJdom().getRootElement();
        Element resultEl = root.getChild("Result");
        if (resultEl == null) {
            return null;
        }
        return resultEl.getText();
    }

    public void setComment(String aComment) {
        final Element root = dobj.getData().getJdom().getRootElement();
        Element resultEl = root.getChild("Result");
        if (resultEl == null) {
            resultEl = new Element("Result");
            root.addContent(resultEl);
        }
        resultEl.setText(aComment);
        dobj.setModified(true);
    }




}
