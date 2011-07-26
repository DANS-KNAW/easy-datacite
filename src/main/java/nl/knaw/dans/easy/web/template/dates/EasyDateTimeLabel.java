package nl.knaw.dans.easy.web.template.dates;

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.easy.web.EasyResources;

import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;

public class EasyDateTimeLabel extends DateTimeLabel
{
	private static final long serialVersionUID = 8829625269109334314L;

	public EasyDateTimeLabel(String id, DateTime dateTime)
	{
		super(id, dateTime, new ResourceModel(EasyResources.DATETIME_FORMAY_KEY));
	}

}
