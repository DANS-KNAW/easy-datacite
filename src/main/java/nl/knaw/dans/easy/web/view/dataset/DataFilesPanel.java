package nl.knaw.dans.easy.web.view.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.fileexplorer2.FileExplorer;
import nl.knaw.dans.easy.web.permission.PermissionRequestPage;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

public class DataFilesPanel extends AbstractDatasetModelPanel
{
    public static final int TAB_INDEX = 2;
    private static final long serialVersionUID = 3634861329844965675L;

    public DataFilesPanel(final String id, final DatasetModel datasetModel)
    {
        /* sits on a tab created in on onBeforeRenderer, so no onBeforeRenderer needed here */
        super(id, datasetModel);

        final EasyUser user = getEasySession().getUser();
        final Dataset dataset = getDataset();
        final boolean seesAll = seesAll(dataset, user);
        
        boolean showFileExplorer = seesAll || dataset.hasVisibleItems(user);

        DateTime statusDate = getPermissionStateDate(dataset, user);
        if(statusDate == null) {
        	statusDate = new DateTime();
        }
        final Model<DateTime> statusDateModel = new Model<DateTime>(statusDate);
        
        final PageParameters urlForFe = getUrlForFe(dataset);
        
        /* --------- NEW MESSAGES STRUCTURE --------- */
        List<AuthzMessage> messages = dataset.getAuthzStrategy().getReadMessages();
        
        BookmarkablePageLink<DatasetViewPage> loginButton = new BookmarkablePageLink<DatasetViewPage>("loginButton", DatasetViewPage.class, urlForFe);
        loginButton.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_LOGIN, messages));
        add(loginButton);
        
        Label embargo = new Label(AbstractDatasetAutzStrategy.MSG_EMBARGO, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_EMBARGO, datasetModel));
        embargo.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_EMBARGO, messages));
        add(embargo);
        
        Label permission = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION, datasetModel));
        permission.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION, messages));
        add(permission);
        
        final Link<Void> permissionButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_BUTTON, dataset);
        permissionButton.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_BUTTON, messages));
        add(permissionButton);
        
        Label permissionLogin = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_LOGIN, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_LOGIN, datasetModel));
        permissionLogin.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_LOGIN, messages));
        add(permissionLogin);
        
        Label permissionSubmitted = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED, statusDateModel));
        permissionSubmitted.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED, messages));
        add(permissionSubmitted);
        
        final Link<Void> permissionSubmittedButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_SUBMITTED+".button", dataset);
        add(permissionSubmittedButton);
        
        Label permissionReturned = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED, statusDateModel));
        permissionReturned.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED, messages));
        add(permissionReturned);
        
        final Link<Void> permissionReturnedButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_RETURNED+".button", dataset); 
        add(permissionReturnedButton);
        
        Label permissionGranted = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED, statusDateModel));
        permissionGranted.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED, messages));
        add(permissionGranted);
        
        final Link<Void> permissionGrantedButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_GRANTED+".button", dataset);
        add(permissionGrantedButton);
        
        Label permissionDenied = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED, statusDateModel));
        permissionDenied.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED, messages));
        add(permissionDenied);
        
        final Link<Void> permissionDeniedButton = createLink(AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED+".button", dataset);
        add(permissionDeniedButton);
        
        Label permissionEmbargo = new Label(AbstractDatasetAutzStrategy.MSG_PERMISSION_EMBARGO, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_PERMISSION_EMBARGO, datasetModel));
        permissionEmbargo.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_PERMISSION_EMBARGO, messages));
        add(permissionEmbargo);
        
        Label groupAccess = new Label(AbstractDatasetAutzStrategy.MSG_GROUP, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_GROUP, datasetModel));
        groupAccess.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_GROUP, messages));
        add(groupAccess);
        
        Label otherAccess = new Label(AbstractDatasetAutzStrategy.MSG_OTHER, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_OTHER, datasetModel));
        otherAccess.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_OTHER, messages));
        add(otherAccess);
        
        Label noVisibleFiles = new Label(AbstractDatasetAutzStrategy.MSG_NO_FILES, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_NO_FILES, datasetModel));
        noVisibleFiles.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_NO_FILES, messages));
        add(noVisibleFiles);
        
        Label depositor = new Label(AbstractDatasetAutzStrategy.MSG_DEPOSITOR, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_DEPOSITOR, datasetModel));
        depositor.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_DEPOSITOR, messages));
        add(depositor);
        
        Label depositorDraft = new Label(AbstractDatasetAutzStrategy.MSG_DEPOSITOR_DRAFT, new StringResourceModel(AbstractDatasetAutzStrategy.MSG_DEPOSITOR_DRAFT, datasetModel));
        depositorDraft.setVisible(containsMessage(AbstractDatasetAutzStrategy.MSG_DEPOSITOR_DRAFT, messages));
        add(depositorDraft);
        
        add(new FileExplorer("fe", datasetModel).setVisible(showFileExplorer));
    }
    
    private boolean containsMessage(String messageCode, List<AuthzMessage> messages) {
    	for(AuthzMessage message : messages) {
    		if(message.getMessageCode().equals(messageCode)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean seesAll(final Dataset dataset, final EasyUser user)
    {
        if (user == null || user.isAnonymous())
            return false;
        if (user.hasRole(Role.ARCHIVIST))
            return true;
        if (! dataset.hasDepositor(user) ) return false;
        return dataset.getAdministrativeState().equals(DatasetState.DRAFT) || dataset.getState().equals("Active")  ;
    }

    private PageParameters getUrlForFe(final Dataset dataset)
    {
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
    
    private DateTime getPermissionStateDate(Dataset dataset, EasyUser user)
    {
        final PermissionSequenceList sequenceList = dataset.getPermissionSequenceList();
        if (sequenceList==null || user==null || user.isAnonymous() || !sequenceList.hasSequenceFor(user))
            return null;
        final PermissionSequence sequence = sequenceList.getSequenceFor(user);
        if (sequence==null)return null;
        return sequence.getLastStateChange();
    }
}
