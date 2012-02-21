package nl.knaw.dans.easy.fedora.store;

import java.net.MalformedURLException;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStores;
import nl.knaw.dans.common.lang.search.SearchEngine;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.solr.SolrSearchEngine;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.search.EasySearchBeanFactory;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.db.DbLocalConfig;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;

import org.joda.time.DateTime;

public abstract class AbstractOnlineTest
{
    
    private static final String BASE_URL   = "fedora.base.url";
    private static final String ADMIN_NAME = "fedora.admin.username";
    private static final String ADMIN_PASS = "fedora.admin.userpass";
    
    private static Fedora fedora;
	private static SolrSearchEngine se;
    
    private static final String USER_NAME       = "fedora.db.username";
    private static final String PASSWORD        = "fedora.db.password";
    private static final String CONNECTION_URL  = "fedora.db.connectionUrl";
    private static final String HBN_DRIVERCLASS = "fedora.db.hbnDriverClass";
    private static final String HBN_DIALECT     = "fedora.db.hbnDialect";
    
    private static DbLocalConfig dbLocalConfig;
    
    public static void setUpData() throws MalformedURLException
    {
        DmoStores.skipThisRubbish = true;
        Data data = new Data();
        EasyStore easyStore = new EasyFedoraStore("easy", getFedora(), getSearchEngine());
        
        data.setEasyStore(easyStore);
    }
    
    public static Fedora getFedora()
    {
        if (fedora == null)
        {
            fedora = new Fedora(Tester.getString(BASE_URL), Tester.getString(ADMIN_NAME), Tester
                    .getString(ADMIN_PASS));
        }
        return fedora;
    }
    
    public static DbLocalConfig getDbLocalConfig()
    {
        if (dbLocalConfig == null)
        {
            dbLocalConfig = new DbLocalConfig(Tester.getString(USER_NAME), 
                    Tester.getString(PASSWORD),
                    Tester.getString(CONNECTION_URL),
                    Tester.getString(HBN_DRIVERCLASS),
                    Tester.getString(HBN_DIALECT));
        }
        return dbLocalConfig;
    }

    public static SearchEngine getSearchEngine() throws MalformedURLException
    {
    	if (se == null)
    	{
    		se = new SolrSearchEngine(Tester.getString("solr.url"), new EasySearchBeanFactory());
    	}
    	return se;
    }
    
    public static Dataset getDummyDataset(String nextSid)
    {
	    Dataset dataset = new DatasetImpl(nextSid, MetadataFormat.UNSPECIFIED);
	
	    // fgs.state, fgs.label, fgs.ownerId, fgs.createdDate, fgs.lastModifiedDate are indirectly set.
	
	    EasyMetadata emd = dataset.getEasyMetadata();
	    emd.getEmdTitle().getDcTitle().add(new BasicString("title of full ingest.")); // sf.title
	    emd.getEmdCreator().getDcCreator().add(new BasicString("creator of ingest.")); // sf.creator
	    emd.getEmdDate().getEasCreated().add(new IsoDate(new DateTime())); // sf.dateCreated
	    emd.getEmdDate().getEasDateSubmitted().add(new IsoDate(new DateTime())); // sf.dateSubmitted
	    BasicString audience = new BasicString("easy-discipline:11");
	    audience.setSchemeId("custom.disciplines");
	    emd.getEmdAudience().getTermsAudience().add(audience); // sf.audience
	    emd.getEmdRights().getTermsAccessRights().add(new BasicString(AccessCategory.OPEN_ACCESS.name())); // sf.accessrights
	
	    AdministrativeMetadata amd = dataset.getAdministrativeMetadata();
	    amd.setAdministrativeState(DatasetState.MAINTENANCE); // sf.datasetState
	    amd.setDepositorId("admin"); // sf.depositorId
	    // sf.assigneeId automatically set
	    
	    return dataset;
    }
    
}
