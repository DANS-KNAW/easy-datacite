package nl.knaw.dans.easy.web.view.dataset;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.fileexplorer.FileExplorer;
import nl.knaw.dans.easy.web.permission.PermissionRequestPage;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;

public class DataFilesPanel extends AbstractDatasetModelPanel {
    private static final Model<NoDate> NO_DATE_MODEL = new Model<NoDate>(new NoDate());
    public static final int TAB_INDEX = 2;
    private static final long serialVersionUID = 3634861329844965675L;

    private static class NoDate implements Serializable {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unused")
        public String toLocalDate() {
            // used via reflection
            return "";
        }
    }

    @SpringBean(name = "fileStoreAccess")
    private FileStoreAccess fileStoreAccess;

    public DataFilesPanel(final String id, final DatasetModel datasetModel) throws StoreAccessException {
        /* sits on a tab created in on onBeforeRenderer, so no onBeforeRenderer needed here */
        super(id, datasetModel);

        final EasyUser user = getEasySession().getUser();
        final Dataset dataset = getDataset();
        final boolean seesAll = seesAll(dataset, user);

        boolean userIsKnown = user != null && !user.equals(EasyUserAnonymous.getInstance());
        boolean userHasGroupAccess = userIsKnown && dataset.isGroupAccessGrantedTo(user);
        boolean userHasPermissionAccess = userIsKnown && dataset.isPermissionGrantedTo(user);
        final boolean showFileExplorer = seesAll
                || fileStoreAccess.hasVisibleFiles(dataset.getDmoStoreId(), userIsKnown, userHasGroupAccess, userHasPermissionAccess);

        final Model<? extends Object> statusDateModel = getPermissionStateDateModel(dataset, user);

        final PageParameters urlForFe = getUrlForFe(dataset);

        /* --------- NEW MESSAGES STRUCTURE --------- */
        final List<AuthzMessage> messages = dataset.getAuthzStrategy().getReadMessages();

        final Component loginButton = new BookmarkablePageLink<DatasetViewPage>("loginButton", DatasetViewPage.class, urlForFe);
        loginButton.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_LOGIN, messages));
        add(loginButton);

        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_EMBARGO));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION));

        final Component permissionButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_BUTTON, dataset);
        permissionButton.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_BUTTON, messages));
        add(permissionButton);

        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_LOGIN));
        add(createMessageLabel(statusDateModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED));
        add(createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED + ".button", dataset));
        add(createMessageLabel(statusDateModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED));
        add(createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED + ".button", dataset));
        add(createMessageLabel(statusDateModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED));
        add(createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED + ".button", dataset));
        add(createMessageLabel(statusDateModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED));
        add(createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED + ".button", dataset));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_PERMISSION_EMBARGO));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_GROUP));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_OTHER));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_NO_FILES));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_DEPOSITOR));
        add(createMessageLabel(datasetModel, messages, AbstractDatasetAutzStrategy.MSG_DEPOSITOR_DRAFT));

        add(new FileExplorer("fe", datasetModel).setVisible(showFileExplorer));
    }

    private Label createMessageLabel(final IModel<? extends Object> model, final List<AuthzMessage> messages, final String wicketId) {
        final Label label = new Label(wicketId, new StringResourceModel(wicketId, model));
        label.setVisible(containsMessage(wicketId, messages));
        return label;
    }

    private boolean containsMessage(final String messageCode, final List<AuthzMessage> messages) {
        for (final AuthzMessage message : messages) {
            if (message.getMessageCode().equals(messageCode)) {
                return true;
            }
        }
        return false;
    }

    private boolean seesAll(final Dataset dataset, final EasyUser user) {
        if (user == null || user.isAnonymous())
            return false;
        if (user.hasRole(Role.ARCHIVIST))
            return true;
        if (!dataset.hasDepositor(user))
            return false;
        return dataset.getAdministrativeState().equals(DatasetState.DRAFT) || dataset.getState().equals("Active");
    }

    private PageParameters getUrlForFe(final Dataset dataset) {
        return DatasetViewPage.urlParametersFor(dataset.getStoreId(), TAB_INDEX, true);
    }

    private Link<Void> createLink(final String id, final Dataset dataset) {
        return new Link<Void>(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(new PermissionRequestPage(new DatasetModel(dataset), (AbstractEasyPage) getPage()));
            }
        };
    }

    private Model<? extends Object> getPermissionStateDateModel(final Dataset dataset, final EasyUser user) {
        final PermissionSequenceList sequenceList = dataset.getPermissionSequenceList();
        if (sequenceList == null || user == null || user.isAnonymous() || !sequenceList.hasSequenceFor(user))
            return NO_DATE_MODEL;
        final PermissionSequence sequence = sequenceList.getSequenceFor(user);
        if (sequence == null)
            return NO_DATE_MODEL;
        DateTime lastStateChange = sequence.getLastStateChange();
        if (lastStateChange == null)
            return NO_DATE_MODEL;
        return new Model<DateTime>(lastStateChange);
    }
}
