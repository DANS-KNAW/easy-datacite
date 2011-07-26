package nl.knaw.dans.easy.web.fileexplorer2;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy.TriState;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem;
import nl.knaw.dans.common.wicket.components.explorer.ITreeItem.Type;
import nl.knaw.dans.common.wicket.components.explorer.content.SelectableFolderContent;
import nl.knaw.dans.common.wicket.components.explorer.style.ExplorerIcon;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wickettree.AbstractTree;


public class NamePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	
	public NamePanel(final String name, final IModel<ITreeItem> model, final AbstractTree<ITreeItem> tree, final DatasetModel datasetModel, final SelectableFolderContent content, final boolean hasAdditionalLicense, final AjaxLink<Void> fileActionLink) throws ServiceRuntimeException, ServiceException {
       super(name, model);
       
       AbstractLink link = null; 
       String style = "";
       
       TreeItem item = (TreeItem)model.getObject();
       AuthzStrategy strategy = item.getItemVO().getAuthzStrategy();
       
       if(item.getType().equals(Type.FOLDER)) {
    	   if(strategy.canChildrenBeRead().equals(TriState.ALL)) {
	    	   style = "AllAccessible";
	       } else if(strategy.canChildrenBeRead().equals(TriState.SOME)) {
	    	   style = "SomeAccessible";
	       } else {
	    	   style = "NoneAccessible";
	       }
    	   
	       link = new IndicatingAjaxLink<Void>("link") {
				private static final long serialVersionUID = 1L;
				@Override
		    	public void onClick(AjaxRequestTarget target) {
					tree.expand(content.getSelected().getObject());
		    		tree.expand(model.getObject());
		    		content.setSelectedAndUpdate(model, tree, target);
				}
	       };
       } else if(item.getType().equals(Type.FILE)) {
    	   boolean canUnitBeRead = strategy.canUnitBeRead(EasyFile.UNIT_ID);
    	   if(canUnitBeRead) {
    		   style = "AllAccessible";
    	   } else if(datasetModel.getObject().isUnderEmbargo()) {
    		   style = "EmbargoAccessible";
    	   } else {
    		   style = "NoneAccessible";
    	   }
           
    	   if(canUnitBeRead && !hasAdditionalLicense && EasySession.getSessionUser().hasAcceptedGeneralConditions()) {
    		   	// the user has already accepted general conditions and there are no additional conditions so create a direct download link
				final FileContentWrapper fcw = Services.getItemService().getContent(EasySession.getSessionUser(), datasetModel.getObject(), item.getId());
				link = new Link<Void>("link"){
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						// register this download action
						List<ItemVO> downloadList = new ArrayList<ItemVO>();
						downloadList.add(fcw.getFileItemVO());
						Services.getItemService().registerDownload(EasySession.getSessionUser(), datasetModel.getObject(), downloadList);
						
						new StreamDownloadPage(fcw);
					}
				};
				((Link) link).setPopupSettings(new PopupSettings(PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS));
    	   } else {
    		   link = fileActionLink;
    	   }
    	   // disable link if this file isn't accessible
    	   link.setEnabled(canUnitBeRead);
       }
   
       // determine what icon to display
	   Type type = item.getType();
	   String icon = "unknown.gif";
	   if(type.equals(Type.FOLDER)) {
		   icon = "folder.gif";
	   } else if(type.equals(Type.FILE)) {
		   icon = MimeTypes.get(item.getMimeType()) + ".gif";
	   }
       
       add(new Image("icon", new ExplorerIcon(icon)));
       link.add(new Label("name", model.getObject().getName()));
       if(!EasySession.getSessionUser().hasRole(Role.ARCHIVIST) && !datasetModel.getObject().hasDepositor(EasySession.getSessionUser())) {
    	   link.add(new AttributeAppender("class", new Model<String>(style), " "));
       }
       
       add(link);
	}
}
