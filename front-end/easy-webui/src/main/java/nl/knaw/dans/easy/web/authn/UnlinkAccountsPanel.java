package nl.knaw.dans.easy.web.authn;

import java.text.MessageFormat;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnlinkAccountsPanel extends Panel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(UnlinkAccountsPanel.class);

    @SpringBean(name = "federativeUserRepo")
    private FederativeUserRepo federativeUserRepo;

    public UnlinkAccountsPanel(final ModalWindow window, final List<FederativeUserIdMap> list, final Component caller)
    {
        super(window.getContentId());
        add(new Label("text", MessageFormat.format(getString("text"), list.size())));

        add(new IndicatingAjaxLink<Void>("yes")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                handleUnlinkAccounts(list);
                caller.setVisible(false);
                window.close(target);
            }
        });

        add(new IndicatingAjaxLink<Void>("no")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                window.close(target);
            }
        });
    }

    private void handleUnlinkAccounts(final List<FederativeUserIdMap> list)
    {
        try
        {
            for (FederativeUserIdMap idMap : list)
                federativeUserRepo.delete(idMap);
        }
        catch (RepositoryException e)
        {
            logger.error(errorMessage(EasyResources.INTERNAL_ERROR), e);
            throw new InternalWebError();
        }
    }

    private String errorMessage(final String messageKey, final String... param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }
}
