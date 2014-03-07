package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;

public class PossiblyDisabledTextArea<T> extends TextArea<T>
{
    private static final long serialVersionUID = 1L;
    private boolean editMode;

    /**
     * @param id
     * @param model
     * @param editMode
     *        if true the following attributes are added: readonly="true" class="readonly". Add to your
     *        css something like:
     * 
     *        <pre>
     *  textarea.readonly  {
     *    border:0;
     *    color: #575756;
     *  }
     * </pre>
     */
    public PossiblyDisabledTextArea(String id, IModel<T> model, boolean editMode)
    {
        super(id, model);
        this.editMode = editMode;
        setEnabled(editMode);
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        if (editMode)
            return;
        tag.put("readonly", editMode);
        tag.put("class", "readonly");
    }

}
