package nl.knaw.dans.easy.sword;

import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.business.item.ItemIngester.DefaultDelegator;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileOntology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemIngester extends DefaultDelegator
{
    private static final String DDM = buildPattern(RequestContent.MDFileName.DansDatasetMetadata).toLowerCase();
    private static final String EMD = buildPattern(RequestContent.MDFileName.easyMetadata).toLowerCase();
    private static final String FO_DDM = FileOntology.MetadataFormat.DDM.toString();
    private static Logger logger = LoggerFactory.getLogger(ItemIngester.class);
    private final Dataset dataset;

    public ItemIngester(final Dataset dataset)
    {
        super(dataset);
        this.dataset = dataset;
        logger.debug("creating custom ItemIngestor for " + dataset.getStoreId());
    }

    @Override
    public void addAdditionalRDF(final FileItem fileItem)
    {
        logger.debug("checking if file is DDM/EDM: " + fileItem.getPath());
        final String lowerCasePath = fileItem.getPath().toLowerCase();
        if (lowerCasePath.matches(DDM) || lowerCasePath.matches(EMD))
        {
            logger.info("connecting metadata file " + fileItem.getStoreId() + " with dataset " + dataset.getStoreId());
            @SuppressWarnings("rawtypes")
            final AbstractRelations relations = (AbstractRelations) fileItem.getRelations();
            final FileOntology fo = new FileOntology();
            final String datasetURI = RelsConstants.getObjectURI(dataset.getStoreId());
            // relatie naar de dataset
            relations.addRelation(fo.isMetadataOn().get(), datasetURI);
            // type van de metadata
            relations.addRelation(fo.hasMetadataFormat().get(), FO_DDM, RelsConstants.RDF_LITERAL);
        }
    }

    private static String buildPattern(final RequestContent.MDFileName fileName)
    {
        return "(" + nl.knaw.dans.easy.business.item.ItemIngester.DEPOSITOR_FOLDER_NAME + "/" + ")?" + fileName.name() + ".xml";
    }
}
