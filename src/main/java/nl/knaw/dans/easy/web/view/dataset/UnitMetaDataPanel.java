package nl.knaw.dans.easy.web.view.dataset;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyStatelessPanel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitMetaDataPanel extends AbstractEasyStatelessPanel
{
	private static final long		serialVersionUID	= 3497222166400321697L;
	protected static final Logger	logger				= LoggerFactory.getLogger(UnitMetaDataPanel.class);

	// should match the columns in $ItemPanel.html
	private final static String[]	columns				= new String[] {"creationDate", "versionId"};

	private final DatasetModel	datasetModel;

	public UnitMetaDataPanel(final String wicketId, final List<UnitMetadata> versions, final DatasetModel datasetModel, final String unitId)
	{
		super(wicketId);
		this.datasetModel = datasetModel;
		final boolean showTable = versions != null && versions.size() > 0;
		if (versions != null && versions.size() > 1)
			Collections.sort(versions, new CompareByCreationDate());
		add(new WebMarkupContainer("none").setVisible(!showTable));
		add(new UnitMetaDataListView("versions", versions).setVisible(showTable));
	}

	private class CompareByCreationDate implements Comparator<UnitMetadata>
	{
		@Override
		public int compare(final UnitMetadata arg0, final UnitMetadata arg1)
		{
			return - arg0.getCreationDate().compareTo(arg1.getCreationDate());
		}
	}

	private final class UnitMetaDataListView extends ListView<UnitMetadata>
	{
		private static final long	serialVersionUID	= -6597598635055541684L;

		private UnitMetaDataListView(final String id, final List<? extends UnitMetadata> list)
		{
			super(id, list);
		}

		@Override
		protected void populateItem(final ListItem<UnitMetadata> item)
		{
			final IModel<UnitMetadata> model = new CompoundPropertyModel<UnitMetadata>(item.getDefaultModelObject());
			item.add(new ItemPanel("version", model));
		}
	}

	class ItemPanel extends AbstractEasyStatelessPanel
	{
		private static final long	serialVersionUID	= 7544583798689556606L;

		public ItemPanel(final String wicketId, final IModel<UnitMetadata> model)
		{
			super(wicketId, model);
			final UnitMetaDataResource resource = new UnitMetaDataResource(datasetModel, model.getObject());
			for (final String id : columns)
			{
				final Link<UnitMetadata> link = new ResourceLink<UnitMetadata>(id + "Link", resource);
				link.add(new Label(id));
				add(link);
			}
		}
	}
}
