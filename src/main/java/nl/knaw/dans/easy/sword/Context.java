package nl.knaw.dans.easy.sword;

import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class Context
{
    private static final Logger log = LoggerFactory.getLogger(Context.class);

    /** Directory were deposits are unzipped. */
    private static String unzip = null;

    private static String workspaceTitle = null;
    private static String collectionTreatment = null;
    private static String collectionPolicy = null;
    private static String collectionTitle = null;
    private static String collectionAbstract = null;
    private static String depositTreatment = null;
    private static String datasetPath = null;
    private static String providerURL = null;
    private static String servletName = null;
    private static String easyHome = null;

    public void setUnzip(String unzip)
    {
        log.debug("Setting unzip directory to {}", unzip);
        Context.unzip = unzip;
    }

    public static String getUnzip() throws SWORDException
    {
        if (unzip == null)
        {
            error("Missing configuration setting: unzip directory");
        }

        return unzip;
    }

    public void setWorkspaceTitle(String workspaceTitle)
    {
        Context.workspaceTitle = workspaceTitle;
    }

    public static String getWorkspaceTitle() throws SWORDException
    {
        if (workspaceTitle == null)
            error("missing configuration: workspaceTitle");
        return workspaceTitle;
    }

    public void setCollectionTreatment(String collectionTreatment)
    {
        Context.collectionTreatment = collectionTreatment;
    }

    public static String getCollectionTreatment() throws SWORDException
    {
        if (collectionTreatment == null)
            error("missing configuration: collectionTreatment");
        return collectionTreatment;
    }

    public void setCollectionPolicy(String collectionPolicy)
    {
        Context.collectionPolicy = collectionPolicy;
    }

    public static String getCollectionPolicy() throws SWORDException
    {
        if (collectionPolicy == null)
            error("missing configuration: collectionPolicy");
        return collectionPolicy;
    }

    public void setCollectionTitle(String collectionTitle)
    {
        Context.collectionTitle = collectionTitle;
    }

    public static String getCollectionTitle() throws SWORDException
    {
        if (collectionTitle == null)
            error("missing configuration: collectionTitle");
        return collectionTitle;
    }

    public void setCollectionAbstract(String collectionAbstract)
    {
        Context.collectionAbstract = collectionAbstract;
    }

    public static String getCollectionAbstract() throws SWORDException
    {
        if (collectionAbstract == null)
            error("missing configuration: collectionAbstract");
        return collectionAbstract;
    }

    public void setdepositTreatment(String depositTreatment)
    {
        Context.depositTreatment = depositTreatment;
    }

    public static String getDepositTreatment() throws SWORDException
    {
        if (depositTreatment == null)
            error("missing configuration: depositTreatment");
        return depositTreatment;
    }

    public void setDatasetPath(String datasetPath)
    {
        Context.datasetPath = datasetPath;
    }

    public static String getDatasetPath() throws SWORDException
    {
        if (datasetPath == null)
            error("missing configuration: datasetPath");
        return datasetPath;
    }

    public void setProviderURL(String providerURL)
    {
        Context.providerURL = providerURL;
    }

    public static String getProviderURL() throws SWORDException
    {
        if (providerURL == null)
            error("missing configuration: providerURL");
        return providerURL;
    }

    public void setServletName(String servletName)
    {
        Context.servletName = servletName;
    }

    public static String getServletName() throws SWORDException
    {
        if (servletName == null)
            error("missing configuration: servletName");
        return servletName;
    }

    public void setEasyHome(String easyHome)
    {
        Context.easyHome = easyHome;
    }

    public static String getEasyHome() throws SWORDException
    {
        if (easyHome == null)
            error("missing configuration: easyHome");
        return easyHome;
    }

    private static void error(String msg, Object... args) throws SWORDException
    {
        msg = MessageFormatter.format(msg, args).toString();
        log.error(msg);
        throw new SWORDException(msg);
    }
}
