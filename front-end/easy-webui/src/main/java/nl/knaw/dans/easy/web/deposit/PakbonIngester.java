package nl.knaw.dans.easy.web.deposit;

import java.io.File;

import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.business.item.ItemIngester.DefaultDelegator;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileOntology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this to ingest a Pakbon file Issue EASY-710
 * 
 * @author paulboon
 */
public class PakbonIngester extends DefaultDelegator {
    private static final String FO_PAKBON = FileOntology.MetadataFormat.PAKBON.toString();
    private static Logger logger = LoggerFactory.getLogger(PakbonIngester.class);
    private final Dataset dataset;
    private final File pakbon;

    public PakbonIngester(final Dataset dataset, File pakbon) {
        super(dataset);
        this.dataset = dataset;
        this.pakbon = pakbon;
        logger.debug("Creating custom ItemIngester for dataset: {} and pakbon file: {}", dataset.getStoreId(), pakbon.getPath());
    }

    @Override
    public void addAdditionalRDF(final FileItem fileItem) {
        logger.debug("Check if file is Pakbon: {} , file: {}", pakbon.getPath(), fileItem.getFile().getPath());
        if (pakbon.getPath().contentEquals(fileItem.getFile().getPath())) {
            @SuppressWarnings("rawtypes")
            final AbstractRelations relations = (AbstractRelations) fileItem.getRelations();
            final FileOntology fo = new FileOntology();
            final String datasetURI = RelsConstants.getObjectURI(dataset.getStoreId());
            logger.info("Mark file as Pakbon metadata: {}", pakbon.getPath());
            relations.addRelation(fo.hasMetadataFormat().get(), FO_PAKBON, RelsConstants.RDF_LITERAL);
            logger.info("Connecting Pakbon metadata file {} with dataset {}", fileItem.getStoreId(), dataset.getStoreId());
            relations.addRelation(fo.isMetadataOn().get(), datasetURI);
        }
    }
}
