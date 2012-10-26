package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;
import java.util.Locale;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;

import org.apache.wicket.model.IModel;

public interface IModelFactory extends Serializable
{

    IModel createModel(StandardPanelDefinition panelDefinition) throws ModelFactoryException;

    ChoiceList getChoiceList(String listId, Locale locale) throws ServiceException;
}
