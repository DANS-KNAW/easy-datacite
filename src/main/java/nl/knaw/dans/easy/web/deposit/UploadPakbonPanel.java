package nl.knaw.dans.easy.web.deposit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPException;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPakbonPanel;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;
import nl.knaw.dans.platform.language.pakbon.PakbonValidatorCredentials;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPakbonPanel extends AbstractCustomPanel
{

    private static final long serialVersionUID = -9132574510082841750L;
    private static Logger logger = LoggerFactory.getLogger(UploadPakbonPanel.class);

    private final DatasetModel datasetModel;
    
    public UploadPakbonPanel(String id, DatasetModel datasetModel)
    {
        super(id);
        this.datasetModel = datasetModel;
        setOutputMarkupId(true);        
   }

    @Override
    protected Panel getCustomComponentPanel()
    {
        DmoStoreId datasetId = datasetModel.getObject().getDmoStoreId();
        List<String> list = new ArrayList<String>();
        try
        {
            list = Services.getItemService().getFilenames(datasetId, true);
            if (list == null || list.isEmpty())
            {
                super.setVisible(false);
            }
        }
        catch (ServiceException e)
        {
            logger.error(e.getMessage());
        }

        if (list.size() > 0)
        {
            for (String s : list)
            {
//                if (s.endsWith("xml") && isValidPakbon(new File(s)))
                if (s.endsWith("xml"))
                {
            		return new ViewModePanel(s);
                }
            } 
            return new UploadModePanel();
        }
        else
        {
            return new UploadModePanel();
        }
    }

    class UploadModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public UploadModePanel()
        {
            super(CUSTOM_PANEL_ID);
            this.add(new DepositUploadPakbonPanel("uploadPanel", datasetModel));
            
            AjaxSubmitLink refreshPageLink = new AjaxSubmitLink("refreshPageLink")
            {

                private static final long serialVersionUID = 4529821897686007980L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    ((DepositPanel) form.getParent()).setInitiated(false);
                    target.addComponent(form.getParent());
                }

                @Override
                public boolean isVisible()
                {
                    return true;
                }

                @Override
                public boolean isEnabled()
                {
                    return true;
                }
            };
            add(refreshPageLink);
        }

    }

    class ViewModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public ViewModePanel(String fileName)
        {
            super(CUSTOM_PANEL_ID);
            Label label = new Label("noneditable", fileName);
            add(label);
        }
    }

    
//    /* Notice: We keep the option open here for more than one file.  At the moment it is not possible to upload more than one pakbon file. */
//    class ViewModePanel extends Panel
//    {
//
//        private static final long serialVersionUID = -1141097831590702485L;
//
//        public ViewModePanel(List<String> list)
//        {
//            super(CUSTOM_PANEL_ID);
//            String values = "";
//            for (String s : list)
//            {
//                if (s.endsWith("xml"))
//                {
//                    values += s + "\n";
//                    // just the first xml-file is shown
//                    break;
//                }
//            }
//            MultiLineLabel label = new MultiLineLabel("noneditable", values);
//            add(label);
//
//        }
//
//    }

    private boolean isValidPakbon(File xml)
    {
        final String DEFAULT_USERNAME = "bergh";
        final String DEFAULT_PASSWORD = "cC!XzlKK";

        PakbonValidator validator = new PakbonValidator();
        ValidateXmlResponse response;
		try {
	        new PakbonValidatorCredentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);
			response = validator.validateXml(xml);
			if (!response.getSuccess()){
				System.out.println("NOT VALID XML!");
			}
	        return response.getSuccess();
		} catch (ValidatorException e) {
			logger.error(e.getMessage());
		} catch (SOAPException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
    }

}
