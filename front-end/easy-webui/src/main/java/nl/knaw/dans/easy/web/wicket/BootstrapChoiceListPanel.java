package nl.knaw.dans.easy.web.wicket;

import java.util.List;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class BootstrapChoiceListPanel<T> extends Panel {
    private static final long serialVersionUID = 1L;

    protected boolean inline = false;
    protected List<T> choices;

    public BootstrapChoiceListPanel(String id, IModel<T> model, List<T> choices) {
        super(id, model);
        this.choices = choices;

        init();
    }

    protected void init() {}

    protected Label createChoiceText(LabeledWebMarkupContainer choiceItem) {
        Label choiceText = new Label("choiceText", choiceItem.getLabel());
        return choiceText;
    }

    protected FormComponentLabel createContainerLabel(LabeledWebMarkupContainer choiceItem) {
        FormComponentLabel containerLabel = new FormComponentLabel("containerLabel", choiceItem);
        return containerLabel;
    }

    public boolean isInline() {
        return inline;
    }

    public BootstrapChoiceListPanel<T> setInline(boolean inline) {
        this.inline = inline;
        return this;
    }
}
