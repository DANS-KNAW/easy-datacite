package nl.knaw.dans.easy.web.view.dataset;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMetaDataPanel extends AbstractEasyStatelessPanel {
    private static final long serialVersionUID = 3497222166400321697L;
    protected static final Logger logger = LoggerFactory.getLogger(UnitMetaDataPanel.class);

    // should match the columns in $ItemPanel.html
    private final static String[] columns = new String[] {"creationDate", "versionId"};

    private final DatasetModel datasetModel;
    private final boolean showDeleteButton;

    public UnitMetaDataPanel(final String wicketId, final List<UnitMetadata> versions, final DatasetModel datasetModel, final String unitId,
            final boolean addDeleteButton)
    {
        super(wicketId);
        this.datasetModel = datasetModel;
        this.showDeleteButton = addDeleteButton;
        final boolean showTable = versions != null && versions.size() > 0;
        if (versions != null && versions.size() > 1)
            Collections.sort(versions, new CompareByCreationDate());
        add(new WebMarkupContainer("none").setVisible(!showTable));
        add(new UnitMetaDataListView("versions", versions).setVisible(showTable));
    }

    private class CompareByCreationDate implements Comparator<UnitMetadata> {
        @Override
        public int compare(final UnitMetadata arg0, final UnitMetadata arg1) {
            return -arg0.getCreationDate().compareTo(arg1.getCreationDate());
        }
    }

    private final class UnitMetaDataListView extends ListView<UnitMetadata> {
        private static final long serialVersionUID = -6597598635055541684L;

        private UnitMetaDataListView(final String id, final List<? extends UnitMetadata> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<UnitMetadata> item) {
            final IModel<UnitMetadata> model = new CompoundPropertyModel<UnitMetadata>(item.getDefaultModelObject());
            item.add(new ItemPanel("version", model));
        }
    }

    class ItemPanel extends AbstractEasyStatelessPanel {
        private IModel<UnitMetadata> model;
        private static final long serialVersionUID = 7544583798689556606L;

        public ItemPanel(final String wicketId, final IModel<UnitMetadata> model) {
            super(wicketId, model);
            this.model = model;
            final UnitMetaDataResource resource = new UnitMetaDataResource(datasetModel, model.getObject());
            for (final String id : columns) {
                final Link<UnitMetadata> link = new ResourceLink<UnitMetadata>(id + "Link", resource);
                link.add(new Label(id));
                add(link);
            }
            Link deleteButton = new deleteLink("deleteButton", model);
            deleteButton.add(new SimpleAttributeModifier("onclick", "return confirm('Are you sure you want to delete " + "this license agreement?');"));
            add(deleteButton);
        }
    }

    private final class deleteLink extends Link {
        IModel<UnitMetadata> model;

        public deleteLink(String id, IModel<UnitMetadata> model) {
            super(id);
            this.model = model;
        }

        @Override
        public void onClick() {
            final DatasetService datasetService = Services.getDatasetService();
            try {
                datasetService.deleteAdditionalLicense(getSessionUser(), new DmoStoreId(datasetModel.getStoreId()), new DsUnitId(model.getObject().getId()),
                        model.getObject().getCreationDate());
            }
            catch (ServiceException e) {
                final String message = errorMessage(EasyResources.INTERNAL_ERROR);
                logger.error(message, e);
                throw new RestartResponseException(ErrorPage.class);
            }
        }

        @Override
        public boolean isVisible() {
            return showDeleteButton;
        }
    }
}
