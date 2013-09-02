package nl.knaw.dans.easy.web;

import nl.knaw.dans.common.lang.ALiasDelegate;
import nl.knaw.dans.common.lang.ALiasDelegate.AliasInterface;
import nl.knaw.dans.common.wicket.components.upload.command.EasyUploadCancelCommand;
import nl.knaw.dans.common.wicket.components.upload.command.EasyUploadStatusCommand;
import nl.knaw.dans.easy.web.deposit.ChoiceListExport;
import nl.knaw.dans.easy.web.deposit.DepositLicensePreview;
import nl.knaw.dans.easy.web.download.FileDownloadResource;

import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;

/**
 * The instances are in fact entries of a cross reference between {@link Resource} classes and URLs. A
 * static initializer guarantees a 1:1 relationship. The resources are automatically registered by
 * {@link EasyWicketApplication}.
 */
public enum ResourceBookmark implements AliasInterface<Resource>
{
    uploadStatus(EasyUploadStatusCommand.class, EasyUploadStatusCommand.RESOURCE_NAME), //
    uploadCancel(EasyUploadCancelCommand.class, EasyUploadCancelCommand.RESOURCE_NAME), //
    download(FileDownloadResource.class, "fileDownloadResource"), //
    emdDisciplines(ChoiceListExport.class, ChoiceListExport.RESOURCE_NAME), //
    contentExport(DmoContentExport.class, "content"), //
    depositLicense(DepositLicensePreview.class, DepositLicensePreview.RESOURCE_NAME); //

    private static ALiasDelegate<Resource> delegate = new ALiasDelegate<Resource>(ResourceBookmark.values());
    private final String bookmarkedName;
    private final Class<? extends Resource> bookmarkedClass;

    private ResourceBookmark(final Class<? extends Resource> bookmarkedClass, final String bookmarkedName)
    {
        this.bookmarkedName = bookmarkedName;
        this.bookmarkedClass = bookmarkedClass;
    }

    public static ResourceBookmark valueOf(final Class<? extends Resource> bookmarkedClass)
    {
        return (ResourceBookmark) delegate.valueOf(bookmarkedClass);
    }

    public static ResourceBookmark valueOfAlias(final String alias)
    {
        return (ResourceBookmark) delegate.valueOfAlias(alias);
    }

    public final static ResourceReference getResourceReferenceOf(final Class<? extends Resource> bookmarkedClass)
    {
        return new ResourceReference(delegate.valueOf(bookmarkedClass).getAlias());
    }

    @Override
    public Class<? extends Resource> getAliasClass()
    {
        return bookmarkedClass;
    }

    @Override
    public String getAlias()
    {
        return bookmarkedName;
    }
}
