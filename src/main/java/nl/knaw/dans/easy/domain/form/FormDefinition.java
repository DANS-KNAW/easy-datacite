package nl.knaw.dans.easy.domain.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormDefinition extends AbstractInheritableDefinition<FormDefinition>
{   
    
    private static final long serialVersionUID = -8118471606287942806L;
    
    private List<FormPage> formPages = Collections.synchronizedList(new ArrayList<FormPage>());
    
    protected FormDefinition()
    {
        super();
    }
    
    public FormDefinition(String formDefinitionId)
    {
        super(formDefinitionId);
    }

    public List<FormPage> getFormPages()
    {
        return formPages;
    }

    public void addFormPage(FormPage formPage)
    {
        formPage.setParent(this);
        formPages.add(formPage);
    }
    
    public FormPage getFormPage(String formPageId)
    {
        FormPage fp = null;
        for (FormPage formPage : formPages)
        {
            if (formPage.getId().equals(formPageId))
            {
                fp = formPage;
                break;
            }
        }
        return fp;
    }
    
    protected FormDefinition clone()
    {
        FormDefinition clone = new FormDefinition(getId());
        super.clone(clone);
        synchronized (formPages)
        {
            for (FormPage formPage : formPages)
            {
                clone.addFormPage(formPage.clone());
            }
        }
        return clone;
    }

}
