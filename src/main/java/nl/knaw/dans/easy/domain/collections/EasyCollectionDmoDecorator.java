package nl.knaw.dans.easy.domain.collections;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DmoDecorator;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.relations.Relation;

public class EasyCollectionDmoDecorator implements DmoDecorator
{

    private static final long serialVersionUID = -5701990291806961442L;

    public static final String NAMESPACE = "easy-collection";
    
    /**
     * The rootId for Easy (simple) collections. The last part 'esc' is used for OAI setSpec.
     * So this last part should be unique within the system!
     */
    public static final String ROOT_ID   = NAMESPACE + ":" + "esc";

    @Override
    public String getObjectNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public Set<String> getAdditionalContentModels()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MetadataUnit> getAdditionalMetadataUnits()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BinaryUnit> getAdditionalBinaryUnits()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Relation> getAdditionalRelations()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
