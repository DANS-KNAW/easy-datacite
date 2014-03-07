package nl.knaw.dans.common.wicket.components.popup.confirm;

import nl.knaw.dans.common.wicket.components.popup.confirm.ConfirmPanel.ConfirmationAnswer;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class YesNoPanel extends Panel
{
    private static final long serialVersionUID = -7776487314395928663L;

    public YesNoPanel(final ModalWindow window, String confirmMessage, final ConfirmationAnswer answer)
    {
        super(window.getContentId());

        Form<?> yesNoForm = new Form("yesNoForm");

        yesNoForm.add(new MultiLineLabel("confirmMessage", confirmMessage));

        AjaxSubmitLink yesButton = new AjaxSubmitLink("yesButton", yesNoForm)
        {
            private static final long serialVersionUID = -9006695389336829021L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                if (target != null)
                {
                    answer.setAnswer(true);
                    window.close(target);
                }
            }
        };

        AjaxSubmitLink noButton = new AjaxSubmitLink("noButton", yesNoForm)
        {
            private static final long serialVersionUID = -7737147525528570638L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                if (target != null)
                {
                    answer.setAnswer(false);
                    window.close(target);
                }
            }
        };

        yesNoForm.add(yesButton);
        yesNoForm.add(noButton);
        add(yesNoForm);
    }
}
