package nl.knaw.dans.easy.web.wicket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class AssignToDropChoiceList
{

	public static final String NOT_ASSIGNED_RESOURCEKEY = "assignTo.not_assigned";

	/**
	 * I started out using inheritance, but got stuck and after an hour gave up so wrapped the damn
	 * thing. To hell with wicket!
	 */
	public static DropDownChoice getDropDownChoice(String wicketId, IModel model) throws ServiceException
	{
		return new DropDownChoice(wicketId, model, getChoices(), getRenderer());
	}
	
	public static IChoiceRenderer getRenderer()
	{
		return new ChoiceRenderer("value", "key");
	}
	
	@SuppressWarnings("unchecked")
	public static List getChoices() throws ServiceException
	{
        List<KeyValuePair> assingToList = new ArrayList<KeyValuePair>();
        assingToList.add(
        		new KeyValuePair(WorkflowData.NOT_ASSIGNED, 
        				(String) new ResourceModel(NOT_ASSIGNED_RESOURCEKEY).getObject()
        			)
        	);
        List<EasyUser> archivists = Services.getUserService().getUsersByRole(Role.ARCHIVIST);
        
        // sort list of archivists on surname
        Collections.sort(archivists, new Comparator<EasyUser>() {
			@Override
			public int compare(EasyUser o1, EasyUser o2) {
				return o1.getSurname().compareTo(o2.getSurname());
			}        
        });
        
        for (EasyUser archivist : archivists)
        {
            assingToList.add(new KeyValuePair(archivist.getId(), archivist.getDisplayName()));
        }
        return assingToList;
	}
}
