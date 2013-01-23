package nl.knaw.dans.easy.web.deposit;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdRights;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.IClusterable;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class LicensePanel extends AbstractCustomPanel
{
    //private static final Logger logger = LoggerFactory.getLogger(LicensePanel.class);

    private static final long serialVersionUID = -8835422851027831656L;

    public static final String EDITABLE_LICENSE_PANEL_TEMPLATE = "/pages/LicensePanel.template";
    public static final String EDITABLE_LICENSE_PANEL_OTHER_ACCESS_TEMPLATE = "/pages/LicensePanelOtherAccess.template";

    private final EasyMetadata easyMetadata;

    public LicensePanel(String id, IModel<EasyMetadata> model)
    {
        super(id, model);
        easyMetadata = model.getObject();
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        return new CustomPanel();
    }

    @Override
    public boolean takesErrorMessages()
    {
        return true;
    }

    class CustomPanel extends Panel
    {

        private static final long serialVersionUID = 5454733178997217331L;

        public CustomPanel()
        {
            super(CUSTOM_PANEL_ID);
            add(new EasyEditablePanel("editablePanel", getLicenceMessage(), easyMetadata.getEmdIdentifier()));
            add(new AcceptLicense("acceptLicense").setVisible(!AccessCategory.NO_ACCESS.equals(easyMetadata.getEmdRights().getAccessCategory())));
            //add(new ResourceLink<LicenseResource>("licenseLink", new LicenseResource()));
        }

        private String getLicenceMessage()
        {
            if (AccessCategory.NO_ACCESS.equals(easyMetadata.getEmdRights().getAccessCategory()))
            {
                return EDITABLE_LICENSE_PANEL_OTHER_ACCESS_TEMPLATE;
            }
            else
            {
                return EDITABLE_LICENSE_PANEL_TEMPLATE;
            }
        }
    }

    /*private class LicenseResource extends WebResource
    {
        private static final long serialVersionUID = 2114665554680463199L;
        
        @Override
        protected void setHeaders(WebResponse response)
        {
            super.setHeaders(response);
            response.setAttachmentHeader(easyMetadata.getPreferredTitle()+"-" + LicenseUnit.UNIT_LABEL);
            response.setContentType(LicenseUnit.MIME_TYPE);
        }

        @Override
        public IResourceStream getResourceStream()
        {
            setCacheable(false);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final String storeId = easyMetadata.getEmdIdentifier().getDatasetId();
            try
            {
            	final Dataset dataset = Services.getDatasetService().getDataset(storeId);
            	new LicenseComposer(getSessionUser(), dataset, true).createPdf(outputStream);
            }
            catch (final LicenseComposerException exception)
            {
            	final String message = errorMessage(EasyResources.LICENSE_COMPOSER);
                logger.error(message + " " + storeId, exception);
            }
            catch (final ServiceException exception)
            {
                final String message = errorMessage(EasyResources.LICENSE_DATASET);
                logger.error(message + " " + storeId, exception);
            }
    //            catch (MalformedURLException exception)
    //            {
    //                final String message = errorMessage(EasyResources.LICENSE_COMPOSER);
    //                logger.error(message + " " + storeId, exception);
    //            }
            return new AbstractResourceStream()
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void close() throws IOException
                {
                }

                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException
                {
                    if (outputStream.size() == 0)
                        return null;
                    return new ByteArrayInputStream(outputStream.toByteArray());
                }
            };
        }
    }*/

    class AcceptLicense extends CheckBox
    {
        private static final long serialVersionUID = 4738590568299900216L;

        public AcceptLicense(String id)
        {
            super(id);
            setDefaultModel(new PropertyModel<Input>(new Input(), "accept"));
        }
    }

    private class Input implements IClusterable
    {
        private static final long serialVersionUID = -667029584818294155L;
        private List<BasicString> termsLicense = easyMetadata.getEmdRights().getTermsLicense();

        @SuppressWarnings("unused")
        public Boolean getAccept()
        {
            return new Boolean(!termsLicense.isEmpty());
        }

        @SuppressWarnings("unused")
        public void setAccept(Boolean accept)
        {
            termsLicense.clear();
            if (accept)
            {
                termsLicense.add(new BasicString(EmdRights.LICENSE_ACCEPT));
            }
        }
    }
}
