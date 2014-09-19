package nl.knaw.dans.easy.web.view.dataset.relations;

import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;

public class RelationsPanel extends AbstractDatasetModelPanel {

    private static final long serialVersionUID = 1003238896639997725L;

    public RelationsPanel(String wicketId, DatasetModel model) {
        super(wicketId, model);
        setOutputMarkupId(true);

        add(new CollectionPanel("relCollection", model));
        add(new RDFViewer("rdfViewer", model));
    }

}
