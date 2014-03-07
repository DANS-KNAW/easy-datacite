package nl.knaw.dans.c.dmo.collections.core;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.config.Configuration;
import nl.knaw.dans.i.security.SecurityAgent;

public class MockCollectionsConfiguration implements Configuration
{
    
    public MockCollectionsConfiguration()
    {
        
    }

    @Override
    public List<DmoNamespace> getCollectionNamespaces()
    {
        List<DmoNamespace> namespaces = new ArrayList<DmoNamespace>();
        namespaces.add(new DmoNamespace("easy-test"));
        namespaces.add(new DmoNamespace("dans-test"));
        return namespaces;
    }

    @Override
    public DmoStoreId getOAISetContentModelId()
    {
        return new DmoStoreId("easy-model:oai-set1");
    }
    
    @Override
    public List<SecurityAgent> getSecurityAgents()
    {
        List<SecurityAgent> agents = new ArrayList<SecurityAgent>();
        return agents;
    }
    
}
