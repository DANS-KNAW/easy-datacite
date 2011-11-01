package nl.knaw.dans.easy.web.fileexplorer2;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy.TriState;
import nl.knaw.dans.common.lang.service.exceptions.FileSizeException;
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
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.DisciplineStatistics;
import nl.knaw.dans.easy.web.statistics.DownloadStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wickettree.AbstractTree;


public class NamePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(NamePanel.class);
	
	private static final String MSG_FILE_SIZE_TOLARGE = "download.fileSizeToLarge";
	
	public NamePanel(final String name, final IModel<ITreeItem> model, final AbstractTree<ITreeItem> tree, final DatasetModel datasetModel, final SelectableFolderContent content, final boolean hasAdditionalLicense, final AjaxLink<Void> fileActionLink) throws ServiceRuntimeException, ServiceException {
       super(name, model);
       
       final ModalWindow popup = Util.createModalWindow("popup", 450, "Maximum download size exceeded");
       add(popup);
       
       AbstractLink link = null; 
       String style = "";
       
       final TreeItem item = (TreeItem)model.getObject();
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
				link = new IndicatingAjaxLink<Void>("link"){
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						final Model<FileContentWrapper> fcwModel = new Model<FileContentWrapper>(null);
						FileSizeException exception = new FileSizeException("");
						
						try {
							fcwModel.setObject(Services.getItemService().getContent(EasySession.getSessionUser(), datasetModel.getObject(), item.getId()));
						} catch (FileSizeException e) {
							exception = e;
							logger.info("File size too large for download: " + item.getName() + "(" + item.getId() + ")");
						} catch (ServiceRuntimeException e) {
							logger.error("Encountered problem while preparing FileContentWrapper for direct download link.");
						} catch (ServiceException e) {
							logger.error("Encountered problem while preparing FileContentWrapper for direct download link.");
						}
						
						if(fcwModel.getObject() != null) {
							// register this download action
							List<ItemVO> downloadList = new ArrayList<ItemVO>();
							final FileContentWrapper fcw = fcwModel.getObject();
							downloadList.add(fcw.getFileItemVO());
							Services.getItemService().registerDownload(EasySession.getSessionUser(), datasetModel.getObject(), downloadList);
							
							final AJAXDownload download = new AJAXDownload()
                            {
                                private static final long serialVersionUID = 1L;

                                @Override
                                protected IResourceStream getResourceStream()
                                {
                                    return new UrlResourceStream(fcw.getURL());
                                }

                                @Override
                                protected String getFileName()
                                {
                                    return fcw.getFileName();
                                }
                            };
                            add(download);
                            download.initiate(target);
                            StatisticsLogger.getInstance().logEvent(StatisticsEvent.DOWNLOAD_FILE_REQUEST, new DatasetStatistics(datasetModel.getObject()),
                                    new DownloadStatistics(fcw), new DisciplineStatistics(datasetModel.getObject()));
						} else {
							// download can't be handled so show a message
							popup.setContent(new ModalPopup(popup, new StringResourceModel(MSG_FILE_SIZE_TOLARGE, this, new Model<FileSizeException>(exception)).getObject()));
							popup.show(target);
						}
					}
				};
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
