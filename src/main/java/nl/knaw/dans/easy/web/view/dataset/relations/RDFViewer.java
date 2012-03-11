package nl.knaw.dans.easy.web.view.dataset.relations;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.wicket.model.DMOModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class RDFViewer extends Panel
{

    private static final long serialVersionUID = 4619176404348033432L;
    
    private final MultiLineLabel rdfField;
    
    public RDFViewer(String id, DMOModel<?> model)
    {
        super(id, model);
        setOutputMarkupId(true);
        
        AjaxLink<String> toggleRDF = new AjaxLink<String>("toggleRDF")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                rdfField.setVisible(!rdfField.isVisible());
                target.addComponent(RDFViewer.this.getParent());
                
            }
            
        };
        toggleRDF.add(new Label("toggleLabel", new Model<String>()
        {

            private static final long serialVersionUID = 1L;
            
            @Override
            public String getObject()
            {
                return rdfField.isVisible() ? "hide RDF" : "view RDF";
            }
            
        }));
        
        add(toggleRDF);
        
        rdfField = new MultiLineLabel("rdfField", new Model<String>()
        {

            private static final long serialVersionUID = 1L;
            
            @Override
            public String getObject()
            {
                DataModelObject dmo = (DataModelObject) RDFViewer.this.getDefaultModelObject();
                Relations relations = dmo.getRelations();
                try
                {
                    return relations == null ? null : relations.getRdf();
                }
                catch (ObjectSerializationException e)
                {
                    return e.getMessage();
                }
            }
    
        });
        rdfField.setEscapeModelStrings(true);
        rdfField.setOutputMarkupId(true);
        rdfField.setVisible(false);
        add(rdfField);
    }

}
