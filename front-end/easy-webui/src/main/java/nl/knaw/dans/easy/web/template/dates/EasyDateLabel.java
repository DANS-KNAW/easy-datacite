package nl.knaw.dans.easy.web.template.dates;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;

public class EasyDateLabel extends DateTimeLabel {
    private static final long serialVersionUID = 7627372072921690174L;

    public EasyDateLabel(String id, DateTime dateTime) {
        super(id, dateTime, new ResourceModel(EasyResources.DATE_FORMAY_KEY));
    }

}
