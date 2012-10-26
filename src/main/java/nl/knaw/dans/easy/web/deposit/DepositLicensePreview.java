package nl.knaw.dans.easy.web.deposit;

import java.text.MessageFormat;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.LicenseUnit;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Creates a preview of the depositors license for the owner of a draft dataset. Example of a URL:
 * 
 * <pre>
 * http://localhost:8081/resources/easy/deposit_license_preview.pdf?id=easy-dataset:999
 * </pre>
 * 
 * For an up to date URL see {@link EasyWicketApplication#WICKET_APPLICATION_ALIAS}/{@link #RESOURCE_NAME} 
 */
public class DepositLicensePreview extends DynamicWebResource
{

    private static final Logger logger = LoggerFactory.getLogger(DepositLicensePreview.class);

    private static final long serialVersionUID = 2114665554680463199L;

    public static final String RESOURCE_NAME = "deposit_license_preview.pdf";

    @Override
    protected ResourceState getResourceState()
    {
        logger.debug(RequestCycle.get().getRequest().getURL());
        return new DynamicWebResource.ResourceState()
        {
            @Override
            public byte[] getData()
            {
                setCacheable(false);
                DmoStoreId dmoStoreId = new DmoStoreId(getParameters().getString("id"));
                return createContent(dmoStoreId);
            }

            @Override
            public String getContentType()
            {
                return LicenseUnit.MIME_TYPE;
            }

        };
    }

    private static byte[] createContent(final DmoStoreId storeId)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (storeId != null && !storeId.equals(""))
            createLicenseContent(outputStream, storeId);
        if (outputStream.size() == 0)
            createErrorContent(storeId, outputStream);
        return outputStream.toByteArray();
    }

    private static void createLicenseContent(final ByteArrayOutputStream outputStream, final DmoStoreId storeId)
    {
        final EasySession easySession = (EasySession) Session.get();
        final EasyUser user = easySession.getUser();
        final Dataset dataset = getDataset(storeId);
        if (!dataset.hasDepositor(user))
            return;
        if (!dataset.getAdministrativeState().equals(DatasetState.DRAFT))
            return;
        try
        {
            new LicenseComposer(user, dataset, true).createPdf(outputStream);
        }
        catch (final LicenseComposerException exception)
        {
            logger.error(MessageFormat.format("could not create license for dataset {0} of user {0}", user.getId(), storeId), exception);
        }
    }

    private static void createErrorContent(final DmoStoreId storeId, final ByteArrayOutputStream outputStream)
    {
        final Document document = new Document();
        try
        {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            try
            {
                document.add(new Paragraph("You requested a preview of the depositors license document for " + storeId));
                document.add(new Paragraph("You might not be the owner of the dataset or you have submitted it."));
            }
            finally
            {
                document.close();
            }
        }
        catch (final DocumentException exception)
        {
            logger.error("could not create error content", exception);
        }
    }

    private static Dataset getDataset(final DmoStoreId storeId)
    {
        try
        {
            return Services.getDatasetService().getDataset(EasySession.get().getUser(), storeId);
        }
        catch (final ServiceException exception)
        {
            logger.error(MessageFormat.format("could not get dataset {0}", storeId), exception);
        }
        // ????????????????????????? HB
        return new DatasetImpl(storeId.getStoreId());
    }
}
