package nl.knaw.dans.easy.sword;

import org.purl.sword.base.SWORDException;

public class Context
{
    /** Directory were deposits are unzipped. */
    private static String unzip = null;

    private static String workspaceTitle = null;
    private static String collectionTreatment = null;
    private static String collectionPolicy = null;
    private static String collectionTitle = null;
    private static String collectionAbstract = null;
    private static String depositTreatment = null;
    private static String datasetPath = null;

    public void setUnzip(String unzip)
    {
        Context.unzip = unzip;
    }

    public static String getUnzip() throws SWORDException
    {
        if (unzip == null)
            throw new SWORDException("missing configuration: unzip");
        return unzip;
    }

    public void setWorkspaceTitle(String workspaceTitle)
    {
        Context.workspaceTitle = workspaceTitle;
    }

    public static String getWorkspaceTitle() throws SWORDException
    {
        if (workspaceTitle == null)
            throw new SWORDException("missing configuration: workspaceTitle");
        return workspaceTitle;
    }

    public void setCollectionTreatment(String collectionTreatment)
    {
        Context.collectionTreatment = collectionTreatment;
    }

    public static String getCollectionTreatment() throws SWORDException
    {
        if (collectionTreatment == null)
            throw new SWORDException("missing configuration: collectionTreatment");
        return collectionTreatment;
    }

    public void setCollectionPolicy(String collectionPolicy)
    {
        Context.collectionPolicy = collectionPolicy;
    }

    public static String getCollectionPolicy() throws SWORDException
    {
        if (collectionPolicy == null)
            throw new SWORDException("missing configuration: collectionPolicy");
        return collectionPolicy;
    }

    public void setCollectionTitle(String collectionTitle)
    {
        Context.collectionTitle = collectionTitle;
    }

    public static String getCollectionTitle() throws SWORDException
    {
        if (collectionTitle == null)
            throw new SWORDException("missing configuration: collectionTitle");
        return collectionTitle;
    }

    public void setCollectionAbstract(String collectionAbstract)
    {
        Context.collectionAbstract = collectionAbstract;
    }

    public static String getCollectionAbstract() throws SWORDException
    {
        if (collectionAbstract == null)
            throw new SWORDException("missing configuration: collectionAbstract");
        return collectionAbstract;
    }

    public void setdepositTreatment(String depositTreatment)
    {
        Context.depositTreatment = depositTreatment;
    }

    public static String getDepositTreatment() throws SWORDException
    {
        if (depositTreatment == null)
            throw new SWORDException("missing configuration: depositTreatment");
        return depositTreatment;
    }

    public void setDatasetPath(String datasetPath)
    {
        Context.datasetPath = datasetPath;
    }

    public static String getDatasetPath() throws SWORDException
    {
        if (datasetPath == null)
            throw new SWORDException("missing configuration: datasetPath");
        return datasetPath;
    }

}
