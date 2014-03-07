package nl.knaw.dans.easy.web.permission;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyForm;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PermissionForm extends AbstractEasyForm<Dataset>
{

    private static final long serialVersionUID = 7555676473614004617L;
    protected static Logger logger = LoggerFactory.getLogger(PermissionForm.class);
    protected final AbstractEasyPage fromPage;

    public PermissionForm(String wicketId, final AbstractEasyPage fromPage, final DatasetModel datasetModel)
    {
        super(wicketId, datasetModel);
        this.fromPage = fromPage;
    }

    protected FormComponent addFormComponent(final FormComponent field, IModel model)
    {
        addWithComponentFeedback(field, model);
        return field;
    }

    protected Component addComponent(final Component field)
    {
        add(field);
        return field;
    }

    protected void pageBack() throws RestartResponseException
    {
        if (fromPage == null)
            throw new RestartResponseException(HomePage.class);
        // create a new instance of the page so that everything will be refreshed properly
        setResponsePage(new DatasetViewPage(fromPage.getPageParameters()));
        // this method doesn't work correctly:
        // fromPage.refresh();
        // setResponsePage(fromPage);
    }

    protected Dataset getDataset()
    {
        return getModel().getObject();
    }
}
