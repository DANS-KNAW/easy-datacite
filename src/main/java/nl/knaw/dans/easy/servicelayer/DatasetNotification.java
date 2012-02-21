package nl.knaw.dans.easy.servicelayer;

import java.io.IOException;
import java.net.URL;

import nl.knaw.dans.common.lang.mail.Attachement;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.util.StreamUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.LicenseUnit;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

/**
 * Notification about a dataset sent without a license.
 */
public abstract class DatasetNotification extends AbstractNotification
{

    private static DatasetUrlComposer urlComposer;

    public static void setDatasetUrlComposer(final DatasetUrlComposer urlComposer){
        DatasetNotification.urlComposer = urlComposer;
    }
    
    public String getDatasetUrl()
    {
        if (urlComposer == null)
            throw new IllegalStateException("no urlComposer available");
        return urlComposer.getUrl(dataset.getStoreId());
    }

    public String getPermissionUrl()
    {
        if (urlComposer == null)
            throw new IllegalStateException("no urlComposer available");
        return urlComposer.getPermissionUrl(dataset.getStoreId());
    }

    public String getFileExplorerUrl()
    {
        if (urlComposer == null)
            throw new IllegalStateException("no urlComposer available");
        return urlComposer.getFileExplorerUrl(dataset.getStoreId());
    }

    public String getMyDatasetsUrl()
    {
        if (urlComposer == null)
            throw new IllegalStateException("no urlComposer available");
        return urlComposer.getMyDatasetsUrl(dataset.getStoreId());
    }

    
    private final Dataset  dataset;
    
    /**
     * Lazy initialization to recognize a license attached for a previous send not wanted for the new send.
     */
    private Attachement    license = null;

    /**
     * @param dataset
     *        A placeHolderSupplier.<br>
     *        Wrappers for Dataset.getXx().getYy() should be defined here.
     * @param receiver
     *        A placeHolderSupplier and the receiver of the message.<br>
     *        Wrappers for receiver.getXx().getYy() should be defined in {@link AbstractNotification}.<br>
     *        Wrappers for any other EasyUser should be defined in subclasses.
     *        Wrappers for Dataset.getDepositor().getXx() should be defined here.<br>
     * @param placeHolderSuppliers
     *        The types should be unique and not be an EasyUser or Dataset.<br>
     *        Wrappers for any PlaceHolderSupplier.getXx().getYy() should be defined in subclasses.
     */
    public DatasetNotification(final Dataset dataset, final EasyUser receiver, final Object... placeHolderSuppliers)
    {
        super(receiver, concat(dataset, placeHolderSuppliers));
        this.dataset = dataset;
    }

    /** Convenience constructor. dataset.getDepositor() becomes the receiver. */
    public DatasetNotification(final Dataset dataset, final Object... placeHolderSuppliers)
    {
        this(dataset, dataset.getDepositor(), placeHolderSuppliers);
    }

    Dataset getDataset()
    {
        return dataset;
    }

    /**
     * Changes in the dataset are not reflected in the attached license document if sending with a license for another time on the same instance.
     * 
     * @param withLicense
     *        Defaults to false if omitted.
     * @throws ServiceException
     */
    public void send(final boolean withLicense) throws ServiceException
    {
        try
        {
            setLicense(withLicense);
        }
        catch (final IOException e)
        {
            throw new ServiceException("could not create license for " + getDataset().getStoreId());
        }
        send();
    }

    /**
     * Changes in the dataset are not reflected in the attached license document if sending with a license for another time on the same instance.
     * 
     * @param withLicense
     *        Defaults to false if omitted.
     * @return False in case of a ServiceException, the exception is logged.
     */
    public boolean sendMail(final boolean withLicense)
    {
        try
        {
            setLicense(withLicense);
        }
        catch (final IOException e)
        {
            final String format = "could not create license attachement of [%s] for [%s]";
            final String message = String.format(format, getDataset().getPreferredTitle(), getReceiverEmail());
            logger.error(message, e);
            return false;
        }
        return sendMail();
    }

    private void setLicense(final boolean withLicense) throws IOException
    {
        if (!withLicense)
        {
            // just in case the previous send was with a license
            if (license != null && getAttachements().contains(license))
            {
                getAttachements().remove(license);
            }
            return;
        }
        if (license != null)
        {
            // just in case the previous send was also with a license
            if (getAttachements().contains(license))
                return;
        }
        else
        {
            final URL url = Data.getEasyStore().getFileURL(dataset.getDmoStoreId(), new DsUnitId(LicenseUnit.UNIT_ID));
            final byte[] bytes = StreamUtil.getBytes(url.openStream());
            license = new Attachement(LicenseUnit.UNIT_LABEL, LicenseUnit.MIME_TYPE, bytes);
        }
        getAttachements().add(license);
    }
}
