package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.easy.web.ResourceBookmark;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

public class DownloadPanel extends Panel {

    private static final String DOWNLOAD_XML = "download_xml";

    private static final String DOWNLOAD_CSV = "download_csv";

    private static final long serialVersionUID = 9110250938647271835L;

    private boolean initiated;
    private final String dataset_id;

    public DownloadPanel(final String id, final String dataset_id) {
        super(id);
        this.dataset_id = dataset_id;
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        ResourceReference ref = new ResourceReference(ResourceBookmark.emdExport.getAlias());
        String exportXml = String.format("%s?%s=%s&%s=%s", RequestCycle.get().urlFor(ref), MetadataExportResource.DATASET_ID_PARAM, dataset_id,
                MetadataExportResource.EXPORT_FORMAT_PARAM, MetadataExportResource.ExportFormat.XML.name());
        String exportCsv = String.format("%s?%s=%s&%s=%s", RequestCycle.get().urlFor(ref), MetadataExportResource.DATASET_ID_PARAM, dataset_id,
                MetadataExportResource.EXPORT_FORMAT_PARAM, MetadataExportResource.ExportFormat.CSV.name());
        add(new ExternalLink(DOWNLOAD_XML, exportXml));
        add(new ExternalLink(DOWNLOAD_CSV, exportCsv));

    }
}
