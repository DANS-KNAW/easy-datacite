package nl.knaw.dans.common.lang.dataset;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;

public class CommonDataset extends AbstractDataModelObject {
    private static final long serialVersionUID = -15791555597984291L;
    private static final DmoNamespace NAMESPACE = new DmoNamespace("");
    private DublinCoreMetadata dc;

    public CommonDataset(String storeId) {
        super(storeId);
    }

    @Override
    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> cm = super.getContentModels();
        cm.add("fedora-system:common-dataset");
        return cm;
    }

    @Override
    public List<MetadataUnit> getMetadataUnits() {
        List<MetadataUnit> mdUnits = super.getMetadataUnits();
        mdUnits.add(getDublinCoreMetadata());
        return mdUnits;
    }

    public DublinCoreMetadata getDublinCoreMetadata() {
        return this.dc;
    }

    public void setDublinCoreMetadata(DublinCoreMetadata dc) {
        this.dc = dc;
    }

}
