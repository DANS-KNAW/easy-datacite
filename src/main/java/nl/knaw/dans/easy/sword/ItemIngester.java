package nl.knaw.dans.easy.sword;

import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileOntology;
import nl.knaw.dans.easy.business.item.ItemIngester.DefaultDelegator;

public class ItemIngester extends DefaultDelegator
{
    
    private Dataset dataset;

    public ItemIngester(Dataset dataset)
    {
        super(dataset);
        this.dataset = dataset;
    }

    @Override
    public void addAdditionalRDF(final FileItem fileItem){
        if (fileItem!=null)return;
        if (fileItem==null)return;
        @SuppressWarnings("rawtypes")
        final AbstractRelations relations = (AbstractRelations) fileItem.getRelations();
        final FileOntology fo = new FileOntology();
        // relatie naar de dataset
        relations.addRelation(
                fo.isMetadataOn().get(), 
                RelsConstants.getObjectURI(dataset.getStoreId()));
        // type van de metadata
        relations.addRelation(
                fo.hasMetadataFormat().get(), 
                FileOntology.MetadataFormat.DDM.toString(), 
                RelsConstants.RDF_LITERAL);
    }

}
