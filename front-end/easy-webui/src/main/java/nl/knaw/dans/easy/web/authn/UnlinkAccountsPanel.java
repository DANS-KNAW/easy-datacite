package nl.knaw.dans.easy.web.authn;

import java.text.MessageFormat;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.ModalYesNoPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnlinkAccountsPanel extends ModalYesNoPanel {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(UnlinkAccountsPanel.class);

    @SpringBean(name = "federativeUserRepo")
    private FederativeUserRepo federativeUserRepo;

    private List<FederativeUserIdMap> list;

    private Component caller;

    public UnlinkAccountsPanel(final ModalWindow window, final List<FederativeUserIdMap> list, final Component caller) {
        super(window);
        this.list = list;
        this.caller = caller;
        String format = getString("user.unlink.institution.accounts.confirm");
        add(new Label("confirm", MessageFormat.format(format, list.size())));
        add(new Label("relink", getString("user.unlink.institution.accounts.relink")));
    }

    @Override
    protected void handleYesClicked() {
        try {
            for (FederativeUserIdMap idMap : list)
                federativeUserRepo.delete(idMap);
            // works in the unit test but field is not updated in real life
            caller.setVisible(false);
        }
        catch (RepositoryException e) {
            String message = WicketUtil.commonMessage(this, EasyResources.INTERNAL_ERROR, FeedbackMessage.ERROR, new String[] {});
            logger.error(message, e);
            throw new InternalWebError();
        }
        // refresh the page for real life
        Page page = getPage();
        if (page != null && page instanceof AbstractEasyPage) {
            ((AbstractEasyPage) page).refresh();
        }
        if (page != null) {
            setResponsePage(page);
        }
    }
}
