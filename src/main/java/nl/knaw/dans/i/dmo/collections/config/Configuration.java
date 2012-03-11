package nl.knaw.dans.i.dmo.collections.config;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.security.SecurityAgent;

public interface Configuration
{

    /**
     * Get the list of DmoNamespaces that will be managed by the {@link CollectionManager}. These are the
     * DmoNamespaces used by the various collections. F.i. 'easy-collection', easy-discipline' etc.
     * <p/>
     * A {@link DmoCollection} who's DmoNamespace is not in the list <b>cannot be managed</b>.
     * 
     * @return list of DmoNamespaces
     */
    List<DmoNamespace> getCollectionNamespaces(); 

    /**
     * Get the identifier for the content model of OAI sets in use. F.i. 'easy-model:oai-set1'.
     * 
     * @return identifier for the content model of OAI sets
     */
    DmoStoreId getOAISetContentModelId();

    /**
     * Get a list of SecurityAgents, relevant to operations by {@link CollectionManager}.
     * 
     * @return list of SecurityAgents
     */
    List<SecurityAgent> getSecurityAgents();

}
