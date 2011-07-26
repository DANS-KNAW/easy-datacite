package nl.knaw.dans.easy.web.deposit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;

import org.apache.wicket.AbortException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Archis2ViewPanel extends AbstractCustomPanel
{

    private static final long serialVersionUID = -9039579510082841556L;
    private static final Logger logger = LoggerFactory.getLogger(Archis2ViewPanel.class);

    private final EasyMetadata easyMetadata;

    public Archis2ViewPanel(String id, EasyMetadata easyMetadata)
    {
        this(id, new Model<EasyMetadata>(easyMetadata));
    }

    public Archis2ViewPanel(String id, IModel<EasyMetadata> model)
    {
        super(id, model);
        easyMetadata = (EasyMetadata) model.getObject();
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        if (isInEditMode())
        {
            throw new UnsupportedOperationException("EditMode not supported.");
        }
        else
        {
            return new ViewPanel();
        }
    }

    class ViewPanel extends Panel
    {

        private static final long serialVersionUID = -3441453142983333780L;

        public ViewPanel()
        {
            super(CUSTOM_PANEL_ID);
            List<BasicIdentifier> nummerList = easyMetadata.getEmdIdentifier().getAllIdentfiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
            ListView<BasicIdentifier> listView = new ListView<BasicIdentifier>("nummers", nummerList)
            {

                private static final long serialVersionUID = 3720302690110935794L;

                @Override
                protected void populateItem(ListItem<BasicIdentifier> item)
                {
                    String label = item.getModelObject().getValue();
                    String nummer = ArchisCollector.getDigits(label);
                    item.add(new ArchisLink("nummerLink", nummer));
                }
            };
            add(listView);

            setVisible(!nummerList.isEmpty());

        }
    }

    class ArchisLink extends Link<String>
    {

        private static final long serialVersionUID = 6730630139772319892L;

        private final String nummer;

        public ArchisLink(String id, String nummer)
        {
            super(id);
            this.nummer = nummer;
            add(new Label("nummer", nummer));
        }

        @Override
        public void onClick()
        {
            if (!loggedOnAsArcheologistOrArchivistOrAdmin())
            {
                return;
            }

            WebResponse response = (WebResponse) getResponse();
            response.setAttachmentHeader(nummer + ".pdf");
            response.setContentType("application/pdf");
            try
            {
                URL url = new URL(ArchisCollector.ARCHIS_PDF + nummer);
                response.write(url.openStream());
                throw new AbortException();
            }
            catch (MalformedURLException e)
            {
                logger.error("Malformed URL: ", e);
                throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_BAD_REQUEST);
            }
            catch (IOException e)
            {
                logger.error("IOException: ", e);
                throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_BAD_GATEWAY);
            }

        }

        private boolean loggedOnAsArcheologistOrArchivistOrAdmin()
        {
            return !EasySession.getSessionUser().isAnonymous()// @formatter:off
                    && (EasySession.getSessionUser().isMemberOfGroup(Arrays.asList(Group.ID_ARCHEOLOGY))
                         || EasySession.getSessionUser().hasRole(Role.ADMIN) 
                         || EasySession.getSessionUser().hasRole(Role.ARCHIVIST));
                    // @formatter:on            
        }
    }

}
