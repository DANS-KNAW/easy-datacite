package nl.knaw.dans.easy.domain.worker;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;

public class DefaultWorkListener implements WorkListener
{

	public void onWorkEnd()
	{
	}

	public boolean onWorkStart()
	{
		return false;
	}

	public void afterIngest(DataModelObject dmo)
	{
	}

	public void afterPurge(DataModelObject dmo)
	{
	}

	public void afterRetrieveObject(DataModelObject dmo)
	{
	}

	public void afterUpdate(DataModelObject dmo)
	{
	}

	public void afterUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit)
	{
	}

	public void onException(Throwable t)
	{
	}

	public boolean onIngest(DataModelObject dmo)
	{
		return false;
	}

	public boolean onPurge(DataModelObject dmo)
	{
		return false;
	}

	public boolean onUpdate(DataModelObject dmo)
	{
		return false;
	}

	public boolean onUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit)
	{
		return false;
	}

}
