package nl.knaw.dans.common.wicket.components.popup.confirm;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ConfirmPanel extends Panel {

    private static final long serialVersionUID = 2837409577443616047L;

    protected ModalWindow confirmModal;
    protected ConfirmationAnswer answer;
    protected Map<String, String> modifiersToApply;

    public ConfirmPanel(String id, String buttonName, String modalMessageText, String modalTitleText) {
        super(id);
        answer = new ConfirmationAnswer(false);
        addElements(id, buttonName, modalMessageText, modalTitleText);
    }

    protected void addElements(String id, String buttonName, String modalMessageText, String modalTitleText) {
        confirmModal = createConfirmModal(id, modalMessageText, modalTitleText);
        Form<?> form = new Form("confirmForm");
        add(form);

        AjaxSubmitLink confirmButton = new AjaxSubmitLink("confirmButton") {
            private static final long serialVersionUID = 8077215621843817611L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                confirmModal.show(target);
            }
        };
        confirmButton.add(new Label("confirmButtonName", buttonName));
        form.add(confirmButton);
        form.add(confirmModal);
    }

    protected abstract void onConfirm(AjaxRequestTarget target);

    protected abstract void onCancel(AjaxRequestTarget target);

    protected ModalWindow createConfirmModal(String id, String modalMessageText, String modalTitleText) {
        ModalWindow modalWindow = new ModalWindow("confirmModal");
        modalWindow.setTitle(modalTitleText);
        modalWindow.setUseInitialHeight(false);
        modalWindow.setInitialWidth(400);
        modalWindow.setCookieName(id);

        modalWindow.setContent(new YesNoPanel(modalWindow, modalMessageText, answer));

        modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            private static final long serialVersionUID = -3911323073933533800L;

            @Override
            public void onClose(AjaxRequestTarget target) {
                if (answer.isAnswer()) {
                    onConfirm(target);
                } else {
                    onCancel(target);
                }
            }
        });
        return modalWindow;
    }

    public class ConfirmationAnswer implements Serializable {
        private static final long serialVersionUID = -7876765511746536180L;

        private boolean answer;

        public ConfirmationAnswer(boolean answer) {
            this.answer = answer;
        }

        public boolean isAnswer() {
            return answer;
        }

        public void setAnswer(boolean answer) {
            this.answer = answer;
        }
    }
}
