package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.Style;
import nl.knaw.dans.easy.web.view.dataset.AdministrationPanel;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectUserPanel extends AbstractEasyPanel {
    private static final long serialVersionUID = 5428378223455873788L;

    private static final Logger logger = LoggerFactory.getLogger(AdministrationPanel.class);

    private final IdModel model;

    private IModel labelModel;
    private boolean initiated;

    public SelectUserPanel(String wicketId) {
        super(wicketId);
        model = new IdModel();
    }

    public IModel getLabelModel() {
        return labelModel;
    }

    public void setLabelModel(IModel labelModel) {
        this.labelModel = labelModel;
    }

    public String getSelectedId() {
        return model.getSelectedId();
    }

    public EasyUser getSelectedUser() {
        String userId = getSelectedId();
        EasyUser user = null;
        if (userId == null) {
            final String message = errorMessage(EasyResources.NO_USER_SELECTED);
            logger.error(message);
        } else {
            try {
                user = Services.getUserService().getUserById(getSessionUser(), userId);
            }
            catch (ObjectNotAvailableException e) {
                final String message = errorMessage(EasyResources.NONVALID_USER, userId);
                logger.error(message, e);
            }
            catch (ServiceException e) {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new InternalWebError();
            }
        }
        return user;
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        add(Style.USER_SELECTOR_HEADER_CONTRIBUTION);
        add(new FeedbackPanel("supFeedback", new IFeedbackMessageFilter() {
            private static final long serialVersionUID = 3253987728694803331L;

            public boolean accept(FeedbackMessage message) {
                return message.getReporter().getId().equals(SelectUserPanel.this.getId());
            }

        }));

        add(new Label("supLabel", labelModel == null ? new ResourceModel("sup.label") : labelModel));

        add(new UserSelector("autoCompleteTextField", model));

    }

}
