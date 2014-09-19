package nl.knaw.dans.easy.web.deposit.repeater;

import java.net.URI;
import java.net.URISyntaxException;

import nl.knaw.dans.easy.web.deposit.repeasy.RelationListWrapper.RelationModel;

import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

public class VerifyUrlLink extends SubmitLink {

    private static final long serialVersionUID = 1L;
    private IModel<RelationModel> model;

    public VerifyUrlLink(String id, final IModel<RelationModel> model) {
        super(id);
        this.model = model;
    }

    @Override
    public void onSubmit() {
        final String uri = model.getObject().getSubjectLink();
        if (uri != null && uri.trim().length() > 0) {
            try {
                new URI(uri);
                getRequestCycle().setRequestTarget(new RedirectRequestTarget(uri));
            }
            catch (URISyntaxException e) {}
        }
    }
}
